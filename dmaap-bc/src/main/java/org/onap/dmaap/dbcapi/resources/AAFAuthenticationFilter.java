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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.onap.aaf.cadi.PropAccess;
import org.onap.aaf.cadi.filter.CadiFilter;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

public class AAFAuthenticationFilter extends BaseLoggingClass implements Filter{

    static final String CADI_PROPERTIES = "cadi.properties";
    static final String CADI_AUTHN_FLAG = "enableCADI";

    private boolean isCadiEnabled;
    private CadiFilter cadiFilter;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        DmaapConfig dmaapConfig = getConfig();
        String flag = dmaapConfig.getProperty(CADI_AUTHN_FLAG, "false");
        isCadiEnabled = "true".equalsIgnoreCase(flag);
        initCadi(dmaapConfig);
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {

        if(isCadiEnabled) {
            cadiFilter.doFilter(servletRequest, servletResponse, filterChain);
            updateResponseBody((HttpServletResponse)servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private void updateResponseBody(HttpServletResponse httpResponse)
        throws IOException {
        if(httpResponse.getStatus() == 401) {
            String errorMsg = "invalid or no credentials provided";
            errorLogger.error(errorMsg);
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.getWriter().print(buildErrorResponse(errorMsg));
            httpResponse.getWriter().flush();
        }
    }

    private String buildErrorResponse(String msg) {
        try {
            return new ObjectMapper().writeValueAsString(new ApiError(HttpStatus.UNAUTHORIZED_401, msg, "Authentication"));
        } catch (JsonProcessingException e) {
            logger.warn("Could not serialize response entity: " + e.getMessage());
            return "";
        }
    }


    @Override
    public void destroy() {
        //nothing to cleanup
    }

    private void initCadi(DmaapConfig dmaapConfig) throws ServletException {
        if(isCadiEnabled) {
            try {
                String cadiPropertiesFile = dmaapConfig.getProperty(CADI_PROPERTIES);
                if(cadiPropertiesFile != null && !cadiPropertiesFile.isEmpty()) {
                    cadiFilter = new CadiFilter(loadCadiProperties(cadiPropertiesFile));
                } else {
                    throw new ServletException("Cannot initialize CADI filter.CADI properties not available.");
                }
            } catch (ServletException e) {
                errorLogger.error("CADI init error :" + e.getMessage());
                throw e;
            }
        }
    }

    private PropAccess loadCadiProperties(String propertiesFilePath) throws ServletException {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(propertiesFilePath));
            return new PropAccess(props);
        } catch (IOException e) {
            String msg = "Could not load CADI properties file: " + propertiesFilePath;
            errorLogger.error(msg, e);
            throw new ServletException(msg);
        }
    }

    DmaapConfig getConfig() {
        return (DmaapConfig) DmaapConfig.getConfig();
    }

    //tests only
    CadiFilter getCadiFilter() {
        return cadiFilter;
    }

    void setCadiFilter(CadiFilter cadiFilter) {
        this.cadiFilter = cadiFilter;
    }

    boolean isCadiEnabled() {
        return isCadiEnabled;
    }
}
