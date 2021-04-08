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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.onap.aaf.cadi.PropAccess;

public class CadiCertificateManager extends CertificateManager {
	private PropAccess propAccess;	
	
	CadiCertificateManager( Properties properties )  {
		String cadiPropsFile = properties.getProperty("cadi.properties", "etc/org.onap.dmaa-bc.props");
		logger.info( "using cadi properties in ", cadiPropsFile);
		
		propAccess = new PropAccess();
		ready = true;
		try {
			propAccess.load( new FileInputStream( cadiPropsFile ));
		} catch ( IOException e ) {
			logger.error( "Failed to load props file: " + cadiPropsFile + "\n" +  e.getMessage());
			ready = false;
		}
		setKeyStoreType( "jks");
		setKeyStoreFile( propAccess.getProperty("cadi_keystore") );
		setKeyStorePassword( decryptPass( propAccess.getProperty("cadi_keystore_password_jks" ) ));

		setTrustStoreType( "jks");
		setTrustStoreFile( propAccess.getProperty("cadi_truststore" ) );
		setTrustStorePassword( decryptPass( propAccess.getProperty("cadi_truststore_password" ) ));
	}

	private String decryptPass( String password ) {
		String clear = null;
		try {
			clear = propAccess.decrypt(password, false );
		} catch (IOException e) {
			logger.error( "Failed to decrypt " + password + ": " + e.getMessage() );
		}
		return clear;
	}
}
