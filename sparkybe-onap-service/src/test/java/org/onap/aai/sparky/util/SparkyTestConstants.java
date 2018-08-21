package org.onap.aai.sparky.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SparkyTestConstants {

  /** Default to unix file separator if system property file.separator is null */
  public static final String FILESEP =
      (System.getProperty("file.separator") == null) ? "/" : System.getProperty("file.separator");
  
  private static Path currentRelativePath = Paths.get("");
  public static final String PATH_TO_TEST_RESOURCES = currentRelativePath.toAbsolutePath().toString()
      + FILESEP + "src" + FILESEP + "test" + FILESEP + "resources";
  
  public static final String PATH_TO_FILTERS_CONFIG = PATH_TO_TEST_RESOURCES + FILESEP + "filters";
  public static final String FILTERS_JSON_FILE = "file:" + PATH_TO_FILTERS_CONFIG + FILESEP + "aaiui_filters.json";
  public static final String VIEWS_JSON_FILE = "file:" + PATH_TO_FILTERS_CONFIG + FILESEP + "aaiui_views.json";
}
