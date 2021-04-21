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
import java.util.Objects;

@XmlRootElement
public class DR_Node extends DmaapObject {
	private String fqdn;
	private String dcaeLocationName;
	private String hostName;
	private String version;
	
	public DR_Node() {
		
	}
	
	public DR_Node( String f,
					String dLN,
					String hN,
					String v ) {
		this.fqdn = f;
		this.dcaeLocationName = dLN;
		this.hostName = hN;
		this.version = v;
	}

	public String getFqdn() {
		return fqdn;
	}

	public void setFqdn(String fqdn) {
		this.fqdn = fqdn;
	}

	public String getDcaeLocationName() {
		return dcaeLocationName;
	}

	public void setDcaeLocationName(String dcaeLocationName) {
		this.dcaeLocationName = dcaeLocationName;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DR_Node dr_node = (DR_Node) o;
		return Objects.equals(fqdn, dr_node.fqdn) &&
				Objects.equals(dcaeLocationName, dr_node.dcaeLocationName) &&
				Objects.equals(hostName, dr_node.hostName) &&
				Objects.equals(version, dr_node.version);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fqdn, dcaeLocationName, hostName, version);
	}
}
