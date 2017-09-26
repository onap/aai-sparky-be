/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.aai.sparky.dal.cache;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import org.onap.aai.sparky.dal.aai.ActiveInventoryAdapter;
import org.onap.aai.sparky.dal.rest.OperationResult;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.synchronizer.task.PersistOperationResultToDisk;
import org.onap.aai.sparky.synchronizer.task.RetrieveOperationResultFromDisk;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class PersistentEntityCache.
 */
public class PersistentEntityCache implements EntityCache {

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(ActiveInventoryAdapter.class);

  /*
   * TODO: <li>implement time-to-live on the cache, maybe pull in one of Guava's eviction caches?
   * <li>implement abstract-base-cache to hold common cach-y things (like ttl)
   */

  private static final String DEFAULT_OUTPUT_PATH = "offlineEntityCache";
  private ExecutorService persistentExecutor;
  private ObjectMapper mapper;
  private String storagePath;

  /**
   * Instantiates a new persistent entity cache.
   */
  public PersistentEntityCache() {
    this(null, 10);
  }

  /**
   * Instantiates a new persistent entity cache.
   *
   * @param numWorkers the num workers
   */
  public PersistentEntityCache(int numWorkers) {
    this(null, numWorkers);
  }

  /**
   * Instantiates a new persistent entity cache.
   *
   * @param storageFolderOverride the storage folder override
   * @param numWorkers the num workers
   */
  public PersistentEntityCache(String storageFolderOverride, int numWorkers) {
    persistentExecutor = NodeUtils.createNamedExecutor("PEC", numWorkers, LOG);
    mapper = new ObjectMapper();

    if (storageFolderOverride != null && storageFolderOverride.length() > 0) {
      this.storagePath = storageFolderOverride;
    } else {
      this.storagePath = DEFAULT_OUTPUT_PATH;
    }
  }

  /**
   * Generate offline storage path from uri.
   *
   * @param link the link
   * @return the string
   */
  private String generateOfflineStoragePathFromUri(String link) {

    try {
      URI uri = new URI(link);

      String modHost = uri.getHost().replace(".", "_");

      String[] tokens = uri.getPath().split("\\/");
      List<String> resourcePathAndDomain = new ArrayList<String>();

      if (tokens.length >= 4) {

        int numElements = 0;
        for (String w : tokens) {

          if (numElements > 3) {
            break;
          }

          if (w.length() > 0) {
            resourcePathAndDomain.add(w);
            numElements++;
          }

        }
      } else {
        return this.storagePath + "\\";
      }

      return this.storagePath + "\\" + modHost + "\\"
          + NodeUtils.concatArray(resourcePathAndDomain, "_") + "\\";

    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.OFFLINE_STORAGE_PATH_ERROR, link, exc.getMessage());
    }

    return this.storagePath + "\\";

  }

  /**
   * Creates the dirs.
   *
   * @param directoryPath the directory path
   */
  private void createDirs(String directoryPath) {
    if (directoryPath == null) {
      return;
    }

    Path path = Paths.get(directoryPath);
    // if directory exists?
    if (!Files.exists(path)) {
      try {
        Files.createDirectories(path);
      } catch (IOException exc) {
        LOG.error(AaiUiMsgs.DISK_CREATE_DIR_IO_ERROR, exc.getMessage());
      }
    }

  }

  /* (non-Javadoc)
   * @see org.onap.aai.sparky.dal.cache.EntityCache#get(java.lang.String, java.lang.String)
   */
  @Override
  public OperationResult get(String key, String link) {

    final String storagePath = generateOfflineStoragePathFromUri(link);
    createDirs(storagePath);
    final String persistentFileName = storagePath + "\\" + key + ".json";

    CompletableFuture<OperationResult> task = supplyAsync(
        new RetrieveOperationResultFromDisk(persistentFileName, mapper, LOG), persistentExecutor);

    try {
      /*
       * this will do a blocking get, but it will be blocking only on the thread that executed this
       * method which should be one of the persistentWorker threads from the executor.
       */
      return task.get();
    } catch (InterruptedException | ExecutionException exc) {
      // TODO Auto-generated catch block
      LOG.error(AaiUiMsgs.DISK_NAMED_DATA_READ_IO_ERROR, "txn", exc.getMessage());
    }

    return null;

  }

  /* (non-Javadoc)
   * @see org.onap.aai.sparky.dal.cache.EntityCache#put(java.lang.String, org.onap.aai.sparky.dal.rest.OperationResult)
   */
  @Override
  public void put(String key, OperationResult data) {

    final String storagePath = generateOfflineStoragePathFromUri(data.getRequestLink());
    createDirs(storagePath);
    final String persistentFileName = storagePath + "\\" + key + ".json";

    Path persistentFilePath = Paths.get(persistentFileName);

    if (!Files.exists(persistentFilePath, LinkOption.NOFOLLOW_LINKS)) {

      supplyAsync(new PersistOperationResultToDisk(persistentFileName, data, mapper, LOG),
          persistentExecutor).whenComplete((opResult, error) -> {

            if (error != null) {
              LOG.error(AaiUiMsgs.DISK_DATA_WRITE_IO_ERROR, "entity", error.getMessage());
            }

          });
    }

  }


  /* (non-Javadoc)
   * @see org.onap.aai.sparky.dal.cache.EntityCache#shutdown()
   */
  @Override
  public void shutdown() {
    if (persistentExecutor != null) {
      persistentExecutor.shutdown();
    }

  }

  /* (non-Javadoc)
   * @see org.onap.aai.sparky.dal.cache.EntityCache#clear()
   */
  @Override
  public void clear() {
    /*
     * do nothing for this one, as it is not clear if we we really want to clear on the on-disk
     * cache or not
     */
  }

}
