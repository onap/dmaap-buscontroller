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
import jakarta.ws.rs.core.Response.Status;
import org.onap.dmaap.dbcapi.client.DrProvConnection;
import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.logging.DmaapbcLogMessageEnum;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DR_Pub;
import org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status;

public class DR_PubService  extends BaseLoggingClass{
	
	private Map<String, DR_Pub> dr_pubs = DatabaseClass.getDr_pubs();
	private DR_NodeService nodeService = new DR_NodeService();
	private static DrProvConnection prov;
	
	public DR_PubService() {
		super();
		prov = new DrProvConnection();
	}

	public Map<String, DR_Pub> getDr_Pubs() {			
		return dr_pubs;
	}
		
	public List<DR_Pub> getAllDr_Pubs() {
		return new ArrayList<DR_Pub>(dr_pubs.values());
	}
	
	public ArrayList<DR_Pub> getDr_PubsByFeedId( String feedId ) {
		ArrayList<DR_Pub> somePubs = new ArrayList<DR_Pub>();
		for( DR_Pub pub : dr_pubs.values() ) {
			if ( feedId.equals(  pub.getFeedId()  )) {
				somePubs.add( pub );
			}
		}
			
		return somePubs;
	}
		
	public DR_Pub getDr_Pub( String key, ApiError err ) {	
		DR_Pub pub = dr_pubs.get( key );
		if ( pub == null ) {
			err.setCode(Status.NOT_FOUND.getStatusCode());
			err.setFields( "pubId");
			err.setMessage("DR_Pub with pubId = " + key + " not found");
		} else {
			err.setCode(Status.OK.getStatusCode());
		}
		return pub;
	}
	
	private void addIngressRoute( DR_Pub pub, ApiError err ) {
		
		String nodePattern = nodeService.getNodePatternAtLocation( pub.getDcaeLocationName(), true );
		if ( nodePattern != null && nodePattern.length() > 0 ) {
			logger.info( "creating ingress rule: pub " + pub.getPubId() + " on feed " + pub.getFeedId() + " to " + nodePattern);
			prov.makeIngressConnection( pub.getFeedId(), pub.getUsername(), "-", nodePattern);
			int rc = prov.doXgressPost(err);
			logger.info( "rc=" + rc + " error code=" + err.getCode() );
			
			if ( rc != 200 ) {
				switch( rc ) {
				case 403:
					logger.error( "Not authorized for DR ingress API");
					err.setCode(500);
					err.setMessage("API deployment/configuration error - contact support");
					err.setFields( "PROV_AUTH_ADDRESSES");
					break;
				
				default: 
					logger.info( DmaapbcLogMessageEnum.INGRESS_CREATE_ERROR, Integer.toString(rc),  pub.getPubId(), pub.getFeedId(), nodePattern);
				}
			}

		}
	}

	public DR_Pub addDr_Pub( DR_Pub pub ) {
		ApiError err = new ApiError();
		if ( pub.getPubId() != null && ! pub.getPubId().isEmpty() ) {
			addIngressRoute( pub, err);
			if ( err.getCode() > 0 ) {
				pub.setStatus(DmaapObject_Status.INVALID);
			}
			pub.setLastMod();
			dr_pubs.put( pub.getPubId(), pub );
			return pub;
		}
		else {
			return null;
		}
	}
		
	public DR_Pub updateDr_Pub( DR_Pub pub ) {
		if ( pub.getPubId().isEmpty()) {
			return null;
		}
		pub.setLastMod();
		dr_pubs.put( pub.getPubId(), pub );
		return pub;
	}
		
	public DR_Pub removeDr_Pub( String pubId, ApiError err ) {
		return removeDr_Pub( pubId, err, true );
	}
		
	
	public DR_Pub removeDr_Pub( String pubId, ApiError err, boolean hitDR ) {
		DR_Pub pub =  dr_pubs.get( pubId );
		if ( pub == null ) {
			err.setCode(Status.NOT_FOUND.getStatusCode());
			err.setFields( "pubId");
			err.setMessage( "pubId " + pubId + " not found");
		} else {
			dr_pubs.remove(pubId);
			err.setCode(Status.OK.getStatusCode());
		}
		return pub;
				
	}	

}
