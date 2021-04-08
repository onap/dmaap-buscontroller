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

//
// $Id$

package org.onap.dmaap.dbcapi.resources;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.Dmaap;
import org.onap.dmaap.dbcapi.service.DmaapService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


@Path("/dmaap")
@Api( value= "dmaap", description = "Endpoint for this instance of DMaaP object containing values for this OpenDCAE deployment" )
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authorization
public class DmaapResource extends BaseLoggingClass {


	private DmaapService dmaapService = new DmaapService();
	private ResponseBuilder responseBuilder = new ResponseBuilder();
	private RequiredChecker checker = new RequiredChecker();

	@GET
	@ApiOperation( value = "return dmaap details", notes = "returns the `dmaap` object, which contains system wide configuration settings", response = Dmaap.class)
    @ApiResponses( value = {
        @ApiResponse( code = 200, message = "Success", response = Dmaap.class),
        @ApiResponse( code = 400, message = "Error", response = ApiError.class )
    })

	public Response getDmaap(@Context UriInfo uriInfo)  {
		Dmaap d =  dmaapService.getDmaap();
		return responseBuilder.success(d);
	}

	@POST
	@ApiOperation( value = "return dmaap details", notes = "Create a new DMaaP set system wide configuration settings for the *dcaeEnvironment*.  Deprecated with introduction of persistence in 1610.", response = Dmaap.class)
    @ApiResponses( value = {
        @ApiResponse( code = 200, message = "Success", response = Dmaap.class),
        @ApiResponse( code = 400, message = "Error", response = ApiError.class )
    })
	public Response addDmaap( Dmaap obj ) {

		try {
			validateRequiredFields(obj);
		} catch( RequiredFieldException rfe ) {
			return responseBuilder.error(rfe.getApiError());
		}

		Dmaap d =  dmaapService.addDmaap(obj);
		if ( d == null ) {
			return responseBuilder.notFound();

		}

		return responseBuilder.success(d);
	}

	@PUT
	@ApiOperation( value = "return dmaap details", notes = "Update system settings for *dcaeEnvironment*.", response = Dmaap.class)
    @ApiResponses( value = {
        @ApiResponse( code = 200, message = "Success", response = Dmaap.class),
        @ApiResponse( code = 400, message = "Error", response = ApiError.class )
    })
	public Response updateDmaap(  Dmaap obj ) {

		try {
			validateRequiredFields(obj);
		} catch( RequiredFieldException rfe ) {
			return responseBuilder.error(rfe.getApiError());
		}

		Dmaap d =  dmaapService.updateDmaap(obj);
		if ( d != null ) {
			return responseBuilder.success(d);
		} else {
			return responseBuilder.notFound();
		}
	}

	private void validateRequiredFields(Dmaap obj) throws RequiredFieldException {
		checker.required( "dmaapName", obj.getDmaapName(), "^\\S+$" );  //no white space allowed in dmaapName
		checker.required( "dmaapProvUrl", obj.getDrProvUrl());
		checker.required( "topicNsRoot", obj.getTopicNsRoot());
		checker.required( "bridgeAdminTopic", obj.getBridgeAdminTopic());
	}
}
