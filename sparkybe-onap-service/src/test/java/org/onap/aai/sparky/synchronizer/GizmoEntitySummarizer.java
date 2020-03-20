/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 Amdocs
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
 */

package org.onap.aai.sparky.synchronizer;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.dal.GizmoAdapter;
import org.onap.aai.sparky.dal.exception.ElasticSearchOperationException;
import org.onap.aai.sparky.dal.rest.RestClientConstructionException;
import org.onap.aai.sparky.dal.rest.config.RestEndpointConfig;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.sparky.util.OxmModelAndProcessorHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class GizmoEntitySummarizer {

	protected ObjectMapper mapper;
	protected OxmModelLoader oxmModelLoader;
	private static final Logger logger = LoggerFactory.getInstance().getLogger(GizmoEntitySummarizer.class);
	protected ExecutorService gizmoExecutor;
	protected GizmoAdapter gizmoAdapter;
	protected OxmModelAndProcessorHelper oxmHelper;

	/*
	 * We need to add another concept to the OxmModelLoader which is to generate
	 * a list of entity containers from the OXM JaxbContext
	 */

	public GizmoEntitySummarizer()
			throws ElasticSearchOperationException, IOException, RestClientConstructionException {

		OxmModelAndProcessorHelper.API_VERSION_OVERRIDE = "v11";

		this.gizmoExecutor = NodeUtils.createNamedExecutor("GIZMO-WORKER", 5, logger);

		oxmHelper = OxmModelAndProcessorHelper.getInstance();
		this.oxmModelLoader = oxmHelper.getModelLoader();

		this.mapper = new ObjectMapper();

		RestEndpointConfig gizmoConfig = new RestEndpointConfig();

		gizmoConfig.setEndpointIpAddress("10.147.138.153");
		gizmoConfig.setEndpointServerPort("9520");
		gizmoConfig.setNumRequestRetries(5);
		gizmoConfig.setRestAuthenticationMode(RestAuthenticationMode.SSL_CERT);
		gizmoConfig.setConnectTimeoutInMs(60000);
		gizmoConfig.setReadTimeoutInMs(30000);
		gizmoConfig.setCertFileName("client-cert-onap.p12");
		gizmoConfig.setCertPassword("OBF:1y0q1uvc1uum1uvg1pil1pjl1uuq1uvk1uuu1y10");
		gizmoConfig.setTruststoreFileName("synchronizer.jks");
		gizmoConfig.setValidateServerCertChain(false);
		gizmoConfig.setValidateServerHostname(false);

		gizmoAdapter = new GizmoAdapter(oxmModelLoader, gizmoConfig);
		gizmoAdapter.setInventoryBasePath("/services/inventory/v12/");
		gizmoAdapter.setRelationshipsBasePath("/services/inventory/relationships/v12/");

	}

	private Map<String, Integer> getNumEntitiesPerType() {

		Collection<String> containerTypes = oxmHelper.getOxmEntityContainerLookup().getEntityContainers();
		Collection<String> links = new ArrayList<String>();
		Map<String, Integer> entityTypeCounts = new TreeMap<String, Integer>();

		final CountDownLatch latch = new CountDownLatch(containerTypes.size());

		for (String entityType : containerTypes) {

			supplyAsync(new Supplier<Void>() {

				@Override
				public Void get() {

					OperationResult typeLinksResult = null;
					try {
						typeLinksResult = gizmoAdapter.queryGizmoWithRetries(
								gizmoAdapter.getFullInventoryUrl(entityType), "application/json", 1);

						if (typeLinksResult != null) {

							if (typeLinksResult.wasSuccessful() && typeLinksResult.getResult() != null) {

								JsonNode rootNode = mapper.readValue(typeLinksResult.getResult(), JsonNode.class);

								if (rootNode.isArray()) {
									ArrayNode arrayNode = (ArrayNode) rootNode;
									entityTypeCounts.put(entityType, new Integer(arrayNode.size()));
								} else {
									entityTypeCounts.put(entityType, new Integer(-1));
								}

							} else {
								// -1
								entityTypeCounts.put(entityType, new Integer(-1));
							}

						}

					} catch (Exception exc) {
						entityTypeCounts.put(entityType, new Integer(-1));
					}

					return null;
				}

			}, gizmoExecutor).whenComplete((result, error) -> {

				latch.countDown();

				if (error != null) {
					logger.error(AaiUiMsgs.ERROR_GENERIC,
							"An error occurred getting data from AAI. Error = " + error.getMessage());
				}

			});

		}

		// System.out.println("self links size = " + selflinks.size());

		try {
			latch.await();
		} catch (InterruptedException e) {

		}

		return entityTypeCounts;
	}

	private Map<String, Integer> getNumRelationshipsPerType() {

		Map<String, Integer> entityTypeCounts = new TreeMap<String, Integer>();

		final CountDownLatch latch = new CountDownLatch(1);

		supplyAsync(new Supplier<Void>() {

			@Override
			public Void get() {

				OperationResult typeLinksResult = null;
				try {
					typeLinksResult = gizmoAdapter.queryGizmoWithRetries(gizmoAdapter.getFullRelationshipUrl("has"),
							"application/json", 1);

					if (typeLinksResult != null) {

						if (typeLinksResult.wasSuccessful() && typeLinksResult.getResult() != null) {

							JsonNode rootNode = mapper.readValue(typeLinksResult.getResult(), JsonNode.class);

							if (rootNode.isArray()) {
								ArrayNode arrayNode = (ArrayNode) rootNode;
								entityTypeCounts.put("has", new Integer(arrayNode.size()));
							} else {
								entityTypeCounts.put("has", new Integer(-1));
							}

						} else {
							// -1
							entityTypeCounts.put("has", new Integer(-1));
						}

					} else {
						entityTypeCounts.put("has", new Integer(-1));
					}

				} catch (Exception exc) {
					entityTypeCounts.put("has", new Integer(-1));
				}

				return null;
			}

		}, gizmoExecutor).whenComplete((result, error) -> {

			latch.countDown();

			if (error != null) {
				logger.error(AaiUiMsgs.ERROR_GENERIC,
						"An error occurred getting data from AAI. Error = " + error.getMessage());
			}

		});

		// System.out.println("self links size = " + selflinks.size());

		try {
			latch.await();
		} catch (InterruptedException e) {

		}

		return entityTypeCounts;
	}

	public void shutdown() {
		this.gizmoExecutor.shutdown();
	}

	public static void main(String[] args)
			throws ElasticSearchOperationException, IOException, RestClientConstructionException {

		System.setProperty("CONFIG_HOME", "X:\\2018_dev\\OSEAAI\\gizmo_integration\\onap_sparky-be\\appconfig-local\\");
		GizmoEntitySummarizer gizmoSummarizer = new GizmoEntitySummarizer();

		Map<String, Integer> entityCounts = gizmoSummarizer.getNumEntitiesPerType();
		Map<String, Integer> relationshipCounts = gizmoSummarizer.getNumRelationshipsPerType();
		gizmoSummarizer.shutdown();

		System.out.println("Gizmo Entities:");

		for (Entry<String, Integer> entry : entityCounts.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			System.out.printf("\t%s : %d\n", key, value);
		}

		System.out.println("\nGizmo Relationships:");

		for (Entry<String, Integer> entry : relationshipCounts.entrySet()) {
			String key = entry.getKey();
			Integer value = entry.getValue();

			System.out.printf("\t%s : %d\n", key, value);
		}

	}

}