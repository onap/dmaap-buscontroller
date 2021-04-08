/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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
package org.onap.dmaap.dbcapi.authentication;

import org.onap.dmaap.dbcapi.aaf.DmaapPerm;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.logging.DmaapbcLogMessageEnum;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

import java.util.Properties;

public class ApiPolicy extends BaseLoggingClass {

    private boolean permissionClassSet = true;
    private ApiAuthorizationCheckInterface perm = null;

    public ApiPolicy() {
        this(DmaapConfig.getConfig());
    }

    ApiPolicy(Properties p) {
        String dClass = p.getProperty("ApiPermission.Class");
        logger.info("ApiPolicy implements " + dClass);
        logger.info("dClass=" + dClass + " permissionClassSet=" + permissionClassSet);

        try {
            perm = (ApiAuthorizationCheckInterface) (Class.forName(dClass).newInstance());
        } catch (Exception ee) {
            errorLogger.error(DmaapbcLogMessageEnum.UNEXPECTED_CONDITION, "attempting to instantiate " + dClass);
            errorLogger.error("trace is: " + ee);
            permissionClassSet = false;
        }
    }

    public void check(String mechid, String pwd, DmaapPerm p) throws AuthenticationErrorException {
        perm.check(mechid, pwd, p);
    }

    public boolean isPermissionClassSet() {
        return permissionClassSet;
    }

    ApiAuthorizationCheckInterface getPerm() {
        return perm;
    }
}
