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

import org.onap.dmaap.dbcapi.service.MirrorMakerService;

import java.util.ArrayList;

public class MirrorMaker extends DmaapObject {

	private String	sourceCluster;
	private String	targetCluster;
	private String	mmName;
	private	ArrayList<String> topics;  //re-using this var name for backwards DB compatibility

	public MirrorMaker(){
		
	}

	public MirrorMaker(String source, String target, int i) {
		initMM( source, target );
		// original mm names did not have any index, so leave off index 0 for
		// backwards compatibility
		if ( i != 0 ) {
			String n = this.getMmName() + "_" + i;
			this.setMmName(n);
		}
	}

	public MirrorMaker(String source, String target) {
		initMM( source, target );
	}
	
	private void initMM(String source, String target) {
		sourceCluster = source;
		targetCluster = target;
		mmName = genKey(source, target);
		topics = new ArrayList<>();

	}
	
	public String getMmName() {
		return mmName;
	}

	public void setMmName(String mmName) {
		this.mmName = mmName;
	}

	// returns the JSON for MM message containing which Topics to replicate
	/* 
	 * example:
	 * 
			{
			    "messageID":"12349",
			    "updateWhiteList":
			        {
			            "name":"Global1ToGlobal3",
			            "whitelist":"org.openecomp.dcae.topic1,org.openecomp.dcae.topic2"
			        }
			}   
	 */
	public String getWhitelistUpdateJSON() {
		StringBuilder str = new StringBuilder( "{ \"messageID\": \"" + MirrorMakerService.genTransactionId() + "\", \"updateWhiteList\": {"  );
		str.append( " \"name\": \"" + this.getMmName() + "\", \"whitelist\": \"" );
		int numTargets = 0;

		for (String rv: topics) {
			if ( numTargets > 0 ) {
				str.append( ",");
			}
			str.append( rv );
			numTargets++;
		}
		str.append( "\" } }" );
		
		return str.toString();
	}
	
	// returns the JSON for MM message indicating that a MM agent is needed between two clusters
	// example:
	/*
	 * 
			{
			    "messageID":"12345"
			    "createMirrorMaker":
			        {
			            "name":"Global1ToGlobal2",
			            "consumer":"192.168.0.1:2181",
			            "producer":"192.168.0.2:9092"
			        }
			}
	 */
	public String createMirrorMaker( String consumerPort, String producerPort ) {
		StringBuilder str = new StringBuilder( "{ \"messageID\": \"" + MirrorMakerService.genTransactionId() + "\", \"createMirrorMaker\": {"  );
		str.append( " \"name\": \"" + this.getMmName() + "\", " );
		str.append( " \"consumer\": \"" + this.sourceCluster + ":" + consumerPort + "\", " );
		str.append( " \"producer\": \"" + this.targetCluster + ":" + producerPort + "\", ");
		
		str.append( " \"numStreams\": \"10\" } }" );
		
		return str.toString();
	}

	public String getSourceCluster() {
		return sourceCluster;
	}

	public void setSourceCluster(String sourceCluster) {
		this.sourceCluster = sourceCluster;
	}

	public String getTargetCluster() {
		return targetCluster;
	}

	public void setTargetCluster(String targetCluster) {
		this.targetCluster = targetCluster;
	}

	public ArrayList<String> getTopics() {
		return topics;
	}

	public void setTopics(ArrayList<String> topics) {
		this.topics = topics;
	}

	public static String genKey( String s, String t) {
		StringBuilder str = new StringBuilder();
		str.append(s);
		str.append("-To-");
		str.append(t);
		return str.toString();
	}

	public void addTopic( String topic ) {
		if ( ! topics.contains(topic)) {	
			topics.add(topic);
		}
		logger.info( "Mirrormaker.addTopic: topic=" + topic + " . Now have " + topics.size() + " topics" );
	}

	public int getTopicCount() {
		return topics.size();
	}
}
