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

import org.onap.dmaap.dbcapi.util.DmaapConfig;
import org.onap.dmaap.dbcapi.util.DmaapTimestamp;



@XmlRootElement
public class MR_Cluster extends DmaapObject {

	private String dcaeLocationName;
	private String fqdn;
	private	DmaapTimestamp lastMod;
	private	String	topicProtocol;
	private String	topicPort;
	private	String	replicationGroup;
	private	String	sourceReplicationPort;
	private	String	targetReplicationPort;

	
	// TODO: make this a system property
	private static  String defaultTopicProtocol;
	private static	 String defaultTopicPort;
	private static  String defaultReplicationGroup;
	private static  String defaultSourceReplicationPort;
	private static  String defaultTargetReplicationPort;
	
	private static void setDefaults() {
		/* boolean been_here = false;
		if ( been_here ) {
			return;
		} */
		DmaapConfig dc = (DmaapConfig)DmaapConfig.getConfig();
		defaultTopicProtocol = dc.getProperty("MR.TopicProtocol", "https");
		defaultTopicPort = dc.getProperty( "MR.TopicPort", "3905");
		defaultReplicationGroup = dc.getProperty( "MR.ReplicationGroup", "" );
		defaultSourceReplicationPort = dc.getProperty( "MR.SourceReplicationPort", "2181");
		defaultTargetReplicationPort = dc.getProperty( "MR.TargetReplicationPort", "9092");
		// been_here = true;
	}


	public MR_Cluster() {
		setDefaults();
		this.topicProtocol = defaultTopicProtocol;
		this.topicPort = defaultTopicPort;
		this.replicationGroup = null;
		this.sourceReplicationPort = defaultSourceReplicationPort;
		this.targetReplicationPort = defaultTargetReplicationPort;
		this.lastMod = new DmaapTimestamp();
		this.lastMod.mark();

		debugLogger.debug( "MR_Cluster constructor " + this.lastMod );
		
	}

	// new style constructor
	public MR_Cluster( String dLN,
			String f,
			String prot,
			String port) {
		setDefaults();
		this.dcaeLocationName = dLN;
		this.fqdn = f;

		if ( prot == null || prot.isEmpty() ) {
			this.topicProtocol = defaultTopicProtocol;
		} else {
			this.topicProtocol = prot;
		}
		if ( port == null || port.isEmpty() ) {
			this.topicPort = defaultTopicPort;
		} else {
			this.topicPort = port;
		}

		this.replicationGroup = defaultReplicationGroup;
		this.sourceReplicationPort = defaultSourceReplicationPort;
		this.targetReplicationPort = defaultTargetReplicationPort;

		this.lastMod = new DmaapTimestamp();
		this.lastMod.mark();
		
		debugLogger.debug( "MR_Cluster constructor w initialization complete" + this.lastMod.getVal() );
	}

	public MR_Cluster( String dLN,
			String f,
			String prot,
			String port,
			String repGroup,
			String sourceRepPort,
			String targetRepPort ) {
		setDefaults();
		this.dcaeLocationName = dLN;
		this.fqdn = f;

		if ( prot == null || prot.isEmpty() ) {
			this.topicProtocol = defaultTopicProtocol;
		} else {
			this.topicProtocol = prot;
		}
		if ( port == null || port.isEmpty() ) {
			this.topicPort = defaultTopicPort;
		} else {
			this.topicPort = port;
		}
		if ( repGroup == null || repGroup.isEmpty() ) {
			this.replicationGroup = defaultReplicationGroup;
		} else {
			this.replicationGroup = repGroup;
		}
		if ( sourceRepPort == null || sourceRepPort.isEmpty()) {
			this.sourceReplicationPort = defaultSourceReplicationPort;
		} else {
			this.sourceReplicationPort = sourceRepPort;
		}
		if ( targetRepPort == null || targetRepPort.isEmpty()) {
			this.targetReplicationPort = defaultTargetReplicationPort;
		} else {
			this.targetReplicationPort = targetRepPort;
		}
				
		this.lastMod = new DmaapTimestamp();
		this.lastMod.mark();
		
		debugLogger.debug( "MR_Cluster constructor w initialization complete" + this.lastMod.getVal() );
	}
	public String getDcaeLocationName() {
		return dcaeLocationName;
	}

	public void setDcaeLocationName(String dcaeLocationName) {
		this.dcaeLocationName = dcaeLocationName;
	}

	public String getFqdn() {
		return fqdn;
	}

	public void setFqdn(String fqdn) {
		this.fqdn = fqdn;
	}


	public String getTopicProtocol() {
		return topicProtocol;
	}

	public void setTopicProtocol(String topicProtocol) {
		this.topicProtocol = topicProtocol;
	}

	public String getTopicPort() {
		return topicPort;
	}

	public void setTopicPort(String topicPort) {
		this.topicPort = topicPort;
	}

	public String getReplicationGroup() {
		return replicationGroup;
	}

	public void setReplicationGroup(String replicationGroup) {
		this.replicationGroup = replicationGroup;
	}




	public String getSourceReplicationPort() {
		return sourceReplicationPort;
	}



	public void setSourceReplicationPort(String sourceReplicationPort) {
		this.sourceReplicationPort = sourceReplicationPort;
	}



	public String getTargetReplicationPort() {
		return targetReplicationPort;
	}



	public void setTargetReplicationPort(String targetReplicationPort) {
		this.targetReplicationPort = targetReplicationPort;
	}



	public String genTopicURL(String overideFqdn, String topic) {

		StringBuilder str = new StringBuilder( topicProtocol );
		str.append("://")
			.append( overideFqdn != null ? overideFqdn : fqdn)
			.append(":")
			.append(topicPort)
			.append("/events/")
			.append(topic);
		
		return str.toString();


	}


}
