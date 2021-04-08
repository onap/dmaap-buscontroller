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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response.Status;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.onap.dmaap.dbcapi.client.DrProvConnection;
import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DR_Pub;
import org.onap.dmaap.dbcapi.model.DR_Sub;
import org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status;
import org.onap.dmaap.dbcapi.model.Feed;
import org.onap.dmaap.dbcapi.util.DmaapConfig;
import org.onap.dmaap.dbcapi.util.RandomInteger;

public class FeedService  extends BaseLoggingClass {
	
	private Map<String, Feed> feeds = DatabaseClass.getFeeds();
	private Map<String, DR_Sub> dr_subs = DatabaseClass.getDr_subs();
	private DR_PubService pubService = new DR_PubService();
	private DR_SubService subService = new DR_SubService();
	private DcaeLocationService dcaeLocations = new DcaeLocationService();
	private String deleteHandling;
	private String unit_test;
	
	public FeedService() {
		logger.info( "new FeedService");
		DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
		deleteHandling = p.getProperty("Feed.deleteHandling", "DeleteOnDR");
		unit_test = p.getProperty( "UnitTest", "No" );

	}
	
	public Map<String, Feed> getFeeds() {			
		return feeds;
	}
	
	private void getSubObjects( Feed f ) {
		ArrayList<DR_Pub> pubs = pubService.getDr_PubsByFeedId( f.getFeedId() );
		f.setPubs(pubs);
		ArrayList<DR_Sub> subs = subService.getDr_SubsByFeedId( f.getFeedId() );
		f.setSubs(subs);	
	}
	
	public List<Feed> getAllFeeds(){
		return getAllFeeds(null, null, null);
	}
		
	public List<Feed> getAllFeeds( String name, String ver, String match ) {
		logger.info( "getAllFeeds: name=" + name + " ver=" + ver + " match=" + match);
		ArrayList<Feed> fatFeeds = new ArrayList<Feed>();
		for( Feed f:  feeds.values() ) {
			boolean keep = true;
			if ( name != null ) {
				if ( match != null && "startsWith".equals(match) ) {
					if ( ! f.getFeedName().startsWith( name ) ) {
						logger.info( "getAllFeeds: feedName=" + f.getFeedName() + " doesn't start with=" + name);
						keep = false;
					}
				} else if ( match != null && match.equals("contains") ) {
					if ( ! f.getFeedName().contains( name ) ) {
						logger.info( "getAllFeeds: feedName=" + f.getFeedName() + " doesn't contain=" + name);
						keep = false;
					}
				} else {
					if ( ! f.getFeedName().equals( name ) ) {
						logger.info( "getAllFeeds: feedName=" + f.getFeedName() + " doesn't equal=" + name);
						keep = false;
					}
				}

			}
			if ( keep && ver != null ) {
				if ( ! f.getFeedVersion().equals(ver)) {
					logger.info( "getAllFeeds: feedVersion=" + f.getFeedName() + " doesn't match " + ver);
					keep = false;
				} else {
					logger.info( "getAllFeeds: feedVersion=" + f.getFeedName() + " matches " + ver);
				}
			}
					
			if (keep){
				getSubObjects(f);
				fatFeeds.add(f);
			}
		}
		return fatFeeds;
	}
	
	
	private Feed _getFeed( String key, ApiError err, boolean flag ) {
		Feed f = feeds.get( key );
		if ( f != null && ( flag || f.getStatus() != DmaapObject_Status.DELETED ) ) {
			getSubObjects( f );
		} else {
			err.setCode(Status.NOT_FOUND.getStatusCode());
			err.setMessage("feed not found");
			err.setFields("feedId=" + key );
			return null;
		}
		err.setCode(200);
		return f;
	}
	public Feed getFeed( String key, ApiError err ) {
		return _getFeed( key, err, false );
	}
	public Feed getFeedPure( String key, ApiError err ) {
		return _getFeed( key, err, true );
	}
	
