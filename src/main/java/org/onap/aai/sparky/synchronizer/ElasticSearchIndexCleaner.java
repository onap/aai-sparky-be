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
package org.onap.aai.sparky.synchronizer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.onap.aai.sparky.dal.rest.OperationResult;
import org.onap.aai.sparky.dal.rest.RestDataProvider;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.synchronizer.entity.ObjectIdCollection;
import org.onap.aai.sparky.synchronizer.entity.SearchableEntity;
import org.onap.aai.sparky.synchronizer.enumeration.OperationState;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;

/**
 * The Class ElasticSearchIndexCleaner.
 */
public class ElasticSearchIndexCleaner implements IndexCleaner {

  private static final Logger LOG =
      LoggerFactory.getInstance().getLogger(ElasticSearchIndexCleaner.class);

  private static final String BULK_OP_LINE_TEMPLATE = "%s\n";
  private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

  private ObjectIdCollection before;
  private ObjectIdCollection after;

  private String host;
  private String port;

  private String indexName;
  private String indexType;
  private int scrollContextTimeToLiveInMinutes;
  private int numItemsToGetBulkRequest;

  private RestDataProvider restDataProvider;
  private ObjectMapper mapper;

  /**
   * Instantiates a new elastic search index cleaner.
   *
   * @param restDataProvider the rest data provider
   * @param indexName the index name
   * @param indexType the index type
   * @param host the host
   * @param port the port
   * @param scrollContextTimeToLiveInMinutes the scroll context time to live in minutes
   * @param numItemsToGetBulkRequest the num items to get bulk request
   */
  protected ElasticSearchIndexCleaner(RestDataProvider restDataProvider, String indexName,
      String indexType, String host, String port, int scrollContextTimeToLiveInMinutes,
      int numItemsToGetBulkRequest) {
    this.restDataProvider = restDataProvider;
    this.before = null;
    this.after = null;
    this.indexName = indexName;
    this.indexType = indexType;
    this.mapper = new ObjectMapper();
    this.host = host;
    this.port = port;
    this.scrollContextTimeToLiveInMinutes = scrollContextTimeToLiveInMinutes;
    this.numItemsToGetBulkRequest = numItemsToGetBulkRequest;
  }

