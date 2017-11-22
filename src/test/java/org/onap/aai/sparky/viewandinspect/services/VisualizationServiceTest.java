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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.viewandinspect.config.TierSupportUiConstants;
import org.onap.aai.sparky.viewandinspect.entity.QueryRequest;

public class VisualizationServiceTest {

	VisualizationService service;
	
	@Before
	public void init() throws Exception {
		System.setProperty("AJSC_HOME", new File(".").getCanonicalPath().replace('\\', '/'));
	    TierSupportUiConstants.DYNAMIC_CONFIG_APP_LOCATION = System.getProperty("AJSC_HOME")+"/src/test/resources/appconfig/";
	    //TierSupportUiConstants.CONFIG_OXM_LOCATION = System.getProperty("AJSC_HOME")+"/bundleconfig-local/oxm/";
	    TierSupportUiConstants.STATIC_CONFIG_APP_LOCATION = System.getProperty("AJSC_HOME")+"/bundleconfig-local/etc/appprops";
		OxmModelLoader loader = OxmModelLoader.getInstance();
		service = new VisualizationService(loader);
	}
	
	@Test
	public void testAnalyzeQueryRequestBody_InvalidQuery() {
		QueryRequest request = service.analyzeQueryRequestBody("query-json-1");
		Assert.assertNull(request);
	}
	
	@Test
	public void testAnalyzeQueryRequestBody_ValidQuery() {
		QueryRequest request = service.analyzeQueryRequestBody("{\"hashId\":\"hashid-1\"}");
		Assert.assertNotNull(request);
	}
	@Test
	public void testBuildVisualizationUsingGenericQuery_UnsuccessfulOperationResult() {
		QueryRequest queryRequest = new QueryRequest();
		queryRequest.setHashId("hash-id-1");
		service.buildVisualizationUsingGenericQuery(queryRequest);
	}

