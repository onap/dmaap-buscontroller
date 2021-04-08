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

import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;

public abstract class CertificateManager extends BaseLoggingClass{
	
	class cmAttribute {
		private String type;
		private	String file;
		private	String password;
		
		private String getType() {
			return type;
		}
		private void setType(String certificateType) {
			this.type = certificateType;
		}
		private String getFile() {
			return file;
		}
		private void setFile(String keyStoreFile) {
			this.file = keyStoreFile;
		}
		private void setPassword( String pwd ) {
			this.password = pwd;
		}
		private String getPassword() {
			return password;
		}
	}

	private cmAttribute keyStore;
	private cmAttribute	trustStore;
	protected boolean ready;

	CertificateManager() {
		keyStore = new cmAttribute();
		trustStore = new cmAttribute();
		ready = false;
	}

	public boolean isReady() {
		return ready;
	}
	
	public String getKeyStoreType() {
		return keyStore.getType();
	}
	public void setKeyStoreType(String certificateType) {
		this.keyStore.setType( certificateType) ;
	}
	public String getKeyStoreFile() {
		return keyStore.getFile();
	}
	public void setKeyStoreFile(String keyStoreFile) {
		this.keyStore.setFile(keyStoreFile);
	}

	public String getKeyStorePassword() {
		return keyStore.getPassword();
	}
	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStore.setPassword(keyStorePassword);
	}
	public String getTrustStoreType() {
		return trustStore.getType();
	}
	public void setTrustStoreType( String type ) {
		this.trustStore.setType(type);
	}
	public String getTrustStoreFile() {
		return trustStore.getFile();
	}
	public void setTrustStoreFile(String trustStoreFile) {
		this.trustStore.setFile(trustStoreFile);
	}
	public String getTrustStorePassword() {
		return trustStore.getPassword();
	}
	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStore.setPassword(trustStorePassword);
	}

}
