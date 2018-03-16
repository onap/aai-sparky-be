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
package org.onap.aai.sparky.subscription.config;

import java.util.Collection;


/**
 * The Class SubscriptionConfig.
 */
public class SubscriptionConfig {

  private static final String EMPTY_TARGET = "";
  private static final String EMPTY_ORIGIN = "";
  private static final String EMPTY_MESSAGE_TYPE = "";
  private static final String EMPTY_TOPIC = "";


  private String subscriptionTarget = EMPTY_TARGET;
  private String subscriptionOrigin = EMPTY_ORIGIN;
  private String subscriptionMessageType = EMPTY_MESSAGE_TYPE;
  private String subscriptionTopic = EMPTY_TOPIC;

  private String launchOITarget = EMPTY_TARGET;
  private String launchOIOrigin = EMPTY_ORIGIN;
  private String launchOIMessageType = EMPTY_MESSAGE_TYPE;
  private String launchOITopic = EMPTY_TOPIC;

  private Boolean isLaunchOIEnabled = false;

  private Collection<String> annEntitiyTypes;

  /**
   * Instantiates a new Subscription config.
   */
  public SubscriptionConfig() {}

  public String getSubscriptionTarget() {
    return subscriptionTarget;
  }

  public void setSubscriptionTarget(String target) {
    this.subscriptionTarget = target;
  }

  public String getSubscriptionOrigin() {
    return subscriptionOrigin;
  }

  public void setSubscriptionOrigin(String origin) {
    this.subscriptionOrigin = origin;
  }

  public String getSubscriptionMessageType() {
    return subscriptionMessageType;
  }

  public void setSubscriptionMessageType(String messageType) {
    this.subscriptionMessageType = messageType;
  }

  public String getSubscriptionTopic() {
    return subscriptionTopic;
  }

  public void setSubscriptionTopic(String topic) {
    this.subscriptionTopic = topic;
  }

  public String getLaunchOITarget() {
    return launchOITarget;
  }

  public void setLaunchOITarget(String launchOITarget) {
    this.launchOITarget = launchOITarget;
  }

  public String getLaunchOIOrigin() {
    return launchOIOrigin;
  }

  public void setLaunchOIOrigin(String launchOIOrigin) {
    this.launchOIOrigin = launchOIOrigin;
  }

  public String getLaunchOIMessageType() {
    return launchOIMessageType;
  }

  public void setLaunchOIMessageType(String launchOIMessageType) {
    this.launchOIMessageType = launchOIMessageType;
  }

  public String getLaunchOITopic() {
    return launchOITopic;
  }

  public void setLaunchOITopic(String launchOITopic) {
    this.launchOITopic = launchOITopic;
  }

  public Collection<String> getAnnEntitiyTypes() {
    return annEntitiyTypes;
  }

  public void setAnnEntitiyTypes(Collection<String> annEntitiyTypes) {
    this.annEntitiyTypes = annEntitiyTypes;
  }

  public Boolean getIsLaunchOIEnabled() {
    return isLaunchOIEnabled;
  }

  public void setIsLaunchOIEnabled(Boolean isLaunchOIEnabled) {
    this.isLaunchOIEnabled = isLaunchOIEnabled;
  }
}
