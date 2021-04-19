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
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.onap.dmaap.dbcapi.client.DrProvConnection;
import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.logging.DmaapbcLogMessageEnum;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DR_Sub;
import org.onap.dmaap.dbcapi.util.DmaapConfig;
import org.onap.dmaap.dbcapi.util.RandomInteger;

public class DR_SubService extends BaseLoggingClass {

	private Map<String, DR_Sub> dr_subs = DatabaseClass.getDr_subs();
	private DR_NodeService nodeService = new DR_NodeService();
	private String provURL;
	private static DrProvConnection prov;
	
	private String unit_test;
	
	
	public DR_SubService(  ) {
		logger.debug( "Entry: DR_SubService (with no args)" );
		DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
		unit_test = p.getProperty( "UnitTest", "No" );
	}	
	public DR_SubService( String subURL ) {
		logger.debug( "Entry: DR_SubService " + subURL );
		provURL = subURL;
		DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
		unit_test = p.getProperty( "UnitTest", "No" );
	}
	public Map<String, DR_Sub> getDR_Subs() {
		logger.debug( "enter getDR_Subs()");
		return dr_subs;
	}
		
	public List<DR_Sub> getAllDr_Subs() {
		logger.debug( "enter getAllDR_Subs()");
		return new ArrayList<>(dr_subs.values());
	}
	
	public ArrayList<DR_Sub> getDr_SubsByFeedId( String pubId ) {
		ArrayList<DR_Sub> someSubs = new ArrayList<>();
		for( DR_Sub sub : dr_subs.values() ) {
			if ( pubId.equals(  sub.getFeedId()  )) {
				someSubs.add( sub );
			}
		}
			
		return someSubs;
	}
	public DR_Sub getDr_Sub( String key, ApiError apiError ) {	
		logger.debug( "enter getDR_Sub()");
		DR_Sub sub = dr_subs.get( key );
		if ( sub == null ) {
			apiError.setCode(Status.NOT_FOUND.getStatusCode());
			apiError.setFields( "subId");
			apiError.setMessage("subId " + key + " not found");
		} else {
			apiError.setCode(200);
		}
		return sub;
	}

	public DR_Sub addDr_Sub( DR_Sub sub, ApiError apiError ) {
		logger.debug( "enter addDR_Subs()");
		prov = new DrProvConnection();
		prov.makeSubPostConnection( provURL );
		String resp = prov.doPostDr_Sub( sub, apiError );
		if ( "Yes".equals(unit_test) ) {
			resp = simulateResp( sub, "POST" );
			apiError.setCode(201);
		}
		logger.debug( "addDr_Sub resp=" + resp );

		DR_Sub snew = null;

		if ( resp != null ) {
			snew = new DR_Sub( resp );
			snew.setDcaeLocationName(sub.getDcaeLocationName());
			snew.setLastMod();
			addEgressRoute( snew, apiError );
			dr_subs.put( snew.getSubId(), snew );	
			apiError.setCode(201);
		} else {
			apiError.setCode(400);
		}
		
		return snew;
	}

	private void addEgressRoute( DR_Sub sub, ApiError err ) {
		
		String nodePattern = nodeService.getNodePatternAtLocation( sub.getDcaeLocationName(), false );
		if ( nodePattern != null && nodePattern.length() > 0 ) {
			logger.info( "creating egress rule: sub " + sub.getSubId() + " on feed " + sub.getFeedId() + " to " + nodePattern);
			prov.makeEgressConnection( sub.getSubId(),  nodePattern);
			int rc = prov.doXgressPost(err);
			logger.info( "rc=" + rc + " error code=" + err.getCode() );
			
			if ( rc != 200 ) {
				switch( rc ) {
				case 403:
					logger.error( "Not authorized for DR egress API");
					err.setCode(500);
					err.setMessage("API deployment/configuration error - contact support");
					err.setFields( "PROV_AUTH_ADDRESSES");
					break;
				
				default: 
					logger.info( DmaapbcLogMessageEnum.EGRESS_CREATE_ERROR, Integer.toString(rc),  sub.getSubId(), sub.getFeedId(), nodePattern);
				}
			}

		}
	}
	
	public DR_Sub updateDr_Sub( DR_Sub obj, ApiError apiError ) {
		logger.debug( "enter updateDR_Subs()");

		DrProvConnection prov = new DrProvConnection();
		prov.makeSubPutConnection( obj.getSubId() );
		String resp = prov.doPutDr_Sub( obj, apiError );
		if ( unit_test.equals( "Yes" ) ) {
			resp = simulateResp( obj, "PUT" );
			apiError.setCode(200);
		}
		logger.debug( "resp=" + resp );

		DR_Sub snew = null;

		if ( resp != null ) {
			snew = new DR_Sub( resp );
			snew.setDcaeLocationName(obj.getDcaeLocationName());
			snew.setLastMod();
			dr_subs.put( snew.getSubId(), snew );	
			apiError.setCode(200);
		} else if ( apiError.is2xx()) {
			apiError.setCode(400);
			apiError.setMessage("unexpected empty response from DR Prov");
		}
		
		return snew;
	}
		
	public void removeDr_Sub( String key, ApiError apiError ) {
		removeDr_Sub( key, apiError, true );
		return;
	}
	
	public void removeDr_Sub( String key, ApiError apiError, boolean hitDR ) {
		logger.debug( "enter removeDR_Subs()");
		
		DR_Sub sub = dr_subs.get( key );
		if ( sub == null ) {
			apiError.setCode(Status.NOT_FOUND.getStatusCode());
			apiError.setFields( "subId");
			apiError.setMessage("subId " + key + " not found");
		} else {
			if ( hitDR ) {
				DrProvConnection prov = new DrProvConnection();
				prov.makeSubPutConnection( key );
				String resp = prov.doDeleteDr_Sub( sub, apiError );
				logger.debug( "resp=" + resp );
			} else {
				apiError.setCode(200);
			}
			
			if ( apiError.is2xx() || unit_test.equals( "Yes" ) ) {
				dr_subs.remove(key);
			}
		}

		return;
	}	

	private String simulateResp( DR_Sub sub, String action ){
		String server = "subscriber.onap.org";
		String subid;
		if ( action.equals( "POST" ) ) { 
			RandomInteger ran = new RandomInteger(10000);
			subid = Integer.toString( ran.next() );
		} else if ( action.equals( "PUT" ) ) {
			subid = sub.getSubId();
		} else {
			subid = "99";
		}
		String ret = String.format("{\"suspend\": false, \"delivery\": {\"url\": \"https://%s/delivery/%s\", \"user\": \"%s\", \"password\": \"%s\", \"use100\":  true}, \"metadataOnly\": false, \"groupid\": \"0\" , \"follow_redirect\": true, ", 
			server, subid, sub.getUsername(), sub.getUserpwd());
		String links = String.format( "\"links\": {\"feed\": \"https://dr-prov/feedlog/%s\", \"self\": \"https://dr-prov/sub/%s\", \"log\": \"https://dr-prov/sublog/%s\" }", 
				sub.getFeedId(),
				subid,
				subid );
		ret += links + "}";
		logger.info( "DR_SubService:simulateResp=" + ret);

		return ret;
	}
}
