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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.Dmaap;



@Path("/info")
@Api( value= "info", description = "Endpoint for this instance of DBCL.  Returns health info." )
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authorization
public class InfoResource extends BaseLoggingClass {


	private ResponseBuilder responseBuilder = new ResponseBuilder();
	
	@GET
	@ApiOperation( value = "return info details", notes = "returns the `info` object", response = Dmaap.class)
    @ApiResponses( value = {
        @ApiResponse( code = 200, message = "Success", response = Dmaap.class),
        @ApiResponse( code = 400, message = "Error", response = ApiError.class )
    })

	public Response getInfo(@Context UriInfo uriInfo)  {
		return responseBuilder.success(204, null);
	}
	

	
	
}
