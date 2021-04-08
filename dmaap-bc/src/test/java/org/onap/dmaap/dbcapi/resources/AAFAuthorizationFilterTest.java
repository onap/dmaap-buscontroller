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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import com.sun.security.auth.UserPrincipal;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.dmaap.dbcapi.model.Dmaap;
import org.onap.dmaap.dbcapi.service.DmaapService;
import org.onap.dmaap.dbcapi.util.DmaapConfig;
import org.onap.dmaap.dbcapi.util.PermissionBuilder;

@RunWith(MockitoJUnitRunner.class)
public class AAFAuthorizationFilterTest {

    @Spy
    private AAFAuthorizationFilter filter;
    @Mock
    private FilterConfig filterConfig;
    @Mock
    private HttpServletRequest servletRequest;
    @Mock
    private HttpServletResponse servletResponse;
    @Mock
    private FilterChain filterChain;
    @Mock
    private DmaapConfig dmaapConfig;
    @Mock
    private PermissionBuilder permissionBuilder;
    @Mock
    private DmaapService dmaapService;

    @Before
    public void setUp() throws Exception {
        filter.setPermissionBuilder(permissionBuilder);
        doReturn(dmaapConfig).when(filter).getConfig();
        doReturn(dmaapService).when(filter).getDmaapService();
    }

    @Test
    public void init_shouldNotInitializePermissionBuilder_whenAAFnotUsed() throws Exception {
        //given
        filter.setPermissionBuilder(null);
        configureAAFUsage(false);

        //when
        filter.init(filterConfig);

        //then
        assertNull(filter.getPermissionBuilder());
    }

    @Test
    public void init_shouldInitializePermissionBuilder_whenAAFisUsed() throws Exception {
        //given
        filter.setPermissionBuilder(null);
        configureAAFUsage(true);
        //doReturn(provideEmptyInstance()).when(dmaapService).getDmaap();
        when(dmaapService.getDmaap()).thenReturn(mock(Dmaap.class));

        //when
        filter.init(filterConfig);

        //then
        assertNotNull(permissionBuilder);
    }

    @Test
    public void doFilter_shouldSkipAuthorization_whenAAFnotUsed() throws Exception {
        //given
        filter.setCadiEnabled(false);

        //when
        filter.doFilter(servletRequest,servletResponse,filterChain);

        //then
        verify(filterChain).doFilter(servletRequest,servletResponse);
        verifyNoMoreInteractions(filterChain);
        verifyZeroInteractions(permissionBuilder, servletRequest, servletResponse);
    }

    @Test
    public void doFilter_shouldPass_whenUserHasPermissionToResourceEndpoint() throws Exception {
        //given
        String user = "johnny";
        String permission = "org.onap.dmaap-bc.api.topics|mr|GET";
        when(permissionBuilder.buildPermission(servletRequest)).thenReturn(permission);
        configureServletRequest(permission, user, true);
        filter.setCadiEnabled(true);

        //when
        filter.doFilter(servletRequest,servletResponse,filterChain);

        //then
        verify(filterChain).doFilter(servletRequest,servletResponse);
        verify(permissionBuilder).updateDmaapInstance();
        verifyZeroInteractions(servletResponse);
    }

    @Test
    public void doFilter_shouldReturnError_whenUserDontHavePermissionToResourceEndpoint() throws Exception {
        //given
        String user = "jack";
        String permission = "org.onap.dmaap-bc.api.topics|mr|GET";
        when(permissionBuilder.buildPermission(servletRequest)).thenReturn(permission);
        configureServletRequest(permission, user, false);
        filter.setCadiEnabled(true);

        String errorMsgJson = "{\"code\":403,\"message\":\"User "+user+" does not have permission "
            + permission +"\",\"fields\":\"Authorization\",\"2xx\":false}";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(servletResponse.getWriter()).thenReturn(pw);

        //when
        filter.doFilter(servletRequest,servletResponse,filterChain);

        //then
        verifyZeroInteractions(filterChain);
        verify(permissionBuilder).updateDmaapInstance();
        verify(servletResponse).setStatus(403);
        assertEquals(errorMsgJson, sw.toString());
    }

    private void configureServletRequest(String permission, String user, boolean isUserInRole) {
        when(servletRequest.getUserPrincipal()).thenReturn(new UserPrincipal(user));
        when(servletRequest.isUserInRole(permission)).thenReturn(isUserInRole);
    }

    private void configureAAFUsage(Boolean isUsed) {
        doReturn(isUsed.toString()).when(dmaapConfig).getProperty(eq(AAFAuthorizationFilter.CADI_AUTHZ_FLAG), anyString());
    }
}
