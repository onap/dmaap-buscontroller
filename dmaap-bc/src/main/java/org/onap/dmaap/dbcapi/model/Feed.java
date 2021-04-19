/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 *
 * Modifications Copyright (C) 2019 IBM.
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import org.json.simple.*;
import org.json.simple.parser.*;
import org.onap.dmaap.dbcapi.service.DmaapService;

@XmlRootElement
public class Feed extends DmaapObject {
		
		private String feedId;

		private String feedName;
		private String feedVersion;
		private String feedDescription;
		private String owner;
		private String asprClassification;
		private String publishURL;
		private String subscribeURL;
		private	boolean	suspended;
		private String logURL;
		private String formatUuid;

		private	ArrayList<DR_Pub> pubs;
		private ArrayList<DR_Sub> subs;


		public Feed() {
			this.pubs = new ArrayList<>();
			this.subs = new ArrayList<>();
			this.setStatus( DmaapObject_Status.EMPTY );

		}

		public	Feed( String name,
						String version,
						String description,
						String owner,
						String aspr) {
			this.feedName = name;
			this.feedVersion = version;
			this.feedDescription = description;
			this.owner = owner;
			this.asprClassification = aspr;
			this.pubs = new ArrayList<>();
			this.subs = new ArrayList<>();
			this.setStatus( DmaapObject_Status.NEW );

		}

		// expects a String in JSON format, with known fields to populate Feed object
		public Feed ( String json ) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
			try {
				jsonObj = (JSONObject) parser.parse( json );
			} catch ( ParseException pe ) {
				logger.error( "Error parsing provisioning data: " + json );
				this.setStatus( DmaapObject_Status.INVALID );
				return;
			}
			this.setFeedName( (String) jsonObj.get("name"));

			this.setFeedVersion( (String) jsonObj.get("version"));
			this.setFeedDescription( (String) jsonObj.get("description"));
			this.setOwner( (String) jsonObj.get("publisher"));

			this.setSuspended( (boolean) jsonObj.get("suspend"));
			JSONObject links = (JSONObject) jsonObj.get("links");
			String url = (String) links.get("publish");
			this.setPublishURL( url );
			this.setFeedId( url.substring( url.lastIndexOf('/')+1, url.length() ));
			logger.info( "feedid="+ this.getFeedId() );
			this.setSubscribeURL( (String) links.get("subscribe") );
			this.setLogURL( (String) links.get("log") );
			JSONObject auth = (JSONObject) jsonObj.get("authorization");
			this.setAsprClassification( (String) auth.get("classification"));
			JSONArray pubs = (JSONArray) auth.get( "endpoint_ids");
			int i;
			ArrayList<DR_Pub> dr_pub = new ArrayList<>();
			this.subs = new ArrayList<>();

			for( i = 0; i < pubs.size(); i++ ) {
				JSONObject entry = (JSONObject) pubs.get(i);
				dr_pub.add(  new DR_Pub( "someLocation",
						(String) entry.get("id"),
						(String) entry.get("password"),
						this.getFeedId(),
						this.getFeedId() + "." +  DR_Pub.nextKey() ));

			}
			this.setPubs( dr_pub );

			this.setStatus( DmaapObject_Status.VALID );

		}

		

		public boolean isSuspended() {
			return suspended;
		}

		public void setSuspended(boolean suspended) {
			this.suspended = suspended;
		}

		public String getSubscribeURL() {
			return subscribeURL;
		}

		public void setSubscribeURL(String subscribeURL) {
			this.subscribeURL = subscribeURL;
		}

		public String getFeedId() {
			return feedId;
		}

		public void setFeedId(String feedId) {
			this.feedId = feedId;
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

		public String getFeedDescription() {
			return feedDescription;
		}

		public void setFeedDescription(String feedDescription) {
			this.feedDescription = feedDescription;
		}

		public String getOwner() {
			return owner;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public String getAsprClassification() {
			return asprClassification;
		}

		public void setAsprClassification(String asprClassification) {
			this.asprClassification = asprClassification;
		}

		public String getPublishURL() {
			return publishURL;
		}

		public void setPublishURL(String publishURL) {
			this.publishURL = publishURL;
		}

		public String getLogURL() {
			return logURL;
		}

		public void setLogURL(String logURL) {
			this.logURL = logURL;
		}


		
		public String getFormatUuid() {
			return formatUuid;
		}

		public void setFormatUuid(String formatUuid) {
			this.formatUuid = formatUuid;
		}

		// returns the Feed object in JSON that conforms to DR Prov Server expectations
		public String toProvJSON() {

			String postJSON = String.format("{\"name\": \"%s\", \"version\": \"%s\", \"description\": \"%s\", \"suspend\": %s, \"authorization\": { \"classification\": \"%s\", ",
					this.getFeedName(), 
					this.getFeedVersion(),
					this.getFeedDescription(),
					this.isSuspended() ,
					this.getAsprClassification()
					);
			int i;
			postJSON += "\"endpoint_addrs\": [],\"endpoint_ids\": [";
			String comma = "";
			for( i = 0 ; i < pubs.size(); i++) {
				postJSON +=	String.format("	%s{\"id\": \"%s\",\"password\": \"%s\"}", 
						comma,
						pubs.get(i).getUsername(),
						pubs.get(i).getUserpwd()
						) ;
				comma = ",";
			}
			postJSON += "]}}";
			
			logger.info( "postJSON=" + postJSON);		
			return postJSON;
		}
		
		public ArrayList<DR_Pub> getPubs() {
			return pubs;
		}

		public void setPubs( ArrayList<DR_Pub> pubs) {
			this.pubs = pubs;
		}

		public ArrayList<DR_Sub> getSubs() {
			return subs;
		}

		public void setSubs( ArrayList<DR_Sub> subs) {
			this.subs = subs;
		}

		public byte[] getBytes() {
			return toProvJSON().getBytes(StandardCharsets.UTF_8);
		}
		
		public static String getSubProvURL( String feedId ) {
			return new DmaapService().getDmaap().getDrProvUrl() + "/subscribe/" + feedId;
		}

		@Override
		public String toString() {
			String rc = String.format ( "Feed: {feedId=%s feedName=%s feedVersion=%s feedDescription=%s owner=%s asprClassification=%s publishURL=%s subscriberURL=%s suspended=%s logURL=%s formatUuid=%s}",
					feedId,
					feedName,
					feedVersion,
					feedDescription,
					owner,
					asprClassification,
					publishURL,
					subscribeURL,
					suspended,
					logURL,
					formatUuid

		
					);

			for( DR_Pub pub: pubs) {
				rc += "\n" + pub.toString();
			}

			for( DR_Sub sub: subs ) {
				rc += "\n" + sub.toString();
			}
			return rc;
		}
}
