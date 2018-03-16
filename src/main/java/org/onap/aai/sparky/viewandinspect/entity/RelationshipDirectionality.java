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
package org.onap.aai.sparky.viewandinspect.entity;

/**
 * This enumeration is intended to be used to help us discriminate neighbor relationships for the
 * purpose of visualization and conceptualization to model in/out relationships between
 * ActiveInventoryNodes.
 * Possible visualization behaviors could be the following: - IN ( draw a line with 1 arrow ) - OUT
 * ( draw a line with 1 arrow ) - BOTH ( draw a line with 2 arrows, or 2 lines with 1 arrow each ) -
 * UNKNOWN ( draw a line with no arrows )
 * The UNKNOWN case is what we have at the moment where we have a collection neighbors with no
 * knowledge of relationship directionality.
 * 
 * @author davea
 *
 */
public enum RelationshipDirectionality {
  IN, OUT, BOTH, UNKNOWN
}
