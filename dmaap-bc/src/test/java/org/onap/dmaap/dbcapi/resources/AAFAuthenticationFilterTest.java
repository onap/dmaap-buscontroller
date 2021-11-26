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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.BeforeClass;
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

    @BeforeClass
    public static void setUpClass(){
        System.setProperty("ConfigFile", "src/test/resources/dmaapbc.properties");
    }

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
}