/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2018 IBM.
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

package org.onap.dmaap.dbcapi.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.BrTopic;
import org.onap.dmaap.dbcapi.model.MirrorMaker;
import org.onap.dmaap.dbcapi.service.MirrorMakerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Path("/bridge")
@Api( value= "bridge", description = "Endpoint for retreiving MR Bridge metrics" )
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authorization
public class BridgeResource extends BaseLoggingClass {
	
	private MirrorMakerService mmService = new MirrorMakerService();
	private ResponseBuilder responseBuilder = new ResponseBuilder();

	@GET
	@ApiOperation( value = "return BrTopic details", 
	notes = "Returns array of  `BrTopic` objects. If source and target query params are specified, only report on that bridge.  "
			+ "If detail param is true, list topics names, else just a count is returned.", 
	response = BrTopic.class)
@ApiResponses( value = {
    @ApiResponse( code = 200, message = "Success", response = BrTopic.class),
    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
})
	public Response	getBridgedTopics(@QueryParam("mmagent") String mmagent,
						   			@QueryParam("detail") Boolean detailFlag ){

		if ( mmagent == null ) {
			return responseBuilder.success(getMMcounts(Boolean.TRUE.equals(detailFlag)));

		}
		logger.info( "getBridgeTopics():" + " mmagent=" + mmagent);

		if ( ! Boolean.TRUE.equals(detailFlag)) {
			BrTopic brTopic = new BrTopic();
			
			// get topics between 2 bridged locations

			MirrorMaker mm = mmService.getMirrorMaker(mmagent);
			if ( mm == null ) {		
				return responseBuilder.notFound();
			} 
					
			brTopic.setTopicCount( mm.getTopicCount() );
			brTopic.setBrSource( mm.getSourceCluster());
			brTopic.setBrTarget( mm.getTargetCluster());
			brTopic.setMmAgentName(mm.getMmName());
			
			logger.info( "topicCount [2 locations]: " + brTopic.getTopicCount() );
		
			return responseBuilder.success(brTopic);
		} else {	
			logger.info( "getBridgeTopics() detail:" + " mmagent=" + mmagent);
			// get topics between 2 bridged locations	
			MirrorMaker mm = mmService.getMirrorMaker(mmagent);
			if ( mm == null ) {		
				return responseBuilder.notFound();
			} 

			return responseBuilder.success(mm);
		}
	}
	
	private BrTopic[] getMMcounts( Boolean showDetail ) {
		
		List<String> mmList = mmService.getAllMirrorMakers();
		int s = 1;
		if ( showDetail ) {
			s = mmList.size() + 1;
		}
		BrTopic[] brTopic = new BrTopic[s];
		
		int totCnt = 0;
		s = 0;
		for( String key: mmList ) {
			int mCnt = 0;
			MirrorMaker mm = mmService.getMirrorMaker(key);
			if ( mm != null ) {
				mCnt = mm.getTopicCount();
			}
			logger.info( "Count for "+ key + ": " + mCnt);
			totCnt += mCnt;
			if (showDetail && mm!=null) {
				brTopic[s] =  new BrTopic();
				brTopic[s].setBrSource( mm.getSourceCluster());
				brTopic[s].setBrTarget(mm.getTargetCluster());
				brTopic[s].setMmAgentName(mm.getMmName());
				brTopic[s].setTopicCount(mm.getTopicCount());
				s++;
			}
		}
		
		logger.info( "topicCount [all locations]: " + totCnt );
		brTopic[s] =  new BrTopic();
		brTopic[s].setBrSource("all");
		brTopic[s].setBrTarget("all");
		brTopic[s].setMmAgentName("n/a");
		brTopic[s].setTopicCount(totCnt);
		return brTopic;
	}
	
	@PUT
	@ApiOperation( value = "update MirrorMaker details", 
		notes = "replace the topic list for a specific Bridge.  Use JSON Body for value to replace whitelist, "
				+ "but if refreshFlag param is true, simply refresh using existing whitelist."
				+ "If split param is true, spread whitelist over smaller mmagents.", 
		response = MirrorMaker.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = BrTopic.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	public Response	putBridgedTopics(@QueryParam("mmagent") String mmagent,
						   			@QueryParam("refresh") Boolean refreshFlag,
						   			@QueryParam("split") Boolean splitFlag,
						   			MirrorMaker newBridge ){
		logger.info( "putBridgeTopics() mmagent:" +  mmagent );

		if ( mmagent != null ) {		// put topics between 2 bridged locations
			
			MirrorMaker mm = mmService.getMirrorMaker(mmagent);
			if ( mm == null ) {		
				return responseBuilder.notFound();
			} 
			
			if ( splitFlag != null && splitFlag == true ) {
				mm = mmService.splitMM( mm );
			} else if ( refreshFlag == null  ||  refreshFlag == false ) {
				logger.info( "setting whitelist from message body containing mmName=" + newBridge.getMmName());
				if ( ! mmagent.equals(newBridge.getMmName()) ){
					logger.error( "mmagent query param does not match mmName in body");
					return responseBuilder.error(new ApiError(BAD_REQUEST.getStatusCode(),
							"mmagent query param does not match mmName in body"));
				}
				mm.setTopics( newBridge.getTopics() );
			} else {
				logger.info( "refreshing whitelist from memory");
			}
			mmService.updateMirrorMaker(mm);
			return responseBuilder.success(mm);
		}

		else {
			logger.error( "mmagent is required for PUT");
			return responseBuilder.error(new ApiError(BAD_REQUEST.getStatusCode(), "mmagent is required for PUT"));
		}

	}
}
