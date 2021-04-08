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

package org.onap.dmaap.dbcapi.resources;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DR_Sub;
import org.onap.dmaap.dbcapi.model.Feed;
import org.onap.dmaap.dbcapi.service.DR_SubService;
import org.onap.dmaap.dbcapi.service.FeedService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static javax.ws.rs.core.Response.Status.CREATED;


@Path("/dr_subs")
@Api( value= "dr_subs", description = "Endpoint for a Data Router client that implements a Subscriber" )
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authorization
public class DR_SubResource extends BaseLoggingClass {

	private ResponseBuilder responseBuilder = new ResponseBuilder();
	private RequiredChecker checker = new RequiredChecker();
		
	@GET
	@ApiOperation( value = "return DR_Sub details", 
	notes = "Returns array of  `DR_Sub` objects.  Add filter for feedId.", 
	response = DR_Sub.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = DR_Sub.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	public Response getDr_Subs() {
		DR_SubService dr_subService = new DR_SubService();
		List<DR_Sub> subs = dr_subService.getAllDr_Subs();

		GenericEntity<List<DR_Sub>> list = new GenericEntity<List<DR_Sub>>(subs) {
        };
        return responseBuilder.success(list);
	}
		
	@POST
	@ApiOperation( value = "return DR_Sub details", 
	notes = "Create a  `DR_Sub` object.  ", 
	response = DR_Sub.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = DR_Sub.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	public Response addDr_Sub(DR_Sub sub) {

		ApiError apiError = new ApiError();
		FeedService feeds = new FeedService();
		Feed fnew = null;
		try {
			checker.required( "feedId", sub.getFeedId());
		} catch ( RequiredFieldException rfe ) {
			try {
				checker.required( "feedName", sub.getFeedName());
			}catch ( RequiredFieldException rfe2 ) {
				logger.debug( rfe2.getApiError().toString() );
				return responseBuilder.error(rfe2.getApiError());
			}
			// if we found a FeedName instead of a FeedId then try to look it up.
			List<Feed> nfeeds =  feeds.getAllFeeds( sub.getFeedName(), sub.getFeedVersion(), "equals");
			if ( nfeeds.isEmpty() ) {
				apiError.setCode(Status.NOT_FOUND.getStatusCode());
				apiError.setFields("feedName");
				return responseBuilder.error(apiError);
			} else if (nfeeds.size() > 1) {
				logger.debug( "Attempt to match "+ sub.getFeedName() + " ver="+sub.getFeedVersion() + " matched " + nfeeds.size() );
				apiError.setCode(Status.CONFLICT.getStatusCode());
				apiError.setFields("feedName");
				return responseBuilder.error(apiError);
			}
			fnew = Iterables.getOnlyElement(nfeeds);
		}
			
		try {
			checker.required( "dcaeLocationName", sub.getDcaeLocationName());
		} catch ( RequiredFieldException rfe ) {
			logger.debug( rfe.getApiError().toString() );
			return responseBuilder.error(rfe.getApiError());
		}
		// we may have fnew already if located by FeedName
		if ( fnew == null ) {
			fnew = feeds.getFeed( sub.getFeedId(), apiError);
		}
		if ( fnew == null ) {
			logger.warn( "Specified feed " + sub.getFeedId() + " or " + sub.getFeedName() + " not known to Bus Controller");
			apiError.setCode(Status.NOT_FOUND.getStatusCode());
			return responseBuilder.error(apiError);
		}
		DR_SubService dr_subService = new DR_SubService( fnew.getSubscribeURL());
		ArrayList<DR_Sub> subs = fnew.getSubs();
		logger.info( "num existing subs before = " + subs.size() );
		DR_Sub snew = dr_subService.addDr_Sub(sub, apiError);
		if (!apiError.is2xx()) {
			return responseBuilder.error(apiError);
		}
		subs.add( snew );
		logger.info( "num existing subs after = " + subs.size() );
		
		fnew.setSubs(subs);
		logger.info( "update feed");
		return responseBuilder.success(CREATED.getStatusCode(), snew);

	}
		
	@PUT
	@ApiOperation( value = "return DR_Sub details", 
	notes = "Update a  `DR_Sub` object, selected by subId", 
	response = DR_Sub.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = DR_Sub.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	@Path("/{subId}")
	public Response updateDr_Sub(@PathParam("subId") String name, DR_Sub sub) {

		ApiError apiError = new ApiError();

		try {
			checker.required( "subId", name);
			checker.required( "feedId", sub.getFeedId());
			checker.required( "dcaeLocationName", sub.getDcaeLocationName());
	
		} catch ( RequiredFieldException rfe ) {
			logger.debug( rfe.getApiError().toString() );
			return responseBuilder.error(rfe.getApiError());
		}
		FeedService feeds = new FeedService();
		Feed fnew = feeds.getFeed(sub.getFeedId(), apiError);
		if ( fnew == null ) {
			logger.warn( "Specified feed " + sub.getFeedId() + " not known to Bus Controller");
			return responseBuilder.error(apiError);
		}

		DR_SubService dr_subService = new DR_SubService();
		sub.setSubId(name);
		DR_Sub nsub = dr_subService.updateDr_Sub(sub, apiError);
		if ( nsub != null && nsub.isStatusValid() ) {
			return responseBuilder.success(nsub);
		}
		return responseBuilder.error(apiError);
	}
		
	@DELETE
	@ApiOperation( value = "return DR_Sub details", 
	notes = "Delete a  `DR_Sub` object, selected by subId", 
	response = DR_Sub.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = DR_Sub.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	@Path("/{subId}")
	public Response deleteDr_Sub(@PathParam("subId") String id){

		ApiError apiError = new ApiError();

		try {
			checker.required( "subId", id);
		} catch ( RequiredFieldException rfe ) {
			logger.debug( rfe.getApiError().toString() );
			return responseBuilder.error(rfe.getApiError());
		}
		DR_SubService dr_subService = new DR_SubService();
		dr_subService.removeDr_Sub(id, apiError);
		if (!apiError.is2xx() ) {
			return responseBuilder.error(apiError);
		}
		return responseBuilder.success(Status.NO_CONTENT.getStatusCode(), null );
	}

	@GET
	@ApiOperation( value = "return DR_Sub details", 
	notes = "Retrieve a  `DR_Sub` object, selected by subId", 
	response = DR_Sub.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = DR_Sub.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	@Path("/{subId}")
	public Response get(@PathParam("subId") String id) {

		ApiError apiError = new ApiError();

		try {
			checker.required( "subId", id);
		} catch ( RequiredFieldException rfe ) {
			logger.debug( rfe.getApiError().toString() );
			return responseBuilder.error(rfe.getApiError());
		}
		DR_SubService dr_subService = new DR_SubService();
		DR_Sub sub =  dr_subService.getDr_Sub(id, apiError);
		if ( sub != null && sub.isStatusValid() ) {
			return responseBuilder.success(sub);
		}
		return responseBuilder.error(apiError);
	}
}
