/*-
 * ============LICENSE_START=======================================================
 * org.onap.dcae
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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

import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DR_Node;
import org.onap.dmaap.dbcapi.service.DR_NodeService;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

@Path("/dr_nodes")
@Api( value= "dr_nodes", description = "Endpoint for a Data Router Node server" )
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authorization
public class DR_NodeResource extends BaseLoggingClass {

	private DR_NodeService dr_nodeService = new DR_NodeService();
	private ResponseBuilder responseBuilder = new ResponseBuilder();
	private RequiredChecker checker = new RequiredChecker();
	
	@GET
	@ApiOperation( value = "return DR_Node details", 
	notes = "Returns array of `DR_Node` object array.  Need to add filter by dcaeLocation.", 
	response = DR_Node.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = DR_Node.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	public Response getDr_Nodes() {
		List<DR_Node> nodes = dr_nodeService.getAllDr_Nodes();

		GenericEntity<List<DR_Node>> list = new GenericEntity<List<DR_Node>>(nodes) {
        };
        return responseBuilder.success(list);
	}
	
	@POST
	@ApiOperation( value = "return DR_Node details", 
	notes = "create a `DR_Node` in a *dcaeLocation*.  Note that multiple `DR_Node`s may exist in the same `dcaeLocation`.", 
	response = DR_Node.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = DR_Node.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	public Response addDr_Node(DR_Node node) {

		ApiError apiError = new ApiError();

		try {
			checker.required( "dcaeLocation", node.getDcaeLocationName());
			checker.required( "fqdn", node.getFqdn());
		} catch ( RequiredFieldException rfe ) {
			return responseBuilder.error(new ApiError(BAD_REQUEST.getStatusCode(),
					"missing required field", "dcaeLocation, fqdn"));
		}
		DR_Node nNode = dr_nodeService.addDr_Node(node, apiError);
		if (apiError.is2xx()) {
			return responseBuilder.success(nNode);
		}
		return responseBuilder.error(apiError);
	}
	
	@PUT
	@ApiOperation( value = "return DR_Node details", 
	notes = "Update a single `DR_Node` object.", 
	response = DR_Node.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = DR_Node.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	@Path("/{fqdn}")
	public Response updateDr_Node(@PathParam("fqdn") String name, DR_Node node) {

		ApiError apiError = new ApiError();

		try {
			checker.required( "dcaeLocation", node.getDcaeLocationName());
			checker.required( "fqdn", node.getFqdn());
		} catch ( RequiredFieldException rfe ) {
			return responseBuilder.error(new ApiError(BAD_REQUEST.getStatusCode(),
					"missing required field", "dcaeLocation, fqdn"));
		}
		node.setFqdn(name);
		DR_Node nNode = dr_nodeService.updateDr_Node(node, apiError);
		if (apiError.is2xx()) {
			return responseBuilder.success(nNode);
		}
		return responseBuilder.error(apiError);
	}
	
	@DELETE
	@ApiOperation( value = "No Content", 
	notes = "Delete a single `DR_Node` object.", 
	response = DR_Node.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 204, message = "Success", response = DR_Node.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	@Path("/{fqdn}")
	public Response deleteDr_Node( 
			@PathParam("fqdn") String name){


		ApiError apiError = new ApiError();

		dr_nodeService.removeDr_Node(name, apiError);
		if (apiError.is2xx()) {
			return responseBuilder.success(NO_CONTENT.getStatusCode(), null);
		}
		return responseBuilder.error(apiError);
	}

	@GET
	@ApiOperation( value = "return DR_Node details", 
	notes = "Retrieve a single `DR_Node` object.", 
	response = DR_Node.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = DR_Node.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	@Path("/{fqdn}")
	public Response get(@PathParam("fqdn") String name) {

		ApiError apiError = new ApiError();

		DR_Node nNode = dr_nodeService.getDr_Node( name, apiError );
		if (apiError.is2xx()) {
			return responseBuilder.success(nNode);
		}
		return responseBuilder.error(apiError);
	}
}
