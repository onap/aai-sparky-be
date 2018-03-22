package org.onap.aai.sparky.viewandinspect;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.config.oxm.OxmEntityLookup;
import org.onap.aai.sparky.subscription.config.SubscriptionConfig;
import org.onap.aai.sparky.util.OxmModelAndProcessorHelper;
import org.onap.aai.sparky.viewandinspect.config.VisualizationConfigs;
import org.onap.aai.sparky.viewandinspect.entity.ActiveInventoryNode;
import org.onap.aai.sparky.viewandinspect.entity.GraphMeta;
import org.onap.aai.sparky.viewandinspect.entity.SparkyGraphNode;
import org.onap.aai.sparky.viewandinspect.services.VisualizationTransformer;
import org.onap.aai.sparky.viewandinspect.util.SchemaVisualizationTestDataBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VisualizationTransformerTest {

  VisualizationTransformer testTransformer;
  VisualizationConfigs visConfig;
  SubscriptionConfig subConfig;
  GraphMeta graphMeta;
  OxmEntityLookup oxmEntityLookup;
  
  
  @Before
  public void init() throws Exception {
    this.visConfig = new VisualizationConfigs();
    this.subConfig = new SubscriptionConfig();
    this.graphMeta = new GraphMeta();
    this.oxmEntityLookup = OxmModelAndProcessorHelper.getInstance().getOxmEntityLookup();
   
    this.testTransformer = new VisualizationTransformer(visConfig, subConfig);
  }
  
  @Test
  public void testGenerateVisualizationOutput() throws JsonParseException, JsonMappingException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      JsonNode elasticValue = mapper.readValue(SchemaVisualizationTestDataBuilder.getRawRootNode(), JsonNode.class);

  }

  public ActiveInventoryNode generateActiveInventoryNode(String id, String type, String selfLink, String primaryKeyName, String primaryKeyValue) {
    ActiveInventoryNode testAin = new ActiveInventoryNode(visConfig, oxmEntityLookup);
    
    testAin.setNodeId(id);
    testAin.setEntityType(type);
    testAin.setSelfLink(selfLink);
    testAin.setPrimaryKeyName(primaryKeyName);
    testAin.setPrimaryKeyValue(primaryKeyValue);
    
    return testAin;
  }
  
  public SparkyGraphNode generateSparkyGraphNode(ActiveInventoryNode ain) {
    
    SparkyGraphNode testSparkyGraphNode = new SparkyGraphNode(ain, visConfig, subConfig);
    
    return testSparkyGraphNode;
  }
  
  public Map<String, ActiveInventoryNode> generateFlatNodeArray() {
    Map<String, ActiveInventoryNode> nodeArray = new HashMap<String, ActiveInventoryNode>();
    
    return nodeArray;
  }
}
