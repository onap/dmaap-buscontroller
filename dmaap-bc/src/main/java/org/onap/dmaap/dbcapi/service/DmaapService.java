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

package org.onap.dmaap.dbcapi.service;

import java.util.ArrayList;
import org.onap.dmaap.dbcapi.aaf.AafService;
import org.onap.dmaap.dbcapi.aaf.AafServiceFactory;
import org.onap.dmaap.dbcapi.aaf.DmaapGrant;
import org.onap.dmaap.dbcapi.aaf.DmaapPerm;
import org.onap.dmaap.dbcapi.aaf.AafService.ServiceType;
import org.onap.dmaap.dbcapi.authentication.ApiPerms;
import org.onap.dmaap.dbcapi.authentication.ApiPolicy;
import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.logging.DmaapbcLogMessageEnum;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.Dmaap;
import org.onap.dmaap.dbcapi.model.MR_Client;
import org.onap.dmaap.dbcapi.model.Topic;
import org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status;
import org.onap.dmaap.dbcapi.util.DmaapConfig;
import org.onap.dmaap.dbcapi.util.Singleton;

public class DmaapService  extends BaseLoggingClass  {

	
	private Singleton<Dmaap> dmaapholder = DatabaseClass.getDmaap();
	private static String noEnvironmentPrefix;
	
	
	String topicFactory; // = "org.openecomp.dcae.dmaap.topicFactory";
	String topicMgrRole; // = "org.openecomp.dmaapBC.TopicMgr";
	
	private boolean multiSite;
	
	
	public DmaapService() {
		DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
		topicFactory = p.getProperty("MR.TopicFactoryNS", "MR.topicFactoryNS.not.set");
		topicMgrRole = p.getProperty("MR.TopicMgrRole", "MR.TopicMgrRole.not.set" );

		multiSite = "true".equalsIgnoreCase(p.getProperty("MR.multisite", "true"));
		noEnvironmentPrefix = p.getProperty( "AAF.NoEnvironmentPrefix", "org.onap");
		
		logger.info( "DmaapService settings: " + 
				" topicFactory=" + topicFactory +
				" topicMgrRole=" + topicMgrRole +
				
				" multisite=" + multiSite +
				" noEnvironmentPrefix=" + noEnvironmentPrefix
				);

		Dmaap dmaap = dmaapholder.get();
		logger.info( "DmaapService object values: " +
				" dmaapName=" + dmaap.getDmaapName() +
				" drProvURL=" + dmaap.getDrProvUrl() +
				" version="+ dmaap.getVersion()
				);
		
	}
	
	public Dmaap getDmaap() {
		logger.info( "entering getDmaap()" );
		return(dmaapholder.get());
	}
	
	public Dmaap addDmaap( Dmaap nd ) {
		
		logger.info( "entering addDmaap()" );
		Dmaap dmaap = dmaapholder.get();
		if ( dmaap.getVersion().equals( "0")) {

			nd.setLastMod();
			dmaapholder.update(nd);
			
			AafService aaf = new AafServiceFactory().initAafService(ServiceType.AAF_Admin);
			ApiPolicy apiPolicy = new ApiPolicy();
			if ( apiPolicy.isPermissionClassSet() ) {
				ApiPerms p = new ApiPerms();
				p.setEnvMap();
			}
			boolean anythingWrong = false;
			
			if ( multiSite ) {
				anythingWrong = setTopicMgtPerms(  nd,  aaf ) || createMmaTopic();
			}
					
			if ( anythingWrong ) {
				dmaap.setStatus(DmaapObject_Status.INVALID); 
			}
			else {
				dmaap.setStatus(DmaapObject_Status.VALID);  
			}
			dmaap.setLastMod();
			dmaapholder.update(dmaap);

			return dmaap;
		
		}
		else { 
			return dmaap;
		}
	}
	
	public Dmaap updateDmaap( Dmaap nd ) {
		logger.info( "entering updateDmaap()" );
		
		boolean anythingWrong = false;

		Dmaap dmaap = dmaapholder.get();
		
		// some triggers for when we attempt to reprovision perms and MMA topic:
		// - if the DMaaP Name changes
		// - if the version is 0  (this is a handy test to force this processing by updating the DB)
		// - if the object is invalid, reprocessing might fix it.
		if ( ! dmaap.isStatusValid()  || ! nd.getDmaapName().equals(dmaap.getDmaapName()) || dmaap.getVersion().equals( "0") ) {
			nd.setLastMod();
			dmaapholder.update(nd);  //need to set this so the following perms will pick up any new vals.
			//dcaeTopicNs = dmaapholder.get().getTopicNsRoot();
			ApiPolicy apiPolicy = new ApiPolicy();
			if ( apiPolicy.isPermissionClassSet()) {
				ApiPerms p = new ApiPerms();
				p.setEnvMap();
			}
			AafService aaf = new AafServiceFactory().initAafService(ServiceType.AAF_Admin);
			if ( multiSite ) {
				anythingWrong = setTopicMgtPerms(  nd,  aaf ) || createMmaTopic();
			}
		}
					
		if ( anythingWrong ) {
			nd.setStatus(DmaapObject_Status.INVALID); 
		}
		else {
			nd.setStatus(DmaapObject_Status.VALID);  
		}
		nd.setLastMod();
		dmaapholder.update(nd);  // may need to update status...
		return(dmaapholder.get());
		
	}
	
