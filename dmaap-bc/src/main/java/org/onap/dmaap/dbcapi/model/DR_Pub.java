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

import org.onap.dmaap.dbcapi.util.RandomString;

import java.util.Objects;

@XmlRootElement
public class DR_Pub extends DmaapObject {

	private String dcaeLocationName;
	private String username;
	private String userpwd;
	private String feedId;
	private String pubId;
	
	// NOTE: the following fields are optional in the API but not stored in the DB
	private	String	feedName;
	private String	feedVersion;

	
	public DR_Pub() {
		status = DmaapObject_Status.EMPTY;
		
	}
	
	public DR_Pub( String dLN ) {
		this.dcaeLocationName = dLN;
		this.status = DmaapObject_Status.STAGED;
	}
	
	public DR_Pub( String dLN, 
					String uN,
					String uP,
					String fI,
					String pI ) {
		this.dcaeLocationName = dLN;
		this.username = uN;
		this.userpwd = uP;
		this.feedId = fI;
		this.pubId = pI;
		this.status = DmaapObject_Status.VALID;
	}


	public DR_Pub( String dLN, 
							String uN,
							String uP,
							String fI ) {
		this.dcaeLocationName = dLN;
		this.username = uN;
		this.userpwd = uP;
		this.feedId = fI;
		this.pubId = fI + "." +  DR_Pub.nextKey();
		this.status = DmaapObject_Status.VALID;	
	}
			

	public String getDcaeLocationName() {
		return dcaeLocationName;
	}

	public void setDcaeLocationName(String dcaeLocationName) {
		this.dcaeLocationName = dcaeLocationName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserpwd() {
		return userpwd;
	}

	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}

	public String getFeedId() {
		return feedId;
	}

	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}

	public String getPubId() {
		return pubId;
	}

	public void setPubId(String pubId) {
		this.pubId = pubId;
	}
	
	public void setNextPubId() {
		this.pubId = this.feedId + "." +  DR_Pub.nextKey();
	}
	
	public String getFeedName() {
		return feedName;
	}

	public void setFeedName(String feedName) {
		this.feedName = feedName;
	}

	public String getFeedVersion() {
		return feedVersion;
	}

	public void setFeedVersion(String feedVersion) {
		this.feedVersion = feedVersion;
	}

	public DR_Pub setRandomUserName() {
		RandomString r = new RandomString(15);
		this.username = "tmp_" + r.nextString();	
		return this;
	}
	public DR_Pub setRandomPassword() {
		RandomString r = new RandomString(15);
		this.userpwd = r.nextString();
		return this;
	}

	public static String nextKey() {
		RandomString ri = new RandomString(5);
		return ri.nextString();
		
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DR_Pub dr_pub = (DR_Pub) o;
		return Objects.equals(dcaeLocationName, dr_pub.dcaeLocationName) &&
				Objects.equals(username, dr_pub.username) &&
				Objects.equals(userpwd, dr_pub.userpwd) &&
				Objects.equals(feedId, dr_pub.feedId) &&
				Objects.equals(pubId, dr_pub.pubId);
	}

	@Override
	public int hashCode() {

		return Objects.hash(dcaeLocationName, username, userpwd, feedId, pubId);
	}

	@Override
	public String toString() {
		return "DR_Pub{" +
				"dcaeLocationName='" + dcaeLocationName + '\'' +
				", username='" + username + '\'' +
				", userpwd='" + userpwd + '\'' +
				", feedId='" + feedId + '\'' +
				", pubId='" + pubId + '\'' +
				", feedName='" + feedName + '\'' +
				", feedVersion='" + feedVersion + '\'' +
				'}';
	}
}
