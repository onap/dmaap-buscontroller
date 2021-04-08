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

package org.onap.dmaap.dbcapi.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Dmaap extends DmaapObject {
	
	private String version;
	private String topicNsRoot;
	private String dmaapName;
	private String drProvUrl;
	private	String	bridgeAdminTopic;
	private	String loggingUrl;
	private	String nodeKey;
	private	String	accessKeyOwner;


	// no-op constructor used by framework
	public Dmaap() {
		
	}
	
	public Dmaap( DmaapBuilder builder ) {
		this.version = builder.ver;
		this.topicNsRoot = builder.tnr;
		this.dmaapName = builder.dn;
		this.drProvUrl = builder.dpu;
		this.bridgeAdminTopic = builder.bat;
		this.loggingUrl = builder.lu;
		this.nodeKey = builder.nk;
		this.accessKeyOwner = builder.ako;
		this.setStatus( DmaapObject_Status.NEW );

	}

	public static class DmaapBuilder {
		private String ver;
		private String tnr;
		private String dn;
		private String dpu;
		private String lu;
		private String bat;
		private String nk;
		private String ako;

		public DmaapBuilder setVer(String ver) {
			this.ver = ver;
			return this;
		}

		public DmaapBuilder setTnr(String tnr) {
			this.tnr = tnr;
			return this;
		}

		public DmaapBuilder setDn(String dn) {
			this.dn = dn;
			return this;
		}

		public DmaapBuilder setDpu(String dpu) {
			this.dpu = dpu;
			return this;
		}

		public DmaapBuilder setLu(String lu) {
			this.lu = lu;
			return this;
		}

		public DmaapBuilder setBat(String bat) {
			this.bat = bat;
			return this;
		}

		public DmaapBuilder setNk(String nk) {
			this.nk = nk;
			return this;
		}

		public DmaapBuilder setAko(String ako) {
			this.ako = ako;
			return this;
		}

		public Dmaap createDmaap() {
			return new Dmaap(this);
		}
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTopicNsRoot() {
		return topicNsRoot;
	}

	public void setTopicNsRoot(String topicNsRoot) {
		this.topicNsRoot = topicNsRoot;
	}

	public String getDmaapName() {
		return dmaapName;
	}

	public void setDmaapName(String dmaapName) {
		this.dmaapName = dmaapName;
	}

	public String getDrProvUrl() {
		return drProvUrl;
	}

	public void setDrProvUrl(String drProvUrl) {
		this.drProvUrl = drProvUrl;
	}


	public String getNodeKey() {
		return nodeKey;
	}

	public void setNodeKey(String nodeKey) {
		this.nodeKey = nodeKey;
	}

	public String getAccessKeyOwner() {
		return accessKeyOwner;
	}

	public void setAccessKeyOwner(String accessKeyOwner) {
		this.accessKeyOwner = accessKeyOwner;
	}

	
	public String getBridgeAdminTopic() {
		return bridgeAdminTopic;
	}

	public void setBridgeAdminTopic(String bridgeAdminTopic) {
		this.bridgeAdminTopic = bridgeAdminTopic;
	}

	public String getLoggingUrl() {
		return loggingUrl;
	}

	public void setLoggingUrl(String loggingUrl) {
		this.loggingUrl = loggingUrl;
	}

}