	public String getTopicPerm(){
		Dmaap dmaap = dmaapholder.get();
		return getTopicPerm( dmaap.getDmaapName() );
	}
	public String getTopicPerm( String val ) {
		Dmaap dmaap = dmaapholder.get();
		String nsRoot = dmaap.getTopicNsRoot();
		if ( nsRoot == null ) { return null; }
		
		String t;
		// in ONAP Casablanca, we assume no distinction of environments reflected in topic namespace
		if ( nsRoot.startsWith(noEnvironmentPrefix) ) {
			t = nsRoot +  ".mr.topic";
		} else {
			t = nsRoot + "." + val + ".mr.topic";
		}
		return t;
	}
	
	public String getBridgeAdminFqtn(){
		Dmaap dmaap = dmaapholder.get();
		String topic = dmaap.getBridgeAdminTopic();
		
		// check if this is already an fqtn (contains a dot)
		// otherwise build it
		if ( topic.indexOf('.') < 0 ) {
			topic = dmaap.getTopicNsRoot() + "." + dmaap.getDmaapName() + "." + dmaap.getBridgeAdminTopic();
		}
		return( topic );
	}

	private boolean setTopicMgtPerms( Dmaap nd, AafService aaf ){
		String[] actions = { "create", "destroy" };
		String instance = ":" + nd.getTopicNsRoot() + "." + nd.getDmaapName() + ".mr.topic:" + nd.getTopicNsRoot() + "." + nd.getDmaapName();
		
		for( String action : actions ) {

			DmaapPerm perm = new DmaapPerm( topicFactory, instance, action );
		
			int rc = aaf.addPerm( perm );
			if ( rc != 201 &&  rc != 409 ) {
				logger.error( "unable to add perm for "+ topicFactory + "|" + instance + "|" + action );
				return true;
			}

			DmaapGrant grant = new DmaapGrant( perm, topicMgrRole );
			rc = aaf.addGrant( grant );
			if ( rc != 201 && rc != 409 ) {
				logger.error( "unable to grant to " + topicMgrRole + " perm for "+ topicFactory + "|" + instance + "|" + action );
				return true;
			}
		}
		
		String t = nd.getTopicNsRoot() +"." + nd.getDmaapName() + ".mr.topic";
		String[] s = { "view", "pub", "sub" };
		actions = s;
		instance = "*";
		
		for( String action : actions ) {

			DmaapPerm perm = new DmaapPerm( t, instance, action );
		
			int rc = aaf.addPerm( perm );
			if ( rc != 201 &&  rc != 409 ) {
				errorLogger.error( DmaapbcLogMessageEnum.AAF_UNEXPECTED_RESPONSE, Integer.toString(rc), "add perm", t + "|" + instance + "|" + action );
				return true;
			}

			DmaapGrant grant = new DmaapGrant( perm, topicMgrRole );
			rc = aaf.addGrant( grant );
			if ( rc != 201 && rc != 409 ) {
				errorLogger.error( DmaapbcLogMessageEnum.AAF_UNEXPECTED_RESPONSE, Integer.toString(rc), "grant to " + topicMgrRole + " perm ", topicFactory + "|" + instance + "|" + action );
				return true;
			}
				
		}
		return false;
	}

	public boolean testCreateMmaTopic() {

		DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
		String unit_test = p.getProperty( "UnitTest", "No" );
		if ( unit_test.equals( "Yes" ) ) {
			return createMmaTopic();
		}
		return false;
	}
	
	// create the special topic for MMA provisioning.
	// return true indicating a problem in topic creation, 
	// else false means it was ok  (created or previously existed)
	private boolean createMmaTopic() {
		boolean rc = true;
		DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
		Dmaap dmaap = dmaapholder.get();
		
		ArrayList<MR_Client> clients = new ArrayList<MR_Client>();
		String[] actions = { "pub", "sub", "view" };
		String centralMR = new DcaeLocationService().getCentralLocation();
		if ( centralMR == null ) {
			return rc;
		}
		logger.info( "Location for " + dmaap.getBridgeAdminTopic() + " is " + centralMR );
	
		// first client is the Role used by Bus Controller to send messages to MMA
		String provRole = p.getProperty("MM.ProvRole");
		MR_Client nClient = new MR_Client();
		nClient.setAction(actions);
		nClient.setClientRole(provRole);
		nClient.setDcaeLocationName(centralMR);
		clients.add( nClient );
	
		// second client is the Role used by MMA to listen to messages from Bus Controller
		String agentRole = p.getProperty("MM.AgentRole");
		nClient = new MR_Client();
		nClient.setAction(actions);
		nClient.setClientRole(agentRole);
		nClient.setDcaeLocationName(centralMR);
		clients.add( nClient );
	
		// initialize Topic
		Topic mmaTopic = new Topic().init();
		mmaTopic.setTopicName(dmaap.getBridgeAdminTopic());
		mmaTopic.setClients(clients);
		mmaTopic.setOwner("BusController");
		mmaTopic.setTopicDescription("topic reserved for MirrorMaker Administration");
		mmaTopic.setTnxEnabled("false");
		mmaTopic.setPartitionCount("1");  // a single partition should guarantee message order
		
		
		ApiError err = new ApiError();
		TopicService svc = new TopicService();
		try {
			@SuppressWarnings("unused")
			Topic nTopic = svc.addTopic(mmaTopic, err, true);
			if ( err.is2xx() || err.getCode() == 409 ) {
				return false;
			}
		} catch ( Exception e) {
			errorLogger.error( DmaapbcLogMessageEnum.UNEXPECTED_CONDITION, " while adding Topic: " + e.getMessage());
		}
		errorLogger.error( DmaapbcLogMessageEnum.TOPIC_CREATE_ERROR,  dmaap.getBridgeAdminTopic(), err.getFields(), err.getFields(), err.getMessage());
		
		return rc;
		
	}
}
