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

package org.onap.dmaap.dbcapi.util;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import java.io.*;
import java.security.KeyStore;
import java.util.*;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.onap.dmaap.dbcapi.server.CertificateManager;
import org.onap.dmaap.dbcapi.server.JettyServer;

public class DmaapConfig extends Properties	{

	private static final EELFLogger logger = EELFManager.getInstance().getLogger(DmaapConfig.class);
	private static final long serialVersionUID = 1L;
	private static final String CONFIG_FILE_NAME = System.getProperty("ConfigFile", "/opt/app/config/conf/dmaapbc.properties");
	private static final Properties config = new DmaapConfig();

	public static Properties getConfig() {
		return(config);
	}
	public static String getConfigFileName() {
		return(CONFIG_FILE_NAME);
	}
	private DmaapConfig() {
		try (InputStream is = new FileInputStream(CONFIG_FILE_NAME)){
			load(is);
		} catch (Exception e) {
			logger.error("Unable to load configuration file " + CONFIG_FILE_NAME);
			System.exit(1);
		}
	}

	public static SSLSocketFactory getSSLSocketFactory() {
		SSLSocketFactory factory = null;
		try {
			CertificateManager cm = JettyServer.getCertificateManager();
			String truststore = cm.getTrustStoreFile();
			KeyStore ts = KeyStore.getInstance(cm.getTrustStoreType());
			try (InputStream in = new FileInputStream(truststore)) {
				ts.load(in, cm.getTrustStorePassword().toCharArray());
			}
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ts);
			TrustManager[] tm = tmf.getTrustManagers();
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, tm, null);
			factory = sslContext.getSocketFactory();
		} catch (Exception e) {
			logger.error("Exception thrown trying to get SSLSocketFactory: ", e);
		}
		return factory;
	}
	
}
