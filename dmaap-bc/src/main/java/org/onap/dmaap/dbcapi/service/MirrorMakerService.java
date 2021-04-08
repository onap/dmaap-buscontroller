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

package org.onap.dmaap.dbcapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;









//import org.openecomp.dmaapbc.aaf.AndrewDecryptor;
import org.onap.dmaap.dbcapi.aaf.AafDecrypt;
import org.onap.dmaap.dbcapi.client.MrTopicConnection;
import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.logging.DmaapbcLogMessageEnum;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.MR_Cluster;
import org.onap.dmaap.dbcapi.model.MirrorMaker;
import org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status;
import org.onap.dmaap.dbcapi.util.DmaapConfig;
import org.onap.dmaap.dbcapi.util.RandomInteger;

public class MirrorMakerService extends BaseLoggingClass {
	
	private Map<String, MirrorMaker> mirrors = DatabaseClass.getMirrorMakers();
	private static MrTopicConnection prov;
	private static AafDecrypt decryptor;
	
	static final String PROV_USER_PROPERTY = "MM.ProvUserMechId";
	static final String PROV_PWD_PROPERTY = "MM.ProvUserPwd";
	static final String PROV_PWD_DEFAULT = "pwdNotSet";
	static final String SOURCE_REPLICATION_PORT_PROPERTY = "MR.SourceReplicationPort";
	static final String SOURCE_REPLICATION_PORT_DEFAULT = "9092";
	static final String TARGET_REPLICATION_PORT_PROPERTY = "MR.TargetReplicationPort";
	static final String TARGET_REPLICATION_PORT_DEFAULT = "2181";
	
	private static String provUser;
	private static String provUserPwd;
	private static String defaultProducerPort;
	private static String defaultConsumerPort;
	private static String centralFqdn;
	private int maxTopicsPerMM;
	private boolean mmPerMR;
	
	public MirrorMakerService() {
		super();
		decryptor = new AafDecrypt();
		DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
		provUser = p.getProperty(PROV_USER_PROPERTY);
		provUserPwd = decryptor.decrypt(p.getProperty( PROV_PWD_PROPERTY, PROV_PWD_DEFAULT ));
		defaultProducerPort = p.getProperty( SOURCE_REPLICATION_PORT_PROPERTY, SOURCE_REPLICATION_PORT_DEFAULT );
		defaultConsumerPort = p.getProperty( TARGET_REPLICATION_PORT_PROPERTY, TARGET_REPLICATION_PORT_DEFAULT );	
		centralFqdn = p.getProperty("MR.CentralCname", "notSet");
		maxTopicsPerMM = Integer.valueOf( p.getProperty( "MaxTopicsPerMM", "5"));
		mmPerMR = "true".equalsIgnoreCase(p.getProperty("MirrorMakerPerMR", "true"));
	}

