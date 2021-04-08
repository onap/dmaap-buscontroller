/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property. All rights reserved.
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

package org.onap.dmaap.dbcapi.server;


import java.util.Properties;

import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

public class CertficateManagerFactory extends BaseLoggingClass {
	private final Properties dmaapConfig;

    public CertficateManagerFactory() {
        this((DmaapConfig) DmaapConfig.getConfig());
    }

    CertficateManagerFactory(Properties params) {
        this.dmaapConfig = params;
    }

    public CertificateManager initCertificateManager() {
        boolean useCadi = "cadi".equalsIgnoreCase(dmaapConfig.getProperty("CertificateManagement", "legacy"));
        logger.info("CertificateManagerFactory: useCadi=" + useCadi);
        
        if ( useCadi ) {
        	return new CadiCertificateManager( dmaapConfig );
        }
        return new LegacyCertificateManager( dmaapConfig );
    }


}