	String queryRequest = "{\r\n" + 
			"	\"variables\": [],\r\n" + 
			"	\"info\": {\r\n" + 
			"		\"name\": \"NamedQuery\",\r\n" + 
			"		\"_postman_id\": \"8006823d-35ce-16ef-88e9-cd5b873c9e7b\",\r\n" + 
			"		\"schema\": \"https://schema.getpostman.com/json/collection/v2.0.0/collection.json\"\r\n" + 
			"	},\r\n" + 
			"	\"item\": [\r\n" + 
			"		{\r\n" + 
			"			\"name\": \"https://127.0.0.1:8443/aai/v11/service-design-and-creation/named-queries/named-query/0367193e-c785-4d5f-9cb8-7bc89dc9ddb7\",\r\n" + 
			"			\"request\": {\r\n" + 
			"				\"url\": \"https://127.0.0.1:8443/aai/v11/service-design-and-creation/named-queries/named-query/0367193e-c785-4d5f-9cb8-7bc89dc9ddb7\",\r\n" + 
			"				\"method\": \"POST\",\r\n" + 
			"				\"header\": [\r\n" + 
			"					{\r\n" + 
			"						\"key\": \"Content-Type\",\r\n" + 
			"						\"value\": \"application/json\",\r\n" + 
			"						\"description\": \"\"\r\n" + 
			"					},\r\n" + 
			"					{\r\n" + 
			"						\"key\": \"X-TransactionId\",\r\n" + 
			"						\"value\": \"9999\",\r\n" + 
			"						\"description\": \"\"\r\n" + 
			"					},\r\n" + 
			"					{\r\n" + 
			"						\"key\": \"X-FromAppId\",\r\n" + 
			"						\"value\": \"jimmy-postman\",\r\n" + 
			"						\"description\": \"\"\r\n" + 
			"					},\r\n" + 
			"					{\r\n" + 
			"						\"key\": \"Authorization\",\r\n" + 
			"						\"value\": \"Basic QUFJOkFBSQ==\",\r\n" + 
			"						\"description\": \"\"\r\n" + 
			"					}\r\n" + 
			"				],\r\n" + 
			"				\"body\": {\r\n" + 
			"					\"mode\": \"raw\",\r\n" + 
			"					\"raw\": \"{\\n   \\\"named-query-uuid\\\" : \\\"0367193e-c785-4d5f-9cb8-7bc89dc9ddb7\\\",\\n   \\\"named-query-name\\\" : \\\"get-component-list\\\",\\n   \\\"named-query-version\\\" : \\\"1.1\\\",\\n   \\\"description\\\" : \\\"Named Query - Get Component List\\\",\\n   \\\"named-query-elements\\\" : {\\n      \\\"named-query-element\\\" : [ {\\n         \\\"property-collect-list\\\" : [ \\\"service-instance-id\\\", \\\"service-instance-name\\\" ],\\n         \\\"named-query-elements\\\" : {\\n            \\\"named-query-element\\\" : [ {\\n               \\\"named-query-elements\\\" : {\\n                  \\\"named-query-element\\\" : [ {\\n                     \\\"relationship-list\\\" : {\\n                        \\\"relationship\\\" : [ {\\n                           \\\"related-to\\\" : \\\"model\\\",\\n                           \\\"relationship-data\\\" : [ {\\n                              \\\"relationship-key\\\" : \\\"model.model-invariant-id\\\",\\n                              \\\"relationship-value\\\" : \\\"1b2c9ba7-e449-4831-ba15-3073672f5ef2\\\"\\n                           } ]\\n                        } ]\\n                     }\\n                  } ]\\n               },\\n               \\\"relationship-list\\\" : {\\n                  \\\"relationship\\\" : [ {\\n                     \\\"related-to\\\" : \\\"model\\\",\\n                     \\\"relationship-data\\\" : [ {\\n                        \\\"relationship-key\\\" : \\\"model.model-invariant-id\\\",\\n                        \\\"relationship-value\\\" : \\\"3d560d81-57d0-438b-a2a1-5334dba0651a\\\"\\n                     } ]\\n                  } ]\\n               }\\n            }, {\\n               \\\"named-query-elements\\\" : {\\n                  \\\"named-query-element\\\" : [ {\\n                     \\\"relationship-list\\\" : {\\n                        \\\"relationship\\\" : [ {\\n                           \\\"related-to\\\" : \\\"model\\\",\\n                           \\\"relationship-data\\\" : [ {\\n                              \\\"relationship-key\\\" : \\\"model.model-invariant-id\\\",\\n                              \\\"relationship-value\\\" : \\\"fcec1b02-b2d0-4834-aef8-d71be04717dd\\\"\\n                           } ]\\n                        } ]\\n                     }\\n                  }, {\\n                     \\\"named-query-elements\\\" : {\\n                        \\\"named-query-element\\\" : [ {\\n                           \\\"relationship-list\\\" : {\\n                              \\\"relationship\\\" : [ {\\n                                 \\\"related-to\\\" : \\\"model\\\",\\n                                 \\\"relationship-data\\\" : [ {\\n                                    \\\"relationship-key\\\" : \\\"model.model-invariant-id\\\",\\n                                    \\\"relationship-value\\\" : \\\"ff69d4e0-a8e8-4108-bdb0-dd63217e63c7\\\"\\n                                 } ]\\n                              } ]\\n                           }\\n                        }, {\\n                           \\\"relationship-list\\\" : {\\n                              \\\"relationship\\\" : [ {\\n                                 \\\"related-to\\\" : \\\"model\\\",\\n                                 \\\"relationship-data\\\" : [ {\\n                                    \\\"relationship-key\\\" : \\\"model.model-invariant-id\\\",\\n                                    \\\"relationship-value\\\" : \\\"fcec1b02-b2d0-4834-aef8-d71be04717dd\\\"\\n                                 } ]\\n                              } ]\\n                           }\\n                        }, {\\n                           \\\"named-query-elements\\\" : {\\n                              \\\"named-query-element\\\" : [ {\\n                                 \\\"relationship-list\\\" : {\\n                                    \\\"relationship\\\" : [ {\\n                                       \\\"related-to\\\" : \\\"model\\\",\\n                                       \\\"relationship-data\\\" : [ {\\n                                          \\\"relationship-key\\\" : \\\"model.model-invariant-id\\\",\\n                                          \\\"relationship-value\\\" : \\\"1b2c9ba7-e449-4831-ba15-3073672f5ef2\\\"\\n                                       } ]\\n                                    } ]\\n                                 }\\n                              } ]\\n                           },\\n                           \\\"relationship-list\\\" : {\\n                              \\\"relationship\\\" : [ {\\n                                 \\\"related-to\\\" : \\\"model\\\",\\n                                 \\\"relationship-data\\\" : [ {\\n                                    \\\"relationship-key\\\" : \\\"model.model-invariant-id\\\",\\n                                    \\\"relationship-value\\\" : \\\"3d560d81-57d0-438b-a2a1-5334dba0651a\\\"\\n                                 } ]\\n                              } ]\\n                           }\\n                        } ]\\n                     },\\n                     \\\"relationship-list\\\" : {\\n                        \\\"relationship\\\" : [ {\\n                           \\\"related-to\\\" : \\\"model\\\",\\n                           \\\"relationship-data\\\" : [ {\\n                              \\\"relationship-key\\\" : \\\"model.model-invariant-id\\\",\\n                              \\\"relationship-value\\\" : \\\"ef86f9c5-2165-44f3-8fc3-96018b609ea5\\\"\\n                           } ]\\n                        } ]\\n                     }\\n                  } ]\\n               },\\n               \\\"relationship-list\\\" : {\\n                  \\\"relationship\\\" : [ {\\n                     \\\"related-to\\\" : \\\"model\\\",\\n                     \\\"relationship-data\\\" : [ {\\n                        \\\"relationship-key\\\" : \\\"model.model-invariant-id\\\",\\n                        \\\"relationship-value\\\" : \\\"acc6edd8-a8d4-4b93-afaa-0994068be14c\\\"\\n                     } ]\\n                  } ]\\n               }\\n            } ]\\n         },\\n         \\\"relationship-list\\\" : {\\n            \\\"relationship\\\" : [ {\\n               \\\"related-to\\\" : \\\"model\\\",\\n               \\\"relationship-data\\\" : [ {\\n                  \\\"relationship-key\\\" : \\\"model.model-invariant-id\\\",\\n                  \\\"relationship-value\\\" : \\\"82194af1-3c2c-485a-8f44-420e22a9eaa4\\\"\\n               } ]\\n            } ]\\n         }\\n      } ]\\n   }\\n}\\n\"\r\n" + 
			"				},\r\n" + 
			"				\"description\": \"\"\r\n" + 
			"			},\r\n" + 
			"			\"response\": []\r\n" + 
			"		}\r\n" + 
			"	]\r\n" + 
			"}";
}
