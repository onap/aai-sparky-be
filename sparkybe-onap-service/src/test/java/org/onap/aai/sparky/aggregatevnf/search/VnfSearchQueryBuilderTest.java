package org.onap.aai.sparky.aggregatevnf.search;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class VnfSearchQueryBuilderTest {

  private VnfSearchQueryBuilder vnfSearchQueryBuilder;

  @Before
    public void init() throws Exception {

    vnfSearchQueryBuilder = new VnfSearchQueryBuilder();

    }

  @SuppressWarnings("static-access")
  @Test
  public void updateValues() {



      assertNotNull(vnfSearchQueryBuilder.createSuggestionsQuery("10","queryString"));
      assertNotNull(vnfSearchQueryBuilder.getTermBlob("suggest-vnf","firewall"));
      assertNotNull(vnfSearchQueryBuilder.getSortCriteria("term","ascending"));

  }
}
