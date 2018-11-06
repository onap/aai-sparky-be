package org.onap.aai.sparky.personalization.config;


import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class PersonalizationConfigTest {

  
  private PersonalizationConfig personalizationConfig;
  

  @Before
  public void init() throws Exception {
    personalizationConfig = new PersonalizationConfig();
  
  }


  @Test
  public void updateValues() {

    personalizationConfig.setHtmlDocumentTitle("AAI");
    assertNotNull(personalizationConfig.getHtmlDocumentTitle());
    personalizationConfig.setTopLeftHeader("AAI");
    assertNotNull(personalizationConfig.getTopLeftHeader());
 
  }

}
