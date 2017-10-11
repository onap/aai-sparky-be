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
package org.onap.aai.sparky.synchronizer.filter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.onap.aai.sparky.config.oxm.OxmModelLoader;
import org.onap.aai.sparky.logging.AaiUiMsgs;
import org.onap.aai.sparky.synchronizer.SyncHelper;
import org.onap.aai.sparky.util.NodeUtils;
import org.onap.aai.cl.api.Logger;
import org.onap.aai.cl.eelf.LoggerFactory;
import org.onap.aai.cl.mdc.MdcContext;

/*
 * This is a wire-frame for an experiment to get the jetty filter-lifecyle initialization method to
 * setup a scheduled thread executor with an ElasticSearchSynchronization task, which (I'm hoping)
 * will allow us to do periodic ES <=> AAI synchronization.
 * 
 * Alternatively, if the embedded java approach doesn't work we could try instead to do a
 * System.exec( "perl refreshElasticSearchInstance.pl"). We have two options, I'm hoping the
 * embedded options will work for us.
 */

/**
 * The Class ElasticSearchSynchronizerFilter.
 */
public class ElasticSearchSynchronizerFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getInstance().getLogger(ElasticSearchSynchronizerFilter.class);

  private SyncHelper syncHelper;

  /* (non-Javadoc)
   * @see javax.servlet.Filter#destroy()
   */
  @Override
  public void destroy() {

    if (syncHelper != null) {
      syncHelper.shutdown();
    }
  }

  /* (non-Javadoc)
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    /*
     * However, we will setup the filtermap with a url that should never get it, so we shouldn't
     * ever be in here.
     */

    chain.doFilter(request, response);
  }

  /* (non-Javadoc)
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
	String txnID = NodeUtils.getRandomTxnId();
	MdcContext.initialize(txnID, "ElasticSearchSynchronizerFilter", "", "Init", "");
	    
	LOG.debug(AaiUiMsgs.DEBUG_GENERIC, "init()");

    try {
      new SyncHelper(OxmModelLoader.getInstance());
    } catch (Exception exc) {
      throw new ServletException("Caught an exception while initializing filter", exc);
    }

  }

  /**
   * @return the syncHelper
   */
  public SyncHelper getSyncHelper() {
    return syncHelper;
  }

  /**
   * @param syncHelper the syncHelper to set
   */
  public void setSyncHelper(SyncHelper syncHelper) {
    this.syncHelper = syncHelper;
  }

  /**
   * @return the log
   */
  public static Logger getLog() {
    return LOG;
  }

}
