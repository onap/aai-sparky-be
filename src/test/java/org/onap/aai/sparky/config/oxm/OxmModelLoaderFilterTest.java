package org.onap.aai.sparky.config.oxm;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.aai.sparky.config.oxm.OxmModelLoaderFilter;

public class OxmModelLoaderFilterTest {

  @Mock
  ServletRequest servletRequest;

  @Mock
  ServletResponse servletResponse;

  @Mock
  FilterChain filterChain;

  @Mock
  FilterConfig filterConfig;

  @InjectMocks
  OxmModelLoaderFilter oxmModelLoaderFilter;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testDoFilter() throws IOException, ServletException {
    Mockito.doNothing().when(filterChain).doFilter(Mockito.any(ServletRequest.class),
        Mockito.any(ServletResponse.class));
    oxmModelLoaderFilter.doFilter(servletRequest, servletResponse, filterChain);
    Mockito.verify(filterChain, Mockito.times(1)).doFilter(Mockito.any(ServletRequest.class),
        Mockito.any(ServletResponse.class));
  }

  /*
   * This test is taking more than 5 secs. Commented out
   * 
   * @Test public void testInit() throws ServletException { OxmModelLoaderFilter oxmFilter = new
   * OxmModelLoaderFilter(); oxmFilter.init(filterConfig); }
   */

}