  /* (non-Javadoc)
   * @see org.onap.aai.sparky.synchronizer.IndexCleaner#populatePreOperationCollection()
   */
  @Override
  public OperationState populatePreOperationCollection() {

    try {
      before = retrieveAllDocumentIdentifiers();
      return OperationState.OK;
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ES_PRE_SYNC_FAILURE, indexName, exc.getMessage());
      return OperationState.ERROR;
    }

  }

  /* (non-Javadoc)
   * @see org.onap.aai.sparky.synchronizer.IndexCleaner#populatePostOperationCollection()
   */
  @Override
  public OperationState populatePostOperationCollection() {
    try {
      after = retrieveAllDocumentIdentifiers();
      return OperationState.OK;
    } catch (Exception exc) {
      LOG.error(AaiUiMsgs.ES_PRE_SYNC_FAILURE, indexName, exc.getMessage());
      return OperationState.ERROR;
    }
  }

  /* (non-Javadoc)
   * @see org.onap.aai.sparky.synchronizer.IndexCleaner#performCleanup()
   */
  @Override
  public OperationState performCleanup() {
    // TODO Auto-generated method stub
    LOG.info(AaiUiMsgs.ES_SYNC_CLEAN_UP, indexName);

    int sizeBefore = before.getSize();
    int sizeAfter = after.getSize();

    LOG.info(AaiUiMsgs.ES_SYNC_CLEAN_UP_SIZE, String.valueOf(sizeBefore),
        String.valueOf(sizeAfter));

    /*
     * If the processedImportIds size <= 0, then something has failed in the sync operation and we
     * shouldn't do the selective delete right now.
     */

    if (sizeAfter > 0) {

      Collection<String> presyncIds = before.getImportedObjectIds();
      presyncIds.removeAll(after.getImportedObjectIds());

      try {
        LOG.info(AaiUiMsgs.ES_SYNC_SELECTIVE_DELETE, indexName, indexType,
            String.valueOf(presyncIds.size()));

        ObjectIdCollection bulkIds = new ObjectIdCollection();

        Iterator<String> it = presyncIds.iterator();
        int numItemsInBulkRequest = 0;
        int numItemsRemainingToBeDeleted = presyncIds.size();

        while (it.hasNext()) {

          bulkIds.addObjectId(it.next());
          numItemsInBulkRequest++;

          if (numItemsInBulkRequest >= this.numItemsToGetBulkRequest) {
            LOG.info(AaiUiMsgs.ES_BULK_DELETE, indexName, String.valueOf(bulkIds.getSize()));
            OperationResult bulkDeleteResult = bulkDelete(bulkIds.getImportedObjectIds());
            // pegCountersForElasticBulkDelete(bulkDeleteResult);
            numItemsRemainingToBeDeleted -= numItemsInBulkRequest;
            numItemsInBulkRequest = 0;
            bulkIds.clear();
          }
        }

        if (numItemsRemainingToBeDeleted > 0) {
          LOG.info(AaiUiMsgs.ES_BULK_DELETE, indexName, String.valueOf(bulkIds.getSize()));
          OperationResult bulkDeleteResult = bulkDelete(bulkIds.getImportedObjectIds());
          // pegCountersForElasticBulkDelete(bulkDeleteResult);
        }


      } catch (Exception exc) {
        LOG.error(AaiUiMsgs.ES_BULK_DELETE_ERROR, indexName, exc.getLocalizedMessage());

      }
    }

    return OperationState.OK;
  }

  @Override
  public String getIndexName() {
    return indexName;
  }

  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }

  /**
   * Builds the initial scroll request payload.
   *
   * @param numItemsToGetPerRequest the num items to get per request
   * @param fieldList the field list
   * @return the string
   * @throws JsonProcessingException the json processing exception
   */
  protected String buildInitialScrollRequestPayload(int numItemsToGetPerRequest,
      List<String> fieldList) throws JsonProcessingException {

    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.put("size", numItemsToGetPerRequest);

    ArrayNode fields = mapper.createArrayNode();

    for (String f : fieldList) {
      fields.add(f);
    }

    rootNode.set("fields", fields);

    ObjectNode queryNode = mapper.createObjectNode();
    queryNode.set("match_all", mapper.createObjectNode());

    rootNode.set("query", queryNode);

    return mapper.writeValueAsString(rootNode);

  }

  /**
   * Builds the subsequent scroll context request payload.
   *
   * @param scrollId the scroll id
   * @param contextTimeToLiveInMinutes the context time to live in minutes
   * @return the string
   * @throws JsonProcessingException the json processing exception
   */
  protected String buildSubsequentScrollContextRequestPayload(String scrollId,
      int contextTimeToLiveInMinutes) throws JsonProcessingException {

    ObjectNode rootNode = mapper.createObjectNode();

    rootNode.put("scroll", contextTimeToLiveInMinutes + "m");
    rootNode.put("scroll_id", scrollId);

    return mapper.writeValueAsString(rootNode);

  }

  /**
   * Parses the elastic search result.
   *
   * @param jsonResult the json result
   * @return the json node
   * @throws JsonProcessingException the json processing exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  protected JsonNode parseElasticSearchResult(String jsonResult)
      throws JsonProcessingException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readTree(jsonResult);
  }

  /**
   * Lookup index doc.
   *
   * @param ids the ids
   * @param docs the docs
   * @return the array list
   */
  protected ArrayList<SearchableEntity> lookupIndexDoc(ArrayList<String> ids,
      List<SearchableEntity> docs) {
    ArrayList<SearchableEntity> objs = new ArrayList<SearchableEntity>();

    if (ids != null && docs != null) {
      for (SearchableEntity d : docs) {
        if (ids.contains(d.getId())) {
          objs.add(d);
        }
      }
    }

    return objs;
  }

  /**
   * Builds the delete data object.
   *
   * @param index the index
   * @param type the type
   * @param id the id
   * @return the object node
   */
  protected ObjectNode buildDeleteDataObject(String index, String type, String id) {

    ObjectNode indexDocProperties = mapper.createObjectNode();

    indexDocProperties.put("_index", index);
    indexDocProperties.put("_type", type);
    indexDocProperties.put("_id", id);

    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.set("delete", indexDocProperties);

    return rootNode;
  }

  /**
   * This method might appear to be a little strange, and is simply an optimization to take an
   * elipsed JsonNode key path and retrieve the node at the end of the path, if it exists.
   *
   * @param startNode the start node
   * @param fieldPath the field path
   * @return the node path
   */
  protected JsonNode getNodePath(JsonNode startNode, String... fieldPath) {

    JsonNode jsonNode = null;

    for (String field : fieldPath) {
      if (jsonNode == null) {
        jsonNode = startNode.get(field);
      } else {
        jsonNode = jsonNode.get(field);
      }

      /*
       * This is our safety net in case any intermediate path returns a null
       */

      if (jsonNode == null) {
        return null;
      }

    }

    return jsonNode;
  }

  /**
   * Gets the full url.
   *
   * @param resourceUrl the resource url
   * @return the full url
   */
  private String getFullUrl(String resourceUrl) {
    return String.format("http://%s:%s%s", host, port, resourceUrl);
  }

  /**
   * Retrieve all document identifiers.
   *
   * @return the object id collection
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public ObjectIdCollection retrieveAllDocumentIdentifiers() throws IOException {

    ObjectIdCollection currentDocumentIds = new ObjectIdCollection();

    long opStartTimeInMs = System.currentTimeMillis();

    List<String> fields = new ArrayList<String>();
    fields.add("_id");
    // fields.add("entityType");

    String scrollRequestPayload =
        buildInitialScrollRequestPayload(this.numItemsToGetBulkRequest, fields);

    final String fullUrlStr = getFullUrl("/" + indexName + "/" + indexType + "/_search?scroll="
        + this.scrollContextTimeToLiveInMinutes + "m");

    OperationResult result =
        restDataProvider.doPost(fullUrlStr, scrollRequestPayload, "application/json");

    if (result.wasSuccessful()) {

      JsonNode rootNode = parseElasticSearchResult(result.getResult());

      /*
       * Check the result for success / failure, and enumerate all the index ids that resulted in
       * success, and ignore the ones that failed or log them so we have a record of the failure.
       */
      int totalRecordsAvailable = 0;
      String scrollId = null;
      int numRecordsFetched = 0;

      if (rootNode != null) {

        scrollId = getFieldValue(rootNode, "_scroll_id");
        final String tookStr = getFieldValue(rootNode, "took");
        int tookInMs = (tookStr == null) ? 0 : Integer.parseInt(tookStr);
        boolean timedOut = Boolean.parseBoolean(getFieldValue(rootNode, "timed_out"));

        if (timedOut) {
          LOG.error(AaiUiMsgs.COLLECT_TIME_WITH_ERROR, "all document Identifiers",
              String.valueOf(tookInMs));
        } else {
          LOG.info(AaiUiMsgs.COLLECT_TIME_WITH_SUCCESS, "all document Identifiers",
              String.valueOf(tookInMs));
        }

        JsonNode hitsNode = rootNode.get("hits");
        totalRecordsAvailable = Integer.parseInt(hitsNode.get("total").asText());

        LOG.info(AaiUiMsgs.COLLECT_TOTAL, "all document Identifiers",
            String.valueOf(totalRecordsAvailable));

        /*
         * Collect all object ids
         */

        ArrayNode hitsArray = (ArrayNode) hitsNode.get("hits");

        Iterator<JsonNode> nodeIterator = hitsArray.iterator();

        String key = null;
        String value = null;
        JsonNode jsonNode = null;

        while (nodeIterator.hasNext()) {

          jsonNode = nodeIterator.next();

          key = getFieldValue(jsonNode, "_id");

          if (key != null) {
            currentDocumentIds.addObjectId(key);
          }

          /*
           * if (key != null) {
           * 
           * JsonNode fieldsNode = jNode.get("fields");
           * 
           * if (fieldsNode != null) {
           * 
           * JsonNode entityTypeNode = fieldsNode.get("entityType");
           * 
           * if (entityTypeNode != null) { ArrayNode aNode = (ArrayNode) entityTypeNode;
           * 
           * if (aNode.size() > 0) { value = aNode.get(0).asText(); objAndtTypesMap.put(key, value);
           * numRecordsFetched++; } } } }
           */

        }

        int totalRecordsRemainingToFetch = (totalRecordsAvailable - numRecordsFetched);

        int numRequiredAdditionalFetches =
            (totalRecordsRemainingToFetch / this.numItemsToGetBulkRequest);

        /*
         * Do an additional fetch for the remaining items (if needed)
         */

        if (totalRecordsRemainingToFetch % numItemsToGetBulkRequest != 0) {
          numRequiredAdditionalFetches += 1;
        }

        if (LOG.isDebugEnabled()) {
          LOG.debug(AaiUiMsgs.SYNC_NUMBER_REQ_FETCHES,
              String.valueOf(numRequiredAdditionalFetches));
        }


        for (int x = 0; x < numRequiredAdditionalFetches; x++) {

          if (collectItemsFromScrollContext(scrollId, currentDocumentIds) != OperationState.OK) {
            // abort the whole thing because now we can't reliably cleanup the orphans.
            throw new IOException(
                "Failed to collect pre-sync doc collection from index.  Aborting operation");
          }
          if (LOG.isDebugEnabled()) {
            LOG.debug(AaiUiMsgs.SYNC_NUMBER_TOTAL_FETCHES,
                String.valueOf(currentDocumentIds.getSize()),
                String.valueOf(totalRecordsAvailable));
          }

        }

      }

    } else {
      // scroll context get failed, nothing else to do
      LOG.error(AaiUiMsgs.ERROR_GENERIC, result.toString());
    }

    LOG.info(AaiUiMsgs.COLLECT_TOTAL_TIME, "all document Identifiers",
        String.valueOf((System.currentTimeMillis() - opStartTimeInMs)));

    return currentDocumentIds;

  }

  /**
   * Collect items from scroll context.
   *
   * @param scrollId the scroll id
   * @param objectIds the object ids
   * @return the operation state
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private OperationState collectItemsFromScrollContext(String scrollId,
      ObjectIdCollection objectIds) throws IOException {

    // ObjectIdCollection documentIdCollection = new ObjectIdCollection();

    String requestPayload =
        buildSubsequentScrollContextRequestPayload(scrollId, scrollContextTimeToLiveInMinutes);

    final String fullUrlStr = getFullUrl("/_search/scroll");

    OperationResult opResult =
        restDataProvider.doPost(fullUrlStr, requestPayload, "application/json");

    if (opResult.getResultCode() >= 300) {
      LOG.warn(AaiUiMsgs.ES_SCROLL_CONTEXT_ERROR, opResult.getResult());
      return OperationState.ERROR;
    }

    JsonNode rootNode = parseElasticSearchResult(opResult.getResult());

    /*
     * Check the result for success / failure, and enumerate all the index ids that resulted in
     * success, and ignore the ones that failed or log them so we have a record of the failure.
     */

    if (rootNode != null) {
      boolean timedOut = Boolean.parseBoolean(getFieldValue(rootNode, "timed_out"));
      final String tookStr = getFieldValue(rootNode, "took");
      int tookInMs = (tookStr == null) ? 0 : Integer.parseInt(tookStr);

      JsonNode hitsNode = rootNode.get("hits");

      if (timedOut) {
        LOG.info(AaiUiMsgs.COLLECT_TIME_WITH_ERROR, "Scroll Context", String.valueOf(tookInMs));
      } else {
        LOG.info(AaiUiMsgs.COLLECT_TIME_WITH_SUCCESS, "Scroll Context", String.valueOf(tookInMs));
      }

      /*
       * Collect all object ids
       */

      ArrayNode hitsArray = (ArrayNode) hitsNode.get("hits");
      String key = null;
      String value = null;
      JsonNode jsonNode = null;

      Iterator<JsonNode> nodeIterator = hitsArray.iterator();

      while (nodeIterator.hasNext()) {

        jsonNode = nodeIterator.next();

        key = getFieldValue(jsonNode, "_id");

        if (key != null) {
          objectIds.addObjectId(key);

          /*
           * JsonNode fieldsNode = jNode.get("fields");
           * 
           * if (fieldsNode != null) {
           * 
           * JsonNode entityTypeNode = fieldsNode.get("entityType");
           * 
           * if (entityTypeNode != null) { ArrayNode aNode = (ArrayNode) entityTypeNode;
           * 
           * if (aNode.size() > 0) { value = aNode.get(0).asText(); objectIdsAndTypes.put(key,
           * value); } } } }
           */

        }

      }
    } else {
      // scroll context get failed, nothing else to do
      LOG.error(AaiUiMsgs.ERROR_GENERIC, opResult.toString());
    }

    return OperationState.OK;
  }

  /**
   * Gets the field value.
   *
   * @param node the node
   * @param fieldName the field name
   * @return the field value
   */
  protected String getFieldValue(JsonNode node, String fieldName) {

    JsonNode field = node.get(fieldName);

    if (field != null) {
      return field.asText();
    }

    return null;

  }

  /**
   * Bulk delete.
   *
   * @param docIds the doc ids
   * @return the operation result
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public OperationResult bulkDelete(Collection<String> docIds) throws IOException {

    if (docIds == null || docIds.size() == 0) {
      LOG.info(AaiUiMsgs.ES_BULK_DELETE_SKIP);
      return new OperationResult(500,
          "Skipping bulkDelete(); operation because docs to delete list is empty");
    }

    LOG.info(AaiUiMsgs.ES_BULK_DELETE_START, String.valueOf(docIds.size()));

    StringBuilder sb = new StringBuilder(128);

    for (String id : docIds) {
      sb.append(
          String.format(BULK_OP_LINE_TEMPLATE, buildDeleteDataObject(indexName, indexType, id)));
    }

    sb.append("\n");

    final String fullUrlStr = getFullUrl("/_bulk");

    return restDataProvider.doPost(fullUrlStr, sb.toString(), "application/x-www-form-urlencoded");

  }

  /*
  
  */

}
