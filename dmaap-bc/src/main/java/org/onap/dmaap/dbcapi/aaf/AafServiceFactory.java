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

package org.onap.dmaap.dbcapi.aaf;

import org.onap.dmaap.dbcapi.aaf.AafService.ServiceType;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

public class AafServiceFactory extends BaseLoggingClass {

    private final DmaapConfig dmaapConfig;

    public AafServiceFactory() {
        this((DmaapConfig) DmaapConfig.getConfig());
    }

    AafServiceFactory(DmaapConfig dmaapConfig) {
        this.dmaapConfig = dmaapConfig;
    }

    public AafService initAafService(ServiceType serviceType) {
        boolean useAaf = "true".equalsIgnoreCase(dmaapConfig.getProperty("UseAAF", "false"));
        String aafUrl = dmaapConfig.getProperty("aaf.URL", "https://authentication.domain.netset.com:8100/proxy/");
        logger.info("AafService initAafService: useAaf={}, aafUrl={}", useAaf, aafUrl);

        AafCred cred = getCred(serviceType);
        return new AafServiceImpl(useAaf, aafUrl, cred.getIdentity(), new AafConnection(cred.toString()));
    }

    AafCred getCred(ServiceType ctype) {
        String mechIdProperty;
        String secretProperty;
        AafDecrypt decryptor = new AafDecrypt();

        if (ctype == ServiceType.AAF_Admin) {
            mechIdProperty = "aaf.AdminUser";
            secretProperty = "aaf.AdminPassword";
        } else if (ctype == ServiceType.AAF_TopicMgr) {
            mechIdProperty = "aaf.TopicMgrUser";
            secretProperty = "aaf.TopicMgrPassword";
        } else {
            logger.error("Unexpected case for AAF credential type: " + ctype);
            return null;
        }
        String identity = dmaapConfig.getProperty(mechIdProperty, "noMechId@domain.netset.com");
        String pwd = decryptor.decrypt(dmaapConfig.getProperty(secretProperty, "notSet"));

        return new AafCred(identity, pwd);
    }

    class AafCred {
        private final String identity;
        private final String pwd;

        AafCred(String identity, String pwd) {
            this.identity = identity;
            this.pwd = pwd;
        }

        public String getIdentity() {
            return identity;
        }

        public String toString() {
            return identity + ":" + pwd;
        }
    }
}
