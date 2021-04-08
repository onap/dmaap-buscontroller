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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.service.DmaapService;
import org.onap.dmaap.dbcapi.util.DmaapConfig;
import org.onap.dmaap.dbcapi.util.PermissionBuilder;

public class AAFAuthorizationFilter extends BaseLoggingClass implements Filter {

    static final String CADI_AUTHZ_FLAG = "enableCADI";
    private boolean isCadiEnabled = false;

    private PermissionBuilder permissionBuilder;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        DmaapConfig dmaapConfig = getConfig();
        isCadiEnabled = "true".equalsIgnoreCase(dmaapConfig.getProperty(CADI_AUTHZ_FLAG, "false"));
        if(isCadiEnabled) {
            permissionBuilder = new PermissionBuilder(dmaapConfig, getDmaapService());
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {

        if(isCadiEnabled) {
            HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
            permissionBuilder.updateDmaapInstance();
            String permission = permissionBuilder.buildPermission(httpRequest);

            if (httpRequest.isUserInRole(permission)) {
                logger.info("User " + httpRequest.getUserPrincipal().getName() + " has permission " + permission);
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                String msg = "User " + httpRequest.getUserPrincipal().getName() + " does not have permission " + permission;
                errorLogger.error(msg);
                ((HttpServletResponse) servletResponse).setStatus(HttpStatus.FORBIDDEN_403);
                servletResponse.setContentType("application/json");
                servletResponse.setCharacterEncoding("UTF-8");
                servletResponse.getWriter().print(buildErrorResponse(msg));
                servletResponse.getWriter().flush();
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        //nothing to cleanup
    }

    DmaapConfig getConfig() {
        return (DmaapConfig) DmaapConfig.getConfig();
    }

    DmaapService getDmaapService() {
        return new DmaapService();
    }

    private String buildErrorResponse(String msg) {
        try {
            return new ObjectMapper().writeValueAsString(new ApiError(HttpStatus.FORBIDDEN_403, msg, "Authorization"));
        } catch (JsonProcessingException e) {
            logger.warn("Could not serialize response entity: " + e.getMessage());
            return "";
        }
    }

    PermissionBuilder getPermissionBuilder() {
        return permissionBuilder;
    }

    void setPermissionBuilder(PermissionBuilder permissionBuilder) {
        this.permissionBuilder = permissionBuilder;
    }

    void setCadiEnabled(boolean cadiEnabled) {
        isCadiEnabled = cadiEnabled;
    }
}
