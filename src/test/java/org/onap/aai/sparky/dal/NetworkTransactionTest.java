package org.onap.aai.sparky.dal;

import org.junit.Assert;
import org.junit.Test;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.dal.rest.HttpMethod;
import org.onap.aai.sparky.dal.rest.OperationResult;

public class NetworkTransactionTest {

  @Test
  public void testAllMethods() {
    NetworkTransaction ntt = new NetworkTransaction();
    ntt.setOperationType(HttpMethod.GET);
    Assert.assertEquals(HttpMethod.GET, ntt.getOperationType());

    ntt.setTaskAgeInMs();
    Assert.assertNotNull(ntt.getTaskAgeInMs());

    ntt.setOperationResult(new OperationResult());
    Assert.assertNotNull(ntt.getOperationResult());

    ntt.setEntityType("entity");
    Assert.assertEquals(ntt.getEntityType(), "entity");

    ntt.setLink("link");
    Assert.assertEquals(ntt.getLink(), "link");

    ntt.setDescriptor(new OxmEntityDescriptor());
    Assert.assertNotNull(ntt.getDescriptor());

    Assert.assertNotNull(ntt.toString());
  }


}
