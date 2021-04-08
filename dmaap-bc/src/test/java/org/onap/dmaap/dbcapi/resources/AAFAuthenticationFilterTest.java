/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2019 Nokia Intellectual Property. All rights reserved.
 * ================================================================================
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
 * ============LICENSE_END=========================================================
 */
package org.onap.dmaap.dbcapi.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.aaf.cadi.filter.CadiFilter;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

@RunWith(MockitoJUnitRunner.class)
public class AAFAuthenticationFilterTest {

    @Spy
    private AAFAuthenticationFilter filter;
    @Mock
    private FilterConfig filterConfig;
    @Mock
    private CadiFilter cadiFilterMock;
    @Mock
    private HttpServletRequest servletRequest;
    @Mock
    private HttpServletResponse servletResponse;
    @Mock
    private FilterChain filterChain;
    @Mock
    private DmaapConfig dmaapConfig;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        doReturn(dmaapConfig).when(filter).getConfig();
    }

    @Test
    public void init_shouldNotInitializeCADI_whenAafIsNotUsed() throws Exception {
        //given
        doReturn("false").when(dmaapConfig).getProperty(eq(AAFAuthenticationFilter.CADI_AUTHN_FLAG), anyString());

        //when
        filter.init(filterConfig);

        //then
        assertFalse(filter.isCadiEnabled());
        assertNull(filter.getCadiFilter());
    }

    @Test
    public void doFilter_shouldSkipCADI_whenAafIsNotUsed() throws Exception {
        //given
        doReturn("false").when(dmaapConfig).getProperty(eq(AAFAuthenticationFilter.CADI_AUTHN_FLAG), anyString());
        filter.init(filterConfig);
        filter.setCadiFilter(cadiFilterMock);

        //when
        filter.doFilter(servletRequest, servletResponse, filterChain);

        //then
        verify(filterChain).doFilter(servletRequest,servletResponse);
        verifyZeroInteractions(cadiFilterMock,servletRequest,servletResponse);
    }

    @Test
    public void init_shouldFail_whenAafIsUsed_andCadiPropertiesHasNotBeenSet() throws Exception {
        //given
        doReturn("true").when(dmaapConfig).getProperty(eq(AAFAuthenticationFilter.CADI_AUTHN_FLAG), anyString());
        doReturn("").when(dmaapConfig).getProperty(AAFAuthenticationFilter.CADI_PROPERTIES);

        //then
        thrown.expect(ServletException.class);
        thrown.expectMessage("Cannot initialize CADI filter.CADI properties not available.");

        //when
        filter.init(filterConfig);
    }

    @Test
    public void init_shouldFail_whenAafIsUsed_andInvalidCadiPropertiesSet() throws Exception {
        //given
        String invalidFilePath = "src/test/resources/notExisting.properties";
        doReturn("true").when(dmaapConfig).getProperty(eq(AAFAuthenticationFilter.CADI_AUTHN_FLAG), anyString());
        doReturn(invalidFilePath).when(dmaapConfig).getProperty(AAFAuthenticationFilter.CADI_PROPERTIES);

        //then
        thrown.expect(ServletException.class);
        thrown.expectMessage("Could not load CADI properties file: "+invalidFilePath);

        //when
        filter.init(filterConfig);
    }

  /*
   * See https://jira.onap.org/browse/DMAAP-1361  for why this is commented out
    @Test
    public void init_shouldInitializeCADI_whenAafIsUsed_andValidCadiPropertiesSet() throws Exception {
        //given
        doReturn("true").when(dmaapConfig).getProperty(eq(AAFAuthenticationFilter.CADI_AUTHN_FLAG), anyString());
        doReturn("src/test/resources/cadi.properties").when(dmaapConfig).getProperty(AAFAuthenticationFilter.CADI_PROPERTIES);

        //when
        filter.init(filterConfig);

        //then
        assertTrue(filter.isCadiEnabled());
        assertNotNull(filter.getCadiFilter());
    }

    @Test
    public void doFilter_shouldUseCADIfilter_andAuthenticateUser_whenAAFisUsed_andUserIsValid() throws Exception{
        //given
        initCADIFilter();
        doReturn(200).when(servletResponse).getStatus();

        //when
        filter.doFilter(servletRequest,servletResponse,filterChain);

        //then
        verify(cadiFilterMock).doFilter(servletRequest,servletResponse,filterChain);
        verify(servletResponse).getStatus();
        verifyNoMoreInteractions(servletResponse);
        verifyZeroInteractions(filterChain, servletRequest);
    }

    @Test
    public void doFilter_shouldUseCADIfilter_andReturnAuthenticationError_whenAAFisUsed_andUserInvalid() throws Exception{
        //given
        String errorResponseJson = "{\"code\":401,\"message\":\"invalid or no credentials provided\",\"fields\":\"Authentication\",\"2xx\":false}";
        initCADIFilter();
        doReturn(401).when(servletResponse).getStatus();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        doReturn(pw).when(servletResponse).getWriter();

        //when
        filter.doFilter(servletRequest,servletResponse,filterChain);

        //then
        verify(cadiFilterMock).doFilter(servletRequest,servletResponse,filterChain);
        verify(servletResponse).getStatus();
        verify(servletResponse).setContentType("application/json");
        verifyZeroInteractions(filterChain, servletRequest);
        assertEquals(errorResponseJson, sw.toString());
    }

    private void initCADIFilter() throws Exception{
        doReturn("true").when(dmaapConfig).getProperty(eq(AAFAuthenticationFilter.CADI_AUTHN_FLAG), anyString());
        doReturn("src/test/resources/cadi.properties").when(dmaapConfig).getProperty(AAFAuthenticationFilter.CADI_PROPERTIES);
        filter.init(filterConfig);
        filter.setCadiFilter(cadiFilterMock);
    }
*/
}