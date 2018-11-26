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
package org.onap.aai.sparky.logging;

import org.onap.aai.cl.eelf.LogMessageEnum;

import com.att.eelf.i18n.EELFResourceManager;

/**
 * The Enum AaiUiMsgs.
 */
public enum AaiUiMsgs implements LogMessageEnum {
  /** Arguments: {0} = Exception/error. */
  FAILURE_TO_PROCESS_REQUEST,
  /** Arguments: {0} = Message and or error body. */
  FAILED_TO_DETERMINE,
  /** Arguments: {0} = Exception/error. */
  UNKNOWN_SERVER_ERROR,
  /** Arguments: {0} = Message and or error body. */
  FAILED_TO_ANALYZE,
  /** Arguments: {0} = Exception/error. */
  FAILED_TO_GET_NODES_QUERY_RESULT,
  /** Arguments: {0} = Expected link count, {1} = Actual link count. */
  UNEXPECTED_NUMBER_OF_LINKS,
  /** Arguments: {0} = Reason. */
  DANGLING_NODE_WARNING,
  /** Arguments: {0} = Node count, {1} = Link count. */
  VISUALIZATION_GRAPH_OUTPUT,
  /** Arguments: {0} = JsonNode. */
  ITEM_TYPE_NULL,
  /** Arguments: {0} = Filter property. */
  UNEXPECTED_TOKEN_COUNT,
  /** Arguments: {0} = Error/exception message. */
  ADD_SEARCH_TARGET_ATTRIBUTES_FAILED,
  /** Arguments: {0} = Error/exception message. */
  NODE_INTEGRITY_OVERLAY_ERROR,
  /** Arguments: {0} = Node ID. */
  NODE_INTEGRITY_ALREADY_PROCESSED,
  /** Arguments: {0} = Node ID. */
  SKIPPING_PROCESS_NODE_INTEGRITY,
  /** Arguments: {0} = Error/exception message. */
  FAILED_TO_PROCESS_NODE_INTEGRITY,
  /** No argument */
  MAX_EVALUATION_ATTEMPTS_EXCEEDED,
  /** Arguments: {0} = Error/exception message. */
  VISUALIZATION_OUTPUT_ERROR,
  /** Arguments: {0} = Total resolve time, {1} = Total links retrieved, {2} = Op time. */
  ALL_TRANSACTIONS_RESOLVED,
  /** Arguments: {0} = Error/exception message. */
  PROCESSING_LOOP_INTERUPTED,
  /** Arguments: {0} = Node ID. */
  IGNORING_SKELETON_NODE,
  /** Arguments: {0} = Node count. */
  OUTSTANDING_WORK_PENDING_NODES,
  /** Arguments: {0} = Reason. */
  FAILED_TO_ADD_SKELETON_NODE,
  /** Arguments: {0} = Reason. */
  FAILED_TO_PROCESS_SKELETON_NODE,
  INVALID_RESOLVE_STATE_DURING_INIT,
  /** Arguments: {0} = Reason. */
  FAILED_TO_PROCESS_INITIAL_STATE,
  /** Arguments: {0} = Relationship. */
  SKIPPING_RELATIONSHIP,
  /** Arguments: {0} = Failure reason. */
  FAILED_TO_DETERMINE_NODE_ID,
  /** Arguments: {0} = Error/exception message. */
  EXTRACTION_ERROR,
  /** Arguments: {0} = Error/exception message. */
  SELF_LINK_NODE_PARSE_ERROR,
  /** Arguments: {0} = Node ID. */
  ROOT_NODE_DISCOVERED,
  /** Arguments: {0} = Error/exception message. */
  SELF_LINK_PROCESS_NEIGHBORS_ERROR,
  /** Arguments: {0} = Error/exception message. */
  SELF_LINK_JSON_PARSE_ERROR,
  /** Arguments: {0} = Error/exception message. */
  SELF_LINK_PROCESSING_ERROR,
  /** Arguments: {0} = Entity type. */
  UNHANDLED_OBJ_TYPE_FOR_ENTITY_TYPE,
  /** Arguments: {0} = Attribute group. */
  ATTRIBUTE_GROUP_FAILURE,
  /** Arguments: {0} = Situational description, {1} = Exception message. */
  EXCEPTION_CAUGHT,
  /** Arguments: {0} = Operation name, {1} = Operation time. */
  OPERATION_TIME,
  /** Arguments: {0} = Error message. */
  SEARCH_SERVLET_ERROR,
  /** Arguments: {0} = Exception message. */
  SEARCH_RESPONSE_BUILDING_EXCEPTION,
  /** Arguments: {0} = Error message, {1} = Error message. */
  SEARCH_TAG_ANNOTATION_ERROR,
  /** Arguments: {0} = App type. */
  QUERY_FAILED_UNHANDLED_APP_TYPE,
  /** Arguments: {0} = Entity type. */
  ENTITY_NOT_FOUND_IN_OXM,
  /** Arguments: {0} = JSON conversion type, {1} = Error thrown. */
  JSON_CONVERSION_ERROR,
  /** Arguments: {0} = Node ID */
  NO_RELATIONSHIP_DISCOVERED,
  /** No argument */
  SELF_LINK_NULL_EMPTY_RESPONSE,
  /** Arguments: {0} = Error message. */
  SELF_LINK_RELATIONSHIP_LIST_ERROR,
  /** Arguments: {0} = AIN id, {1} = old depth, {2} = new depth. */
  ACTIVE_INV_NODE_CHANGE_DEPTH,
  /** Arguments: {0} = Node ID, {1} = Current state, {2} = New state {3} = Triggering action */
  ACTIVE_INV_NODE_CHANGE_STATE,
  /** Arguments: {0} = Current state, {1} = New state {2} = Triggering action */
  ACTIVE_INV_NODE_CHANGE_STATE_NO_NODE_ID,
  /** Arguments: {0} = Count Key {1} = Aggregation Key. */
  AGGREGATION_KEY_ERROR, 
  /** Arguments: {0} Configuration */
  CONFIGURATION_ERROR,
  /** Arguments: {0} = Source. */
  ERROR_PARSING_JSON_PAYLOAD_NONVERBOSE,
  /** Arguments: {0} = Payload. */
  ERROR_PARSING_JSON_PAYLOAD_VERBOSE,
  /** Arguments: {0} = Key {1} = JSON Blob. */
  ERROR_FETCHING_JSON_VALUE,
  /** Arguments: {0} = Error. */
  ERROR_PARSING_PARAMS,
  /** No argument */
  INVALID_REQUEST_PARAMS,
  /** Arguments: {0} = Key. */
  ERROR_SORTING_VIOLATION_DATA, 
  /** Arguments: {0} = exception */
  ERROR_SERVLET_PROCESSSING,
  /** Arguments: {0} = exception */
  ERROR_BUILDING_RESPONSE_FOR_TABLE_QUERY,
  /** Arguments: {0} = exception */
  ERROR_BUILDING_SEARCH_RESPONSE,
  /** No argument */
  ERROR_CSP_CONFIG_FILE,
  /** Arguments: {0} = exception */
  ERROR_SHUTDOWN_EXECUTORS,
  /** No argument */
  ERROR_LOADING_OXM,
  /** Arguments: {0} = exception */
  ERROR_GETTING_DATA_FROM_AAI,
  /** No argument */
  WAIT_FOR_ALL_SELFLINKS_TO_BE_COLLECTED,
  /** Arguments: {0} = Entity Type */
  MISSING_ENTITY_DESCRIPTOR,
  /** Arguments: {0} = Error */
  SELF_LINK_GET,
  /** Arguments: {0} = Error */
  ES_FAILED_TO_CONSTRUCT_QUERY,
  /** Arguments: {0} = Error */
  ES_RETRIEVAL_FAILED,
  /** Arguments: {0} = Error */
  ES_LINK_UPSERT,
  /** Arguments: {0} = Element */
  ES_SIMPLE_PUT,
  /** Arguments: {0} = Value {1} = Element {2} = Error */
  ES_ABORT_CROSS_ENTITY_REF_SYNC,
  /** Arguments: {0} Return Code */
  ES_OPERATION_RETURN_CODE,
  /** Arguments: {0} = Error */
  ES_CROSS_ENTITY_REF_PUT,
  /** No argument */
  ES_CROSS_REF_SYNC_VERSION_CONFLICT,
  /** Arguments: {0} Result Code {1} = Error */
  ES_CROSS_REF_SYNC_FAILURE,
  /** Arguments: {0} = Error */
  ES_FAILED_TO_CONSTRUCT_URI,
  /** No argument */
  ES_RETRIEVAL_FAILED_RESYNC,
  /** Arguments: {0} = Entity */
  ES_CROSS_ENTITY_RESYNC_LIMIT,
  /** Arguments: {0} Entity Name */
  ES_PKEYVALUE_NULL,
  /** Arguments: {0} = Error */
  ES_STORE_FAILURE,
  /** Arguments: {0} Index Name {1} = Error */
  ES_PRE_SYNC_FAILURE,
  /** Arguments: {0} Index Name */
  ES_SYNC_CLEAN_UP,
  /** Arguments: {0} Index Name {1} Size before clean up {2} = Size after clean up */
  ES_SYNC_CLEAN_UP_SIZE,
  /** Arguments: {0} Index Name {1} Index Type {2} = Size before delete */
  ES_SYNC_SELECTIVE_DELETE,
  /** Arguments: {0} Index Name {1} Number of records */
  ES_BULK_DELETE,
  /** Arguments: {0} Index name {1} = Error */
  ES_BULK_DELETE_ERROR,
  /** Arguments: {0} Type of retrieval {1} Completion Time */
  COLLECT_TIME_WITH_ERROR,
  /** Arguments: {0} Type of retrieval {1} Completion Time */
  COLLECT_TIME_WITH_SUCCESS,
  /** Arguments: {0} Type of retrieval {1} Number of records */
  COLLECT_TOTAL,
  /** Arguments: {0} Number of required fetches */
  SYNC_NUMBER_REQ_FETCHES,
  /** Arguments: {0} Number of total fetches {1} Number of available records*/
  SYNC_NUMBER_TOTAL_FETCHES,
  /** Arguments: {0} Completion Time */
  COLLECT_TOTAL_TIME,
  /** Arguments: {0} = Error */
  ES_SCROLL_CONTEXT_ERROR,
  /** No argument */
  ES_BULK_DELETE_SKIP,
  /** Arguments: {0} = Number of docs */
  ES_BULK_DELETE_START,
  /** No argument */
  SELF_LINK_CROSS_REF_SYNC,
  /** Arguments: {0} = message */
  ERROR_GENERIC,
  /** Arguments: {0} = error */
  JSON_PROCESSING_ERROR,
  /** Arguments: {0} = exception */
  ERROR_PROCESSING_REQUEST,
  /** Arguments: {0} = Self Link */
  SELF_LINK_GET_NO_RESPONSE,
  /** Arguments: {0} = error */
  HISTORICAL_COLLECT_ERROR,
  /** Arguments: {0} = Time */
  HISTORICAL_ENTITY_COUNT_SUMMARIZER_STARTING,
  /** No argument */
  HISTORICAL_ENTITY_COUNT_SUMMARIZER_NOT_STARTED,
  /** Arguments: {0} = Controller {1} = Time */
  HISTORICAL_SYNC_DURATION,
  /** No argument */
  HISTORICAL_SYNC_PENDING,
  /** Arguments: {0} = Time */
  HISTORICAL_SYNC_TO_BEGIN,  
  /** Arguments: {0} = message */
  DEBUG_GENERIC,
  /** Arguments: {0} = message */
  INFO_GENERIC,
  /** Arguments: {0} = message */
  WARN_GENERIC,
  /** Arguments: {0} = context {1} = Exception*/
  INTERRUPTED,
  /** Arguments: {0} = Entity Type {1} Entity */
  GEO_SYNC_IGNORING_ENTITY,
  /** Arguments: {0} = reason */
  OXM_LOADING_ERROR,
  /** Arguments: {0} = type */
  OXM_FAILED_RETRIEVAL,
  OXM_FILE_NOT_FOUND,
  /** No argument */
  OXM_READ_ERROR_NONVERBOSE,
  /** Arguments: {0} = OXM File name */
  OXM_READ_ERROR_VERBOSE,
  /** No argument */
  OXM_PARSE_ERROR_NONVERBOSE,
  /** Arguments: {0} = OXM File name {1} = Exception*/
  OXM_PARSE_ERROR_VERBOSE,
  /** Arguments: {0} = Numerical value for loaded OXM version */
  OXM_LOAD_SUCCESS,
  /** Arguments: {0} = Entity {1} = Found property-value*/
  OXM_PROP_DEF_ERR_CROSS_ENTITY_REF,
  /** Arguments: {0} = Sequence Number */
  ETAG_RETRY_SEQ,
  /** Arguments: {0} = Reason */
  ETAG_WAIT_INTERRUPTION,
  /** Arguments: {0} = URL {1} = Sequence Number */
  QUERY_AAI_RETRY_SEQ,
  /** Arguments: {0} = URL {1} = Sequence Number */
  QUERY_AAI_RETRY_DONE_SEQ,
  /** Arguments: {0} = Reason */
  QUERY_AAI_WAIT_INTERRUPTION,
  /** Arguments: {0} = URL {1} = Sequence Number */
  QUERY_AAI_RETRY_FAILURE_WITH_SEQ,
  /** Arguments: {0} = URL */
  QUERY_AAI_RETRY_MAXED_OUT,
  /** Arguments: {0} = Reason */
  PEGGING_ERROR,
  /** Arguments: {0} = Key */
  DATA_CACHE_SUCCESS,
  /** Arguments: {0} = URL {1} = Sequence Number */
  EXECUTOR_SERV_EXCEPTION,
  /** Arguments: {0} = Exception */
  DISK_CACHE_READ_IO_ERROR,
  /** Arguments: {0} = Exception */
  DISK_CREATE_DIR_IO_ERROR,
  /** Arguments: {0} = Exception */
  DISK_DATA_WRITE_IO_ERROR,
  /** Arguments: {0} = Data Item {1} = Exception */
  DISK_NAMED_DATA_WRITE_IO_ERROR,
  /** Arguments: {0} = Data Item {1} = Exception */
  DISK_NAMED_DATA_READ_IO_ERROR,
  /** No argument */
  OFFLINE_STORAGE_PATH_ERROR,
  /** Arguments: {0} = URL {1} = Error */
  RESTFULL_OP_ERROR_VERBOSE,
  /** Arguments: {0} = Method {1} = Time {2} = URL {3} = Result Code */
  RESTFULL_OP_COMPLETE,
  /** No argument */
  INITIALIZE_OXM_MODEL_LOADER,
  /** Arguments: {0} = Exception */
  AAI_RETRIEVAL_FAILED_GENERIC,
  /** Arguments: {0} = Self Link */
  AAI_RETRIEVAL_FAILED_FOR_SELF_LINK,
  /** Arguments: {0} = Cookie */
  COOKIE_FOUND,
  /** No argument */
  COOKIE_NOT_FOUND,
  /** Arguments: {0} = Message */
  INVALID_REQUEST,
  /** Arguments: {0} = User ID */
  USER_AUTHORIZATION_FILE_UNAVAILABLE,
  /** Arguments: {0} = URL {1} = Cause */
  INVALID_URL_VERBOSE,
  /** Arguments: {0} = Row ID */
  DI_DATA_NOT_FOUND_NONVERBOSE,
  /** Arguments: {0} = Row ID {1} Attempt count */
  DI_DATA_NOT_FOUND_VERBOSE,
  /** Arguments: {0} = Time in ms {1} Status */
  DI_MS_TIME_FOR_DATA_FETCH,
  /** Arguments: {0} = Number of Entity Links */
  ENTITY_SYNC_FAILED_SELFLINK_AMBIGUITY,
  /** Arguments: {0} = Message */
  ERROR_EXTRACTING_FROM_RESPONSE,
  /** No argument */
  ERROR_LOADING_OXM_SEARCHABLE_ENTITIES,
  /** Arguments: {0} = Message */
  ES_SEARCHABLE_ENTITY_SYNC_ERROR,
  /** Arguments: {0} = Message */
  FAILED_TO_REGISTER_DUE_TO_NULL,
  /** Arguments: {0} = File Path */
  FAILED_TO_RESTORE_TXN_FILE_MISSING,
  /** Arguments: {0} = Index Name */
  INDEX_ALREADY_EXISTS,
  /** Arguments: {0} = Index Name */
  INDEX_EXISTS,
  /** Arguments: {0} = Index Name {1} = Operation Result */
  INDEX_INTEGRITY_CHECK_FAILED,
  /** Arguments: {0} = Index Name */
  INDEX_NOT_EXIST,
  /** Arguments: {0} = Index Name */
  INDEX_RECREATED,
  /** Arguments: {0} = Time */
  SEARCH_ENGINE_SYNC_STARTED,
  /** Arguments: {0} = Time */
  SKIP_PERIODIC_SYNC_AS_SYNC_DIDNT_FINISH,
  /** Arguments: {0} = Message */
  SYNC_DURATION,
 /** Arguments: {0} = Entity Type */
  ENTITY_SYNC_FAILED_DESCRIPTOR_NOT_FOUND,
  /** Arguments: {0} = AAI Query Result */
  ENTITY_SYNC_FAILED_DURING_AAI_RESPONSE_CONVERSION,
  /** Arguments: {0} = Message */
  ENTITY_SYNC_FAILED_QUERY_ERROR,
  /** Arguments: {0} = Self Link Query */
  SELF_LINK_DETERMINATION_FAILED_GENERIC,
  /** Arguments: {0} = Number of Entity Links */
  SELF_LINK_DETERMINATION_FAILED_UNEXPECTED_LINKS,
  /** Arguments: {1} = Query {2} = Operation Result Code {3} = Operation Result */
  SELF_LINK_RETRIEVAL_FAILED,
  /** Arguments: {0} = Controller {1} = Synchronizer Current Internal State {2} = New State {3} = Caused By Action */
  SYNC_INTERNAL_STATE_CHANGED,
  /** Arguments: {0} = Message */
  SYNC_INVALID_CONFIG_PARAM,
  /** Arguments: {0} = Synchronizer Current Internal State */
  SYNC_NOT_VALID_STATE_DURING_REQUEST,
  /** No argument */
  SYNC_SKIPPED_SYNCCONTROLLER_NOT_INITIALIZED,
  /** No argument */
  SYNC_START_TIME,
  /** Arguments: {0} = Controller {1} = Time */
  SYNC_TO_BEGIN,
  /** Arguments: {0} = File Path */
  WILL_RETRIEVE_TXN,
  /** Arguments: {0} = Configuration file name {1} = Exception */
  CONFIG_NOT_FOUND_VERBOSE,
  /** Arguments: {0} = File name */
  FILE_NOT_FOUND,
  /** Arguments: {0} = File name */
  FILE_READ_IN_PROGRESS,
  ERROR_LOADING_OXM_SUGGESTIBLE_ENTITIES,
  /** Arguments: {0} = Error message */
  ES_SUGGESTION_SEARCH_ENTITY_SYNC_ERROR,
  /** Arguments: {0} = Error message */
  ES_AGGREGATION_SUGGESTION_ENTITY_SYNC_ERROR,
  /** Arguments: {0} = Error message. */
  ENTITY_SYNC_SEARCH_TAG_ANNOTATION_FAILED,
  /** Arguments: {0} = Error message */
  SEARCH_ADAPTER_ERROR,
  /** Arguments: {0} = Decoding exception message */
  UNSUPPORTED_URL_ENCODING,
  /** Arguments: {0} = Invalid URL */
  INVALID_REDIRECT_URL,
  /** Arguments: {0} = Valid login URL */
  VALID_REDIRECT_URL,
  /** Arguments: {0} = Query Parameter Self-Link Extraction Error */
  QUERY_PARAM_EXTRACTION_ERROR,
  /** Arguments: {0} = Info message */
  LOGIN_FILTER_INFO,
  /** Arguments: {0} = Debug message */
  LOGIN_FILTER_DEBUG,
  /** Arguments: {0} = URL to extract parameter from */
  ERROR_REMOVING_URL_PARAM,
  /** Arguments: {0} = Hash value */
  ERROR_INVALID_HASH,
  ERROR_HASH_NOT_FOUND,
  ERROR_FILTERS_NOT_FOUND,
  ERROR_READING_HTTP_REQ_PARAMS,
  /** Arguments: {0} = Exception */
  ERROR_D3_GRAPH_VISUALIZATION,
  /** Arguments: {0} = Exception */
  ERROR_AAI_QUERY_WITH_RETRY,
  /** Arguments: Error extracting resource path from self-link.  Error = {0} */
  ERROR_EXTRACTING_RESOURCE_PATH_FROM_LINK,
  /** Arguments: {0} = Schema file location */
  ERROR_READING_JSON_SCHEMA,
  /** Arguments: {0} = UI view name */
  VIEW_NAME_NOT_SUPPORTED,
  /** Arguments: {0} = response code, {1} = filter name */
  ERROR_FETCHING_FILTER_VALUES,
  /** Arguments: {0} = query type, {1} = view name */
  ERROR_PROCESSING_WIDGET_REQUEST,
  /** Arguments: {0} = Time in ms */
  DR_PROCESSING_TIME,
  /** Arguments: {0} = Response code {1} = payload */
  DR_PROCESSING_FAILURE,
  /** Arguments: {0} = request uri */
  DR_REQUEST_URI_FOR_PROXY_UNKNOWN,
  /** Arguments: {0} = origin-url {1} = dr-url */
  DR_PROXY_FROM_TO,
  /** Arguments: {0} = Exception */
  URI_DECODING_EXCEPTION,
  /** Arguments: {0} = Value {1} = Error */
  ENCRYPTION_ERROR,
  /** Arguments: {0} = Encrypted value {1} = Error */
  DECRYPTION_ERROR,
  /** Arguments: {0} = URI */
  RESOURCE_NOT_FOUND;

  /**
   * Static initializer to ensure the resource bundles for this class are loaded...
   */
  static {
    EELFResourceManager.loadMessageBundle("logging/AAIUIMsgs");
  }
}
