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
package org.onap.aai.sparky.viewandinspect.services;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.sparky.config.oxm.OxmEntityDescriptor;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.aai.ActiveInventoryAdapter;
import org.onap.aai.sparky.dal.aai.ActiveInventoryDataProvider;
import org.onap.aai.sparky.dal.exception.ElasticSearchOperationException;
import org.onap.aai.sparky.dal.rest.RestClientBuilder;
import org.onap.aai.sparky.synchronizer.entity.SearchableEntity;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;
import org.onap.aai.sparky.viewandinspect.entity.ActiveInventoryNode;
import org.onap.aai.sparky.viewandinspect.entity.QueryParams;
import org.onap.aai.sparky.viewandinspect.enumeration.NodeProcessingState;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class VisualizationContextTest {

    VisualizationContext context;
    ActiveInventoryDataProvider dataProvider;
    long contextId;
    OxmModelLoader oxmModelLoader;
    ExecutorService service;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        System.setProperty("AJSC_HOME", new File(".").getCanonicalPath().replace('\\', '/'));
        TierSupportUiConstants.DYNAMIC_CONFIG_APP_LOCATION = System.getProperty("AJSC_HOME")+"/src/test/resources/appconfig/";
        TierSupportUiConstants.CONFIG_OXM_LOCATION = System.getProperty("AJSC_HOME")+"/bundleconfig-local/oxm/";
        oxmModelLoader = Mockito.spy(OxmModelLoader.getInstance());
        ActiveInventoryDataProvider dataProvider = new ActiveInventoryAdapter(new RestClientBuilder());
        SecureRandom random = new SecureRandom();
        long contextId = random.nextLong();
        ExecutorService service = NodeUtils.createNamedExecutor("SLNC-WORKER",
                2, LoggerFactory.getInstance().getLogger(VisualizationContextTest.class));
        context = new VisualizationContext(contextId, dataProvider, service, oxmModelLoader);
    }

    @Test
    public void testExtractQueryParamsFromSelfLink_NullLink() {
        List<String> queryParams = context.extractQueryParamsFromSelfLink(null);
        Assert.assertEquals(queryParams.size(), 0);
    }

    @Test
    public void testExtractQueryParamsFromSelfLink_NotNullLink() {
        context.extractQueryParamsFromSelfLink("https://localhost:9517/model/aai/webapp/index.html");
    }

    @Test
    public void testDecodeComplexAttributeGroup_NullAttributeGroup() {
        ActiveInventoryNode ain = new ActiveInventoryNode();
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        boolean retValue = context.decodeComplexAttributeGroup(ain, node);
        Assert.assertFalse(retValue);
    }

    @Test
    public void testDecodeComplexAttributeGroup_NotNullAttributeGroup() {
        ActiveInventoryNode ain = new ActiveInventoryNode();
        ObjectNode node1 = JsonNodeFactory.instance.objectNode();
        ObjectNode node2 = JsonNodeFactory.instance.objectNode();
        node1.set("model", node2);
        ObjectNode modelNode = JsonNodeFactory.instance.objectNode();
        node2.set("model-1", modelNode);
        modelNode.set("firstValue", JsonNodeFactory.instance.objectNode());
        modelNode.set("secondValue", JsonNodeFactory.instance.objectNode());
        modelNode.set("thirdValue", JsonNodeFactory.instance.objectNode());
        boolean retValue = context.decodeComplexAttributeGroup(ain, node1);
        Assert.assertFalse(retValue);
    }

    @Test
    public void testProcessSelfLinks() {
        SearchableEntity entity = new SearchableEntity();
        entity.setId("id-1");
        entity.setEntityType("cloud-region");
        entity.setEntityPrimaryKeyValue("cloud-region-1");
        entity.setLink("https://localhost:9517/model/aai/webapp/index.html");
        QueryParams params = new QueryParams();
        params.setSearchTargetNodeId("id-1");
        Map<String, ActiveInventoryNode> nodeMap =  context.getNodeCache();
        ActiveInventoryNode node = new ActiveInventoryNode();
        node.setEntityType("cloud-region");
        node.setNodeId("id-1");
        node.setPrimaryKeyName("cloud-region-1");
        node.setPrimaryKeyValue("cloud-region-1");
        node.addQueryParam(
        node.getEntityType() + "." + node.getPrimaryKeyName() + ":" + node.getPrimaryKeyValue());
        node.setNodeDepth(0);
        node.setRootNode(true);
        node.setSelfLink(entity.getLink());
        nodeMap.put("id-1", node);
        node.setResolvedSelfLink(true);
        context.processSelfLinks(entity, params);

    }
}