	// will create a MM on MMagent if needed
	// will update the MMagent whitelist with all topics for this MM
	public MirrorMaker updateMirrorMaker( MirrorMaker mm ) {
		logger.info( "updateMirrorMaker");
	
		prov = new MrTopicConnection( provUser, provUserPwd );
	
		DmaapService dmaap = new DmaapService();
		MR_ClusterService clusters = new MR_ClusterService();
		MR_Cluster target_cluster = null;
		String override = null;
		
		if ( ! mmPerMR ) {
			// in ECOMP, MM Agent is only deployed at central, so this case is needed for backwards compatibility
			//  we use a cname for the central MR cluster that is active, and provision on agent topic on that target
			// but only send 1 message so MM Agents can read it relying on kafka delivery
			for( MR_Cluster cluster: clusters.getCentralClusters() ) {

				target_cluster = cluster;
				override = centralFqdn;
				// we only want to send one message even if there are multiple central clusters
				break;
			
			} 
		} else {
			// In ONAP deployment architecture, the MM Agent is deployed with each target MR
			target_cluster = clusters.getMr_ClusterByFQDN(mm.getTargetCluster());
			override = null;
		}
		
		prov.makeTopicConnection(target_cluster, dmaap.getBridgeAdminFqtn(), override  );
		ApiError resp = prov.doPostMessage(mm.createMirrorMaker( defaultConsumerPort, defaultProducerPort ));
		if ( ! resp.is2xx() ) {

			errorLogger.error( DmaapbcLogMessageEnum.MM_PUBLISH_ERROR, "create MM", Integer.toString(resp.getCode()), resp.getMessage());
			mm.setStatus(DmaapObject_Status.INVALID);
		} else {
			prov.makeTopicConnection(target_cluster, dmaap.getBridgeAdminFqtn(), override );
			resp = prov.doPostMessage(mm.getWhitelistUpdateJSON());
			if ( ! resp.is2xx()) {
				errorLogger.error( DmaapbcLogMessageEnum.MM_PUBLISH_ERROR,"MR Bridge", Integer.toString(resp.getCode()), resp.getMessage());
				mm.setStatus(DmaapObject_Status.INVALID);
			} else {
				mm.setStatus(DmaapObject_Status.VALID);
			}
		}

		mm.setLastMod();
		return mirrors.put( mm.getMmName(), mm);
	}
	public MirrorMaker getMirrorMaker( String part1, String part2, int index ) {
		String targetPart;

		// original mm names did not have any index, so leave off index 0 for
		// backwards compatibility
		if ( index == 0 ) {
			targetPart = part2;
		} else {
			targetPart = part2 + "_" + index;
		}
		logger.info( "getMirrorMaker using " + part1 + " and " + targetPart );
		return mirrors.get(MirrorMaker.genKey(part1, targetPart));
	}
	public MirrorMaker getMirrorMaker( String part1, String part2 ) {
		logger.info( "getMirrorMaker using " + part1 + " and " + part2 );
		return mirrors.get(MirrorMaker.genKey(part1, part2));
	}	
	public MirrorMaker getMirrorMaker( String key ) {
		logger.info( "getMirrorMaker using " + key);
		return mirrors.get(key);
	}
	
	
	public void delMirrorMaker( MirrorMaker mm ) {
		logger.info( "delMirrorMaker");
		mirrors.remove(mm.getMmName());
	}
	
	// TODO: this should probably return sequential values or get replaced by the MM client API
	// but it should be sufficient for initial 1610 development
	public static String genTransactionId() {
		RandomInteger ri = new RandomInteger(100000);
	    int randomInt = ri.next();
	    return Integer.toString(randomInt);
	}
	public List<String> getAllMirrorMakers() {
		List<String> ret = new ArrayList<String>();
		for( String key: mirrors.keySet()) {
			ret.add( key );
		}
		
		return ret;
	}
	
	public MirrorMaker findNextMM( String source, String target, String fqtn ) {
		int i = 0;
		MirrorMaker mm = null;
		while( mm == null ) {
			
			mm = this.getMirrorMaker( source, target, i);
			if ( mm == null ) {
				mm = new MirrorMaker(source, target, i);
			}
			if ( mm.getTopics().contains(fqtn) ) {
				break;
			}
			if ( mm.getTopicCount() >= maxTopicsPerMM ) {
				logger.info( "getNextMM: MM " + mm.getMmName() + " has " + mm.getTopicCount() + " topics.  Moving to next MM");
				i++;
				mm = null;
			}
		}
	 
		
		return mm;
	}

	public MirrorMaker splitMM( MirrorMaker orig ) {
		
		String source = orig.getSourceCluster();
		String target = orig.getTargetCluster();
		
		
		ArrayList<String> whitelist = orig.getTopics();
		while( whitelist.size() > maxTopicsPerMM ) {
			
			int last = whitelist.size() - 1;
			String topic = whitelist.get(last);
			whitelist.remove(last);
			MirrorMaker mm = this.findNextMM( source, target, "aValueThatShouldNotMatchAnything" );
			mm.addTopic(topic);	
			this.updateMirrorMaker(mm);
		}
		
		orig.setTopics(whitelist);

		return orig;
		
	}
	
	public static String getProvUser() {
		return provUser;
	}

	public static void setProvUser(String provUser) {
		MirrorMakerService.provUser = provUser;
	}

	public static String getProvUserPwd() {
		return provUserPwd;
	}

	public static void setProvUserPwd(String provUserPwd) {
		MirrorMakerService.provUserPwd = provUserPwd;
	}

	public static String getDefaultProducerPort() {
		return defaultProducerPort;
	}

	public static void setDefaultProducerPort(String defaultProducerPort) {
		MirrorMakerService.defaultProducerPort = defaultProducerPort;
	}

	public static String getDefaultConsumerPort() {
		return defaultConsumerPort;
	}

	public static void setDefaultConsumerPort(String defaultConsumerPort) {
		MirrorMakerService.defaultConsumerPort = defaultConsumerPort;
	}

}
