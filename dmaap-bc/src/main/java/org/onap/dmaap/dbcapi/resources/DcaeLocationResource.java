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

package org.onap.dmaap.dbcapi.resources;


import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.service.DcaeLocationService;


@Path("/dcaeLocations")
@Api( value= "dcaeLocations", description = "an OpenStack tenant purposed for OpenDCAE (i.e. where OpenDCAE components might be deployed)" )
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authorization
public class DcaeLocationResource extends BaseLoggingClass {
	private DcaeLocationService locationService = new DcaeLocationService();
	private ResponseBuilder responseBuilder = new ResponseBuilder();
	
	@GET
	@ApiOperation( value = "return dcaeLocation details", 
		notes = "Returns array of  `dcaeLocation` objects.  All objects managed by DMaaP are deployed in some `dcaeLocation` which is a unique identifier for an *OpenStack* tenant purposed for a *dcaeLayer*  (ecomp or edge).", 
		response = DcaeLocation.class)
    @ApiResponses( value = {
        @ApiResponse( code = 200, message = "Success", response = DcaeLocation.class),
        @ApiResponse( code = 400, message = "Error", response = ApiError.class )
    })
	public Response getDcaeLocations() {
		List<DcaeLocation> locs = locationService.getAllDcaeLocations();

		GenericEntity<List<DcaeLocation>> list = new GenericEntity<List<DcaeLocation>>(locs) {};
        return responseBuilder.success(list);
	}
	
	@POST
	@ApiOperation( value = "return dcaeLocation details", 
		notes = "Create some `dcaeLocation` which is a unique identifier for an *OpenStack* tenant purposed for a *dcaeLayer*  (ecomp or edge).", 
		response = DcaeLocation.class)
    @ApiResponses( value = {
        @ApiResponse( code = 200, message = "Success", response = DcaeLocation.class),
        @ApiResponse( code = 400, message = "Error", response = ApiError.class )
    })
	public Response addDcaeLocation(DcaeLocation location) {

		if ( locationService.getDcaeLocation(location.getDcaeLocationName()) != null ) {
			return responseBuilder.error(new ApiError(Status.CONFLICT.getStatusCode(),
					"dcaeLocation already exists", "dcaeLocation"));
		}
		DcaeLocation loc = locationService.addDcaeLocation(location);
		return responseBuilder.success(Status.CREATED.getStatusCode(), loc);
	}
	
	@PUT
	@ApiOperation( value = "return dcaeLocation details", 
		notes = "update the openStackAvailabilityZone of a dcaeLocation", 
		response = DcaeLocation.class)
    @ApiResponses( value = {
        @ApiResponse( code = 200, message = "Success", response = DcaeLocation.class),
        @ApiResponse( code = 400, message = "Error", response = ApiError.class )
    })
	@Path("/{locationName}")
	public Response updateDcaeLocation( 
			@PathParam("locationName") String name, DcaeLocation location) {

		location.setDcaeLocationName(name);
		if ( locationService.getDcaeLocation(location.getDcaeLocationName()) == null ) {
			return responseBuilder.notFound();

		}
		DcaeLocation loc = locationService.updateDcaeLocation(location);
		return responseBuilder.success(Status.CREATED.getStatusCode(), loc );
	}
	
	@DELETE
	@ApiOperation( value = "return dcaeLocation details", notes = "delete a dcaeLocation", response = DcaeLocation.class)
    @ApiResponses( value = {
        @ApiResponse( code = 204, message = "Success", response = DcaeLocation.class),
        @ApiResponse( code = 400, message = "Error", response = ApiError.class )
    })
	@Path("/{locationName}")
	public Response deleteDcaeLocation( 
			@PathParam("locationName") String name
			 ){
		locationService.removeDcaeLocation(name);
		return responseBuilder.success(NO_CONTENT.getStatusCode(), null);
	}

	@GET
	@ApiOperation( value = "return dcaeLocation details", notes = "Returns a specific `dcaeLocation` object with specified tag", response = DcaeLocation.class)
    @ApiResponses( value = {
        @ApiResponse( code = 200, message = "Success", response = DcaeLocation.class),
        @ApiResponse( code = 400, message = "Error", response = ApiError.class )
    })
	@Path("/{locationName}")
	public Response getDcaeLocation( 
			@PathParam("locationName") String name) {

		DcaeLocation loc =  locationService.getDcaeLocation( name );
		if ( loc == null ) {
			ApiError err = new ApiError();
				
			err.setCode(NOT_FOUND.getStatusCode());
			err.setMessage("dcaeLocation does not exist");
			err.setFields("dcaeLocation");
			
			return responseBuilder.error(err);
		}

		return responseBuilder.success(loc);
	}
}
