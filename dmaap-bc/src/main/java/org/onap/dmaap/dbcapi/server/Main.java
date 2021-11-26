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

package org.onap.dmaap.dbcapi.server;
import static com.att.eelf.configuration.Configuration.MDC_ALERT_SEVERITY;
import static com.att.eelf.configuration.Configuration.MDC_INSTANCE_UUID;
import static com.att.eelf.configuration.Configuration.MDC_SERVER_FQDN;
import static com.att.eelf.configuration.Configuration.MDC_SERVER_IP_ADDRESS;
import static com.att.eelf.configuration.Configuration.MDC_SERVICE_INSTANCE_ID;
import static com.att.eelf.configuration.Configuration.MDC_TARGET_ENTITY;

import java.net.InetAddress;
import java.util.Properties;
import java.util.UUID;
import org.onap.dmaap.dbcapi.authentication.ApiPerms;
import org.onap.dmaap.dbcapi.authentication.ApiPolicy;
import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.Dmaap;
import org.onap.dmaap.dbcapi.util.DmaapConfig;
import org.onap.dmaap.dbcapi.util.Singleton;
import org.slf4j.MDC;

public class Main extends BaseLoggingClass {

    private static String provFQDN;
    public static String getProvFQDN() {
		return provFQDN;
	}
	public void setProvFQDN(String provFQDN) {
		Main.provFQDN = provFQDN;
	}
	Main() {
    }
    public static void main(String[] args) {
        (new Main()).main();
    }

    private void main()  {
    
        MDC.clear();

        MDC.put(MDC_SERVICE_INSTANCE_ID, "");
        try {
            MDC.put(MDC_SERVER_FQDN, InetAddress.getLocalHost().getHostName());
            MDC.put(MDC_SERVER_IP_ADDRESS, InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
        	errorLogger.error("Error while getting hostname or address", e);
        }
        MDC.put(MDC_INSTANCE_UUID, UUID.randomUUID().toString());
        MDC.put(MDC_ALERT_SEVERITY, "0");
        MDC.put(MDC_TARGET_ENTITY, "DCAE");

        appLogger.info("Started.");
        Properties parameters = DmaapConfig.getConfig();
        setProvFQDN( parameters.getProperty("ProvFQDN", "ProvFQDN.notset.com"));

		// for fresh installs, we may come up with no dmaap name so need to have a way for Controller to talk to us
		Singleton<Dmaap> dmaapholder = DatabaseClass.getDmaap();
		String name = dmaapholder.get().getDmaapName();
		ApiPolicy apiPolicy = new ApiPolicy();
		if ( apiPolicy.isPermissionClassSet() && (name == null || name.isEmpty())) {
			ApiPerms p = new ApiPerms();
			p.setBootMap();
		}

        try {
        	new JettyServer(parameters);
        } catch (Exception e) {
            errorLogger.error("Unable to start Jetty " + DmaapConfig.getConfigFileName(), e);
            System.exit(1);
        }

    }

}