	public Feed getFeedByName( String name, String ver, ApiError err ) {
		for( Feed f:  feeds.values() ) {
			if ( f.getFeedName().equals( name ) && f.getFeedVersion().equals(ver) ) {
				getSubObjects(f);
				return f;
			}
	
		}
		err.setCode(Status.NOT_FOUND.getStatusCode());
		err.setMessage("feed not found");
		err.setFields("feedName=" + name + " and ver=" + ver );
		return null;
	
	}

	private boolean savePubs( Feed f ) {
		return savePubs( f, f );
	}
	// need to save the Pub objects independently and copy pubId from original request
	private boolean savePubs( Feed fnew, Feed req ) {
		// save any pubs
		DR_PubService pubSvc = new DR_PubService();
		ArrayList<DR_Pub> reqPubs = req.getPubs();
		ArrayList<DR_Pub> newPubs = fnew.getPubs();
		

		
		int nSize = newPubs.size();
		int rSize = reqPubs.size();
		logger.info( "reqPubs size=" + rSize + " newPubs size=" + nSize );
		if ( nSize != rSize ) {
			errorLogger.error( "Resulting set of publishers do not match requested set of publishers " + nSize + " vs " + rSize );
			fnew.setStatus( DmaapObject_Status.INVALID);
			return false;
		}
		// NOTE: when i > 1 newPubs are in reverse order from reqPubs
		for( int i = 0; i < reqPubs.size(); i++ ) {
			DR_Pub reqPub = reqPubs.get(i);	
			ApiError err = new ApiError();
			if ( pubSvc.getDr_Pub( reqPub.getPubId(), err ) == null ) {
				DR_Pub newPub = newPubs.get(nSize - i - 1);
				reqPub.setPubId(newPub.getPubId());
				reqPub.setFeedId(newPub.getFeedId());
				reqPub.setStatus(DmaapObject_Status.VALID);
				if ( reqPub.getDcaeLocationName() == null ) {
					reqPub.setDcaeLocationName("notSpecified");
				}
				pubSvc.addDr_Pub( reqPub );
			}
			
		}
		
		fnew.setPubs(reqPubs);
		fnew.setStatus(DmaapObject_Status.VALID);
		return true;

	}
	
	private boolean saveSubs( Feed f ) {
		return saveSubs( f, f );
	}
	// need to save the Sub objects independently
	private boolean saveSubs( Feed fnew, Feed req ) {	
		ArrayList<DR_Sub> subs = req.getSubs();
		if ( subs == null || subs.size() == 0 ) {
			logger.info( "No subs specified");
		} else {
			DR_SubService subSvc = new DR_SubService( fnew.getSubscribeURL() );
			ApiError err = new ApiError();
			for( int i = 0; i <  subs.size(); i++ ) {
				DR_Sub sub = subs.get(i);
				if ( subSvc.getDr_Sub( sub.getSubId(), err) == null ) {
					subs.set( i,  subSvc.addDr_Sub(sub, err));
					if ( ! err.is2xx())  {
						logger.error( "i=" + i + " url=" + sub.getDeliveryURL() + " err=" + err.getCode() );
						return false;
					}
				}
				
			}
			fnew.setSubs(subs);
		}


		fnew.setStatus(DmaapObject_Status.VALID);
		return true;

	}

