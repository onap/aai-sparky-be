/**
 * ============LICENSE_START===================================================
 * SPARKY (AAI UI service)
 * ============================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=====================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */

package org.onap.aai.sparky.synchronizer;

import java.util.concurrent.atomic.AtomicInteger;

import org.onap.aai.sparky.sync.config.NetworkStatisticsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class AsyncRateControlTester.
 */
public class AsyncRateControlTester {

  private static Logger logger = LoggerFactory.getLogger(AsyncRateControlTester.class);

  private long startTimeInMs;

  private AtomicInteger counter;

  protected boolean syncInProgress;

  /**
   * Instantiates a new async rate control tester.
   *
   * @throws Exception the exception
   */
  public AsyncRateControlTester() throws Exception {

    NetworkStatisticsConfig tpc = new NetworkStatisticsConfig();

    tpc.setNumSamplesPerThreadForRunningAverage(100);

    tpc.setBytesHistogramLabel("bytesHistoLabel");
    tpc.setBytesHistogramMaxYAxis(1000000);
    tpc.setBytesHistogramNumBins(20);
    tpc.setBytesHistogramNumDecimalPoints(2);

    tpc.setQueueLengthHistogramLabel("queueHistoLabel");
    tpc.setQueueLengthHistogramMaxYAxis(1000000);
    tpc.setQueueLengthHistogramNumBins(20);
    tpc.setQueueLengthHistogramNumDecimalPoints(2);

    // ZeroDelayProcessor zdp = new ZeroDelayProcessor(LinkProcessorType.AAI, tpc);
    // zdp.setStatCollector(this.aaiStatCollector);
    /*
     * zdp.setTaskProcessorConfig(tpc);
     * 
     * this.resolver.registerProcessor(zdp); this.resolver.registerEventListener(this); this.counter
     * = new AtomicInteger(0); this.syncInProgress = false; }
     * 
     * @Override public void handleEvent(AsyncEvent event) {
     * 
     * if(event.getEventType() == AsyncEventType.RESOLVER_IDLE) {
     * 
     * if(syncInProgress) { long duration = System.currentTimeMillis() - startTimeInMs;
     * System.out.println(getStatReport(duration)); syncInProgress = false; }
     * 
     * // shutdown(); } else if(event.getEventType() == AsyncEventType.TRANSACTION_PROCESSED) {
     * 
     * this.syncInProgress = true;
     * 
     * ExternalResource resource = (ExternalResource)event.getPayload();
     * 
     * //aaiStatCollector.updateCounters(resource);
     * 
     * counter.incrementAndGet();
     * 
     * }
     * 
     * };
     * 
     * public void shutdown() { resolver.shutdown(); }
     * 
     * private int getCounterValue(AtomicInteger counter) {
     * 
     * if(counter == null) { return 0; }
     * 
     * return counter.get(); }
     * 
     * private void addActiveInventoryStatReport(StringBuilder sb) {
     * 
     * if(sb == null) { return; }
     * 
     * sb.append("\n\n    ").append(LinkProcessorType.AAI.name());
     * 
     * sb.append("\n\n        ").append("REST Operational Stats:");
     * 
     * /* Map<String, AtomicInteger> procOperationalCounters =
     * aaiStatCollector.getActiveInventoryOperationalCounters();
     * 
     * if(procOperationalCounters != null) {
     * 
     * int _1XX =
     * getCounterValue(procOperationalCounters.get(ActiveInventoryStatCollector.GET_1XX)); int _2XX
     * = getCounterValue(procOperationalCounters.get(ActiveInventoryStatCollector.GET_2XX)); int
     * _3XX = getCounterValue(procOperationalCounters.get(ActiveInventoryStatCollector.GET_3XX));
     * int _4XX =
     * getCounterValue(procOperationalCounters.get(ActiveInventoryStatCollector.GET_4XX)); int _5XX
     * = getCounterValue(procOperationalCounters.get(ActiveInventoryStatCollector.GET_5XX)); int
     * _6XX = getCounterValue(procOperationalCounters.get(ActiveInventoryStatCollector.GET_6XX));
     * 
     * sb.append("\n            ").append(String.format(
     * "%-12s 1XX: %-12d 2XX: %-12d 3XX: %-12d 4XX: %-12d 5XX: %-12d 6XX: %-12d ", HttpMethod.GET,
     * _1XX, _2XX, _3XX, _4XX, _5XX, _6XX)); }
     */

    // sb.append("\n\n ").append("Entity Stats:");

    /*
     * sort entities, then sort nested op codes
     */

    /*
     * TreeMap<String, HashMap<String, AtomicInteger>> activeInventoryEntitySortedTreeMap = new
     * TreeMap<String, HashMap<String, AtomicInteger>>( new Comparator<String>() {
     * 
     * public int compare(String o1, String o2) { return
     * o1.toLowerCase().compareTo(o2.toLowerCase()); } });
     */

    /*
     * activeInventoryEntitySortedTreeMap.putAll(aaiStatCollector.getActiveInventoryEntityCounters()
     * );
     * 
     * for(String counterEntityKey : activeInventoryEntitySortedTreeMap.keySet()) {
     * 
     * HashMap<String, AtomicInteger> entityCounters =
     * activeInventoryEntitySortedTreeMap.get(counterEntityKey);
     * 
     * AtomicInteger total = entityCounters.get(ActiveInventoryStatCollector.TOTAL); AtomicInteger
     * found = entityCounters.get(ActiveInventoryStatCollector.FOUND); AtomicInteger notFound =
     * entityCounters.get(ActiveInventoryStatCollector.NOT_FOUND); AtomicInteger error =
     * entityCounters.get(ActiveInventoryStatCollector.ERROR);
     * 
     * int totalValue = (total == null) ? 0 : total.get(); int foundValue = (found == null) ? 0 :
     * found.get(); int notFoundValue = (found == null) ? 0 : notFound.get(); int errorValue =
     * (error == null) ? 0 : error.get();
     * 
     * sb.append("\n            ").append(String.format(
     * "%-30s TOTAL: %-12d FOUND: %-12d NOT_FOUND: %-12d ERROR: %-12d", counterEntityKey,
     * totalValue, foundValue, notFoundValue, errorValue));
     * 
     * }
     */

    // sb.append("\n\n ").append("Task Processor Stats:");

    // int totalRetries =
    // getCounterValue(procOperationalCounters.get(ActiveInventoryStatCollector.NUM_RETRIES));
    // int currentQueueLength = resolver.getCurrentQueueLength(LinkProcessorType.AAI.name());

    /*
     * sb.append("\n            "
     * ).append(resolver.getProcessorTaskAgeStats(LinkProcessorType.AAI.name(), false, "          "
     * )); sb.append("\n            "
     * ).append(resolver.getProcessorResponseStats(LinkProcessorType.AAI.name(), false, "          "
     * )); sb.append("\n")
     * .append(resolver.getQueueItemLengthHistogram(LinkProcessorType.AAI.name(), false,
     * "            ")); sb.append("\n")
     * .append(resolver.getResponseByteSizeHistogram(LinkProcessorType.AAI.name(), false,
     * "            ")); sb.append("\n            "
     * ).append("TPS=").append(resolver.getTPS(LinkProcessorType.AAI.name())).append(", NumRetries="
     * ).append(totalRetries) .append(", CurrentQueueLength=").append(currentQueueLength);
     */
    /*
     * }
     * 
     * private String getStatReport(long syncOpTimeInMs) {
     * 
     * StringBuilder sb = new StringBuilder(128);
     * 
     * sb.append("\n").append("Async Resolver Statistics: ( Sync Operation Duration = " +
     * NodeUtils.getDurationBreakdown(syncOpTimeInMs) + " )");
     * 
     * addActiveInventoryStatReport(sb);
     * 
     * return sb.toString();
     * 
     * }
     * 
     * public void loadResolver(int numItems) {
     * 
     * if(numItems <= 0) { return; }
     * 
     * startTimeInMs = System.currentTimeMillis();
     * 
     * DummyPerformanceTask dpt = null;
     * 
     * for(int i = 0; i < numItems; i++) {
     * 
     * dpt = new DummyPerformanceTask(); dpt.setLinkProcessorType(LinkProcessorType.AAI);
     * dpt.setResourceEntityType("DummyPerformanceEntity"); dpt.setOperationType(HttpMethod.GET);
     * 
     * resolver.resolve(dpt);
     * 
     * }
     * 
     * }
     * 
     * public static void main(String[] args) throws Exception {
     * 
     * System.getProperties().setProperty("AJSC_HOME", "x:\\aaiui\\");
     * 
     * System.out.println("Available processors = " + Runtime.getRuntime().availableProcessors());
     * 
     * AsyncRateControlTester arcTester = new AsyncRateControlTester();
     * 
     * // give us time to instrument the jvm with jvisualvm // Thread.sleep(30000);
     * Thread.sleep(5000);
     * 
     * arcTester.loadResolver(1000);
     * 
     * 
     * }
     */
  }
}
