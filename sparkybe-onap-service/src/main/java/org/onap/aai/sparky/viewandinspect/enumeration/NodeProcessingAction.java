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
package org.onap.aai.sparky.viewandinspect.enumeration;

/**
 * The Enum NodeProcessingAction.
 */
public enum NodeProcessingAction {
  SELF_LINK_SET, NEW_NODE_PROCESSED, SELF_LINK_RESOLVE_ERROR, SELF_LINK_DETERMINATION_ERROR, 
  SELF_LINK_RESOLVE_OK, SELF_LINK_RESPONSE_PARSE_ERROR, SELF_LINK_RESPONSE_PARSE_OK, 
  NEIGHBORS_PROCESSED_ERROR, NEIGHBORS_PROCESSED_OK, COMPLEX_ATTRIBUTE_GROUP_PARSE_ERROR, 
  COMPLEX_ATTRIBUTE_GROUP_PARSE_OK, NODE_IDENTITY_ERROR,UNEXPECTED_STATE_TRANSITION
}