	public  Feed addFeed( Feed req, ApiError err ) {

		// at least 1 pub is required by DR, so create a default pub if none is specified
		if ( req.getPubs().size() == 0 ) {
			logger.info( "No pubs specified - creating tmp pub");
			ArrayList<DR_Pub> pubs = new ArrayList<DR_Pub>();
			pubs.add( new DR_Pub( dcaeLocations.getCentralLocation())
								.setRandomUserName()
								.setRandomPassword());
			req.setPubs(pubs);
		} 
		

		DrProvConnection prov = new DrProvConnection();
		prov.makeFeedConnection();	
		String resp = prov.doPostFeed( req, err );
		if ( unit_test.equals( "Yes" ) ) {
			// assume resp is null, so need to simulate it
			resp = simulateResp( req, "POST" );
		}
		logger.info( "resp=" + resp );
		if ( resp == null ) {
			switch( err.getCode() ) {
			case 400: 
				err.setFields( "feedName=" + req.getFeedName() + " + feedVersion=" + req.getFeedVersion() );
				break;
			case 403:
				err.setCode(500);
				err.setMessage("API deployment/configuration error - contact support");
				err.setFields( "PROV_AUTH_ADDRESSES");
				logger.error( "Prov response: 403. " + err.getMessage() + " regarding " + err.getFields() );
				break;
			default:
				err.setCode(500);
				err.setMessage( "Unexpected response from DR backend" );
				err.setFields("response");
			}
			return null;

		}


		Feed fnew = new Feed( resp );
		logger.info( "fnew status is:" + fnew.getStatus() );
		if ( ! fnew.isStatusValid()) {		
			err.setCode(500);
			err.setMessage( "Unexpected response from DR backend" );
			err.setFields("response");		
			return null;
		}
		
		//saveChildren( fnew, req );
		if ( ! savePubs( fnew, req ) || ! saveSubs( fnew, req ) ) {
			err.setCode(Status.BAD_REQUEST.getStatusCode());
			err.setMessage("Unable to save Pub or Sub objects");
			return null;
		}
		fnew.setFormatUuid(req.getFormatUuid());
		fnew.setLastMod();
		feeds.put( fnew.getFeedId(), fnew );
		return fnew;
	}
		
	public Feed updateFeed( Feed req, ApiError err ) {
	
		// at least 1 pub is required by DR, so create a default pub if none is specified
		if ( req.getPubs().size() == 0 ) {
			logger.info( "No pubs specified - creating tmp pub");
			ArrayList<DR_Pub> pubs = new ArrayList<DR_Pub>();
			pubs.add( new DR_Pub( dcaeLocations.getCentralLocation())
								.setRandomUserName()
								.setRandomPassword());
			req.setPubs(pubs);
		} 
		
		DrProvConnection prov = new DrProvConnection();
		prov.makeFeedConnection( req.getFeedId() );
		String resp = prov.doPutFeed( req, err );
		if ( unit_test.equals( "Yes" ) ) {
			// assume resp is null, so need to simulate it
			resp = simulateResp( req, "PUT" );
			err.setCode(200);
		}
		logger.info( "resp=" + resp );
		if ( resp == null ) {
			switch( err.getCode() ) {
			case 400: 
				err.setFields( "feedName=" + req.getFeedName() + " + feedVersion=" + req.getFeedVersion() );
				break;
			case 403:
				err.setCode(500);
				err.setMessage("API deployment/configuration error - contact support");
				err.setFields( "PROV_AUTH_ADDRESSES");
				break;
			default:
				err.setCode(500);
				err.setMessage( "Unexpected response from DR backend" );
				err.setFields("response");
			}
			return null;
		}


		Feed fnew = new Feed( resp );
		logger.info( "fnew status is:" + fnew.getStatus() );
		if ( ! fnew.isStatusValid()) {		
			err.setCode(500);
			err.setMessage( "Unexpected response from DR backend" );
			err.setFields("response");		
			return null;
		}

		if ( ! savePubs( fnew, req ) || ! saveSubs( fnew, req ) ) {
			err.setCode(Status.BAD_REQUEST.getStatusCode());
			err.setMessage("Unable to save Pub or Sub objects");
			return null;
		}
		fnew.setFormatUuid(req.getFormatUuid());
		fnew.setLastMod();
		feeds.put( fnew.getFeedId(), fnew );
		return fnew;
	}
	
	
	//
	// DR does not actually delete a feed, so we provide two behaviors:
	// 1) clean up the feed by removing all subs and pubs, mark it here as DELETED.
	//    then client can add it back if desired.
	// 2) Call the DR Delete function.  Feed with the same name and version can never be added again
	//
	public Feed removeFeed( Feed req, ApiError err ) {
		return removeFeed( req, err, true );
	}
	
