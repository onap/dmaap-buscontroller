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

import org.onap.dmaap.dbcapi.util.DmaapConfig;

public class LegacyCertificateManager extends CertificateManager {

	public LegacyCertificateManager(Properties properties ) {
		setKeyStoreType( properties.getProperty("KeyStoreType", "jks") );
		setKeyStoreFile( properties.getProperty("KeyStoreFile", "etc/keystore") );
		setKeyStorePassword( properties.getProperty("KeyStorePassword", "changeit") );
		
		setTrustStoreFile( properties.getProperty("TrustStoreFile", "etc/org.onap.dmaap-bc.trust.jks") );
		setTrustStoreType( properties.getProperty("TrustStoreType", "jks") );
		setTrustStorePassword( properties.getProperty("TrustStorePassword", "changeit") );
		ready = true;
	}

}
