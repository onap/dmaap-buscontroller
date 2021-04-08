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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.MR_Client;
import org.onap.dmaap.dbcapi.model.MR_Cluster;
import org.onap.dmaap.dbcapi.model.Topic;
import org.onap.dmaap.dbcapi.service.MR_ClientService;
import org.onap.dmaap.dbcapi.service.MR_ClusterService;
import org.onap.dmaap.dbcapi.service.TopicService;

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
import java.util.List;

import static javax.ws.rs.core.Response.Status.NO_CONTENT;


@Path("/mr_clients")
@Api( value= "MR_Clients", description = "Endpoint for a Message Router Client that implements a Publisher or a Subscriber" )
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authorization
public class MR_ClientResource extends BaseLoggingClass {

	private MR_ClientService mr_clientService = new MR_ClientService();
	private ResponseBuilder responseBuilder = new ResponseBuilder();
	private RequiredChecker checker = new RequiredChecker();
		
	@GET
	@ApiOperation( value = "return MR_Client details", 
	notes = "Returns array of  `MR_Client` objects.", 
	response = MR_Client.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = MR_Client.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	public Response getMr_Clients() {
		List<MR_Client> clients = mr_clientService.getAllMr_Clients();

		GenericEntity<List<MR_Client>> list = new GenericEntity<List<MR_Client>>(clients) {
        };
        return responseBuilder.success(list);
	}
		
	@POST
	@ApiOperation( value = "Associate an MR_Client object to a Topic", 
	notes = "Create a  `MR_Client` object."
			+ "The `dcaeLocation` attribute is used to match an `MR_Cluster` object with the same value, with the intent of localizing message traffic."
			+ "  In legacy implementation, the `clientRole` is granted appropriate permission in AAF."
			+ "  Newer implementions may instead specify an AAF Identity, which will be added to the appropriate `Topic` role.", 
	response = MR_Client.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = MR_Client.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	public Response addMr_Client(MR_Client client) {
		ApiError apiError = new ApiError();

		try {
			checker.required( "fqtn", client.getFqtn());
			checker.required( "dcaeLocationName", client.getDcaeLocationName());
			String s = client.getClientRole();
			if ( s == null ) {
				s = client.getClientIdentity();
			}
			checker.required( "clientRole or clientIdentity", s);
			checker.required( "action", client.getAction());

		} catch ( RequiredFieldException rfe ) {
			logger.debug( rfe.getApiError().toString() );
			return responseBuilder.error(rfe.getApiError());
		}
		MR_ClusterService clusters = new MR_ClusterService();

		MR_Cluster cluster = clusters.getMr_Cluster(client.getDcaeLocationName(), apiError);
		if ( cluster == null ) {

			apiError.setCode(Status.BAD_REQUEST.getStatusCode());
			apiError.setMessage( "MR_Cluster alias not found for dcaeLocation: " + client.getDcaeLocationName());
			apiError.setFields("dcaeLocationName");
			logger.warn(apiError.toString());
			return responseBuilder.error(apiError);
		}

		TopicService topics = new TopicService();

		Topic t = topics.getTopic(client.getFqtn(), apiError);
		if ( t == null ) {
			return responseBuilder.error(apiError);
		}
		MR_Client nClient =  mr_clientService.addMr_Client(client, t, apiError);
		if (apiError.is2xx()) {
			t = topics.getTopic(client.getFqtn(), apiError);
			topics.checkForBridge(t, apiError);
			return responseBuilder.success(nClient);
		}
		else {
			return responseBuilder.error(apiError);
		}
	}
		
	@PUT
	@ApiOperation( value = "Update an MR_Client object", 
	notes = "Update a  `MR_Client` object, specified by clientId", 
	response = MR_Client.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = MR_Client.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	@Path("/{clientId}")
	public Response updateMr_Client(@PathParam("clientId") String clientId, MR_Client client) {
		ApiError apiError = new ApiError();

		try {
			checker.required( "fqtn", client.getFqtn());
			checker.required( "dcaeLocationName", client.getDcaeLocationName());
			checker.required( "clientRole", client.getClientRole());
			checker.required( "action", client.getAction());

		} catch ( RequiredFieldException rfe ) {
			logger.debug( rfe.getApiError().toString() );
			return responseBuilder.error(rfe.getApiError());
		}
		client.setMrClientId(clientId);
		MR_Client nClient = mr_clientService.updateMr_Client(client, apiError);
		if (apiError.is2xx()) {
			return Response.ok(nClient)
				.build();
		}
		return Response.status(apiError.getCode())
				.entity(apiError)
				.build();
	}
		
	@DELETE
	@ApiOperation( value = "Delete an MR_Client object", 
	notes = "Delete a  `MR_Client` object, specified by clientId", 
	response = MR_Client.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 204, message = "Success", response = MR_Client.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	@Path("/{subId}")
	public Response deleteMr_Client(@PathParam("subId") String id){
		ApiError apiError = new ApiError();

		mr_clientService.removeMr_Client(id, true, apiError);
		if (apiError.is2xx()) {
			return responseBuilder.success(NO_CONTENT.getStatusCode(), null);
		}
		
		return responseBuilder.error(apiError);
	}

	@GET
	@ApiOperation( value = "return MR_Client details", 
	notes = "Retrieve a  `MR_Client` object, specified by clientId", 
	response = MR_Client.class)
	@ApiResponses( value = {
	    @ApiResponse( code = 200, message = "Success", response = MR_Client.class),
	    @ApiResponse( code = 400, message = "Error", response = ApiError.class )
	})
	@Path("/{subId}")
	public Response getMr_Client(@PathParam("subId") String id) {
		ApiError apiError = new ApiError();

		MR_Client nClient =  mr_clientService.getMr_Client(id, apiError);
		if (apiError.is2xx()) {
			return responseBuilder.success(nClient);
		}
		return responseBuilder.error(apiError);
	}
}