	public Feed removeFeed( Feed req, ApiError err, boolean hitDR ) {
		
		// strip pubs and subs from feed first no matter what
		ArrayList<DR_Pub> pubs = pubService.getDr_PubsByFeedId( req.getFeedId() );
		for( DR_Pub pub: pubs ) {
			pubService.removeDr_Pub(pub.getPubId(), err, hitDR);
			if ( ! err.is2xx()) {
				return req;
			}
		}
		ArrayList<DR_Sub> subs = subService.getDr_SubsByFeedId( req.getFeedId() );
		for ( DR_Sub sub: subs ) {
			subService.removeDr_Sub(sub.getSubId(), err, hitDR);
			if ( ! err.is2xx()) {
				return req;
			}
		}
		
		if ( ! hitDR ) {
			return feeds.remove(req.getFeedId());	
		}
	
		if ( deleteHandling.equalsIgnoreCase("DeleteOnDR")) {
			DrProvConnection prov = new DrProvConnection();
			prov.makeFeedConnection( req.getFeedId() );
			String resp = prov.doDeleteFeed( req, err );
			if ( unit_test.equals( "Yes" ) ) {
				// assume resp is null, so need to simulate it
				resp = simulateDelResp( req );
			}
			logger.info( "resp=" + resp );
			if ( resp == null ) {
				switch( err.getCode() ) {
				case 400: 
					err.setFields( "feedName=" + req.getFeedName() + " + feedVersion=" + req.getFeedVersion() );
					break;
				case 403:
					err.setCode(500);
					err.setMessage("API deployment/configuration error - contact support");
					err.setFields( "PROV_AUTH_ADDRESSES");
					break;
				default:
					err.setCode(500);
					err.setMessage( "Unexpected response from DR backend" );
					err.setFields("response");
				}
				return req;  // return back the requested feed - implies it wasn't removed
			}
			return feeds.remove(req.getFeedId());
		} else {
		
			logger.info( "Disable pubs for deleted feed - creating tmp pub");
			ArrayList<DR_Pub> tmppub = new ArrayList<DR_Pub>();
			tmppub.add( new DR_Pub( dcaeLocations.getCentralLocation())
								.setRandomUserName()
								.setRandomPassword());
			req.setPubs(tmppub);
			req.setSubs(null);
			Feed fnew = updateFeed( req, err );
			if ( ! err.is2xx()) {
				return req;
			}
			fnew.setStatus(DmaapObject_Status.DELETED);
			feeds.put( fnew.getFeedId(), fnew );
			return null;	
		}

		
	}	
	
	
	/*
	 * sync will retrieve current config from DR and add it to the DB
	 * when hard = true, then first git rid of current DR provisioning data (from the DB)
	 */
	public void sync( boolean hard, ApiError err ) {
	
		if ( hard ) {
			
			ArrayList<Feed> flist = new ArrayList<Feed>(this.getAllFeeds());
			for ( Iterator<Feed> it = flist.iterator(); it.hasNext(); ) {
				Feed f = it.next();
	
				@SuppressWarnings("unused")
				Feed old = removeFeed( f, err, false );
				if (! err.is2xx()) {
					return;
				}
			}
		}
		
		DrProvConnection prov = new DrProvConnection();
		prov.makeDumpConnection();
		String resp = prov.doGetDump( err );
		if (! err.is2xx()) {
			return;
		}
		logger.debug("sync: resp from DR is: " + resp);
		
		JSONParser parser = new JSONParser();
		JSONObject jsonObj;
		try {
			jsonObj = (JSONObject) parser.parse( resp );
		} catch ( ParseException pe ) {
			logger.error( "Error parsing provisioning data: " + resp );
			err.setCode(500);
			return;
		}
		
		int i;

		JSONArray feedsArray = (JSONArray) jsonObj.get( "feeds");
		for( i = 0; i < feedsArray.size(); i++ ) {
			JSONObject entry = (JSONObject) feedsArray.get(i);
			Feed fnew = new Feed( entry.toJSONString() );
			
			logger.info( "fnew status is:" + fnew.getStatus() );
			if ( ! fnew.isStatusValid()) {		
				err.setCode(500);
				err.setMessage( "Unexpected response from DR backend" );
				err.setFields("response");		
				return;
			}
			
				if ( ! savePubs( fnew )  ) {
				err.setCode(Status.BAD_REQUEST.getStatusCode());
				err.setMessage("Unable to save Pub or Sub objects");
				return; 
			}
			fnew.setFormatUuid(fnew.getFormatUuid());
			fnew.setLastMod();
			feeds.put( fnew.getFeedId(), fnew );

		}
		
		JSONArray subArray = (JSONArray) jsonObj.get( "subscriptions");
		for( i = 0; i < subArray.size(); i++ ) {
			JSONObject entry = (JSONObject) subArray.get(i);
			DR_Sub snew = new DR_Sub( entry.toJSONString() );
			
			logger.info( "snew status is:" + snew.getStatus() );
			if ( ! snew.isStatusValid()) {		
				err.setCode(500);
				err.setMessage( "Unexpected response from DR backend" );
				err.setFields("response");		
				return;
			}
			
			dr_subs.put( snew.getSubId(), snew );

		}
		err.setCode(200);
		return;
		
	}

