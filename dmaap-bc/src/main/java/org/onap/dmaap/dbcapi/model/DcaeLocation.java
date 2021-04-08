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
public class DcaeLocation extends DmaapObject {
	private String clli;
	private String dcaeLayer;
	private String dcaeLocationName;
	private String openStackAvailabilityZone;
	private String subnet;

	

	public DcaeLocation() {

	}

	public DcaeLocation( String c,
						String dL,
						String dLN,
						String oSAZ,
						String s ) {
		
		this.clli = c;
		this.dcaeLayer = dL;
		this.dcaeLocationName = dLN;
		this.openStackAvailabilityZone = oSAZ;
		this.subnet = s;
	}

	public String getClli() {
		return clli;
	}

	public void setClli(String clli) {
		this.clli = clli;
	}

	public String getDcaeLayer() {
		return dcaeLayer;
	}

	public void setDcaeLayer(String dcaeLayer) {
		this.dcaeLayer = dcaeLayer;
	}
	public boolean isCentral() {
		return dcaeLayer != null && dcaeLayer.contains("central");
	}
	public boolean isLocal() {
		return dcaeLayer != null && dcaeLayer.contains("local");
	}

	public String getDcaeLocationName() {
		return dcaeLocationName;
	}

	public void setDcaeLocationName(String dcaeLocationName) {
		this.dcaeLocationName = dcaeLocationName;
	}
	


	public String getOpenStackAvailabilityZone() {
		return openStackAvailabilityZone;
	}

	public void setOpenStackAvailabilityZone(String openStackAvailabilityZone) {
		this.openStackAvailabilityZone = openStackAvailabilityZone;
	}
	
	public String getSubnet() {
		return subnet;
	}

	public void setSubnet(String subnet) {
		this.subnet = subnet;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DcaeLocation that = (DcaeLocation) o;
		return Objects.equals(clli, that.clli) &&
				Objects.equals(dcaeLayer, that.dcaeLayer) &&
				Objects.equals(dcaeLocationName, that.dcaeLocationName) &&
				Objects.equals(openStackAvailabilityZone, that.openStackAvailabilityZone) &&
				Objects.equals(subnet, that.subnet);
	}

	@Override
	public int hashCode() {

		return Objects.hash(clli, dcaeLayer, dcaeLocationName, openStackAvailabilityZone, subnet);
	}
}
