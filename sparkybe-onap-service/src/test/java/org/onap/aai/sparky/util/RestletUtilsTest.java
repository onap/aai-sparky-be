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
package org.onap.aai.sparky.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.sparky.search.SearchServiceAdapter;
import org.restlet.Response;
import org.restlet.Server;
import org.restlet.engine.adapter.Call;
import org.restlet.engine.adapter.HttpResponse;
import org.restlet.ext.servlet.internal.ServletCall;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@RunWith(MockitoJUnitRunner.class)
public class RestletUtilsTest {

  static final String EMPTY_STRING = "";
  RestletUtils restletUtils;

  @Before
  public void init() {
    restletUtils = new RestletUtils();
  }

  @Test
  public void testConvertRestletResponseToHttpServletResponse() {
    Response restletResponse = Mockito.mock(HttpResponse.class);
    Call call = new ServletCall(Mockito.mock(Server.class),Mockito.mock(HttpServletRequest.class),Mockito.mock(HttpServletResponse.class));
    Mockito.when(((HttpResponse)restletResponse).getHttpCall()).thenReturn((ServletCall)call);
    restletUtils.convertRestletResponseToHttpServletResponse(restletResponse);
  }

  @Test
  public void testExecutePostQueryNullCheck() {
    Logger logger = Mockito.mock(Logger.class);
    Response response = Mockito.mock(Response.class);
    SearchServiceAdapter search = Mockito.mock(SearchServiceAdapter.class);
    String requestUrl = EMPTY_STRING;
    String requestJsonPayload = EMPTY_STRING;
    try{
      restletUtils.executePostQuery(logger,search,response,requestUrl,requestJsonPayload);
    }catch (NullPointerException e){
      //expected
    }
  }

  @Test
  public void testHandleRestletErrors() {
    Logger logger = Mockito.mock(Logger.class);
    Response response = Mockito.mock(Response.class);
    Exception exc = Mockito.mock(Exception.class);
    String errorMsg = "";
    restletUtils.handleRestletErrors(logger,errorMsg,exc,response);
  }

  @Test
  public void testSetRestletResponse() {
    Logger logger = Mockito.mock(Logger.class);
    Response response = Mockito.mock(Response.class);
    Exception exc = Mockito.mock(Exception.class);
    boolean isError = true;
    int responseCode =200;
    String payload = "";
    restletUtils.setRestletResponse(logger,false,responseCode,response,payload);
  }
}
