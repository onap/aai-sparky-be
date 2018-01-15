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
package org.onap.aai.sparky.subscription.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.onap.aai.sparky.util.ConfigHelper;
import org.onap.aai.sparky.viewandinspect.config.SparkyConstants;


/**
 * The Class SubscriptionConfig.
 */
public class SubscriptionConfig {

  public static final String CONFIG_FILE =
      SparkyConstants.DYNAMIC_CONFIG_APP_LOCATION + "subscription.properties";

  private static SubscriptionConfig instance;

  private String subscriptionTarget;
  
  private String subscriptionOrigin;
  
  private String subscriptionMessageType;
  
  private String subscriptionTopic;

  private String launchOITarget;
  
  private String launchOIOrigin;
  
  private String launchOIMessageType;
  
  private String launchOITopic;
  
  private Boolean isLaunchOIEnabled;
  
  private Collection<String> annEntitiyTypes;
  
  private static final String TARGET = "";
  
  private static final String ORIGIN = "";
  
  private static final String MESSAGE_TYPE = "";
  
  private static final String TOPIC = "";
 



  public static SubscriptionConfig getConfig(){

    if (instance == null) {
      instance = new SubscriptionConfig();
      instance.initializeProperties();
    }
    return instance;
  }

  public static void setConfig(SubscriptionConfig config) {
    /*
     * Explicitly allow setting the configuration singleton. This will be useful for automation.
     */

	  SubscriptionConfig.instance = config;
  }

  /**
   * Instantiates a new Subscription config.
   */
  public SubscriptionConfig() {
    // test method
  }  


  /**
   * Initialize properties.
   */
  private void initializeProperties() {
    Properties props = ConfigHelper.loadConfigFromExplicitPath(CONFIG_FILE);
    
    if (props == null || props.isEmpty()) {
      //Disable subscription launch if the file is missing
      this.setIsLaunchOIEnabled(false);
      return;
    }
    subscriptionTarget = props.getProperty("subscription.target", TARGET);
    subscriptionOrigin = props.getProperty("subscription.origin", ORIGIN);
    subscriptionMessageType = props.getProperty("subscription.messageType", MESSAGE_TYPE);
    subscriptionTopic = props.getProperty("subscription.topic", TOPIC);
    
    this.setLaunchOITarget(props.getProperty("launchOI.target", TARGET));
    this.setLaunchOIOrigin(props.getProperty("launchOI.origin", ORIGIN));
    this.setLaunchOIMessageType(props.getProperty("launchOI.messageType", MESSAGE_TYPE));
    this.setLaunchOITopic(props.getProperty("launchOI.topic", TOPIC));
    
    this.setAnnEntitiyTypes(Arrays.asList(props.getProperty("launchOI.entityTypes", "").split(",")));
    this.setIsLaunchOIEnabled(Boolean.parseBoolean(props.getProperty("launchOI.enable", "false")));
  }

  
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