	private String simulateResp( Feed f, String action ){
		String server = "localhost";
		String feedid;
		if ( action.equals( "POST" ) ) { 
			RandomInteger ran = new RandomInteger(10000);
			feedid = Integer.toString( ran.next() );
		} else if ( action.equals( "PUT" ) ) {
			feedid = f.getFeedId();
		} else {
			feedid = "99";
		}
		String ret = String.format( 
"{\"suspend\":false,\"groupid\":0,\"description\":\"%s\",\"version\":\"1.0\",\"authorization\":",
			f.getFeedDescription() );

		String endpoints = "{\"endpoint_addrs\":[],\"classification\":\"unclassified\",\"endpoint_ids\":[";
		String sep = "";
		for( DR_Pub pub: f.getPubs()) {
			endpoints +=  String.format( "%s{\"password\":\"%s\",\"id\":\"%s\"}", 
					sep, pub.getUserpwd(), pub.getUsername() );
			sep = ",";
			
		}
		endpoints += "]},";
		ret += endpoints;
		
		ret += String.format(
		"\"name\":\"%s\",\"business_description\":\"\",\"publisher\":\"sim\",\"links\":{\"subscribe\":\"https://%s/subscribe/%s\",\"log\":\"https://%s/feedlog/%s\",\"publish\":\"https://%s/publish/%s\",\"self\":\"https://%s/feed/%s\"}}",

			f.getFeedName(),
			server, feedid,
			server, feedid,
			server, feedid,
			server, feedid
				);
		logger.info( "simulateResp ret=" + ret );
		return ret;
	}
	private String simulateDelResp( Feed f ){
		String server = "localhost";
		String feedid = f.getFeedId();
		String ret = String.format( 
"{\"suspend\":true,\"groupid\":0,\"description\":\"%s\",\"version\":\"1.0\",\"authorization\":{\"endpoint_addrs\":[],\"classification\":\"unclassified\",\"endpoint_ids\":[{\"password\":\"topSecret123\",\"id\":\"sim\"}]},\"name\":\"%s\",\"business_description\":\"\",\"publisher\":\"sim\",\"links\":{\"subscribe\":\"https://%s/subscribe/%s\",\"log\":\"https://%s/feedlog/%s\",\"publish\":\"https://%s/publish/%s\",\"self\":\"https://%s/feed/%s\"}}",
		f.getFeedDescription(),
		f.getFeedName(),
		server, feedid,
		server, feedid,
		server, feedid,
		server, feedid

		);
		return ret;
	}
}
