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
package org.onap.dmaap.dbcapi.util;

import javax.servlet.http.HttpServletRequest;
import org.onap.dmaap.dbcapi.model.Dmaap;
import org.onap.dmaap.dbcapi.service.DmaapService;

public class PermissionBuilder {

    static final String API_NS_PROP = "ApiNamespace";
    static final String DEFAULT_API_NS = "org.onap.dmaap-bc.api";
    static final String BOOT_INSTANCE = "boot";
    private static final String PERM_SEPARATOR = "|";
    private static final String NS_SEPARATOR = ".";
    private DmaapConfig dmaapConfig;
    private DmaapService dmaapService;
    private String instance;
    private String apiNamespace;

    public PermissionBuilder(DmaapConfig dmaapConfig, DmaapService dmaapService) {
        this.dmaapConfig = dmaapConfig;
        this.dmaapService = dmaapService;
        initFields();
    }

    public synchronized void updateDmaapInstance() {
        if(instance == null || instance.isEmpty() || instance.equalsIgnoreCase(BOOT_INSTANCE)) {
            String dmaapName = getDmaapName();
            instance = (dmaapName == null || dmaapName.isEmpty()) ? BOOT_INSTANCE : dmaapName;
        }
    }

    public String buildPermission(HttpServletRequest httpRequest) {

        StringBuilder sb = new StringBuilder(apiNamespace);
        sb.append(NS_SEPARATOR)
            .append(getPermissionType(httpRequest.getPathInfo()))
            .append(PERM_SEPARATOR)
            .append(instance)
            .append(PERM_SEPARATOR)
            .append(httpRequest.getMethod());
        return sb.toString();
    }


    private void initFields() {
        apiNamespace = dmaapConfig.getProperty(API_NS_PROP, DEFAULT_API_NS);
        updateDmaapInstance();
    }

    private String getDmaapName() {
        Dmaap dmaap = dmaapService.getDmaap();
        return ( dmaap != null ) ? dmaap.getDmaapName() : BOOT_INSTANCE;
    }

    private String getPermissionType(String pathInfo) {
        char pathSeparator = '/';
        String[] pathSlices = pathInfo.split(String.valueOf(pathSeparator));
        return pathSlices[1];
    }

    String getInstance() {
        return instance;
    }
}
