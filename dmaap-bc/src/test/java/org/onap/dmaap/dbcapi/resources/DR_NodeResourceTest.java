/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DR_Node;
import org.onap.dmaap.dbcapi.testframework.DmaapObjectFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class DR_NodeResourceTest {

    private static final DmaapObjectFactory DMAAP_OBJECT_FACTORY = new DmaapObjectFactory();
    private static FastJerseyTestContainer testContainer;

    @BeforeClass
    public static void setUpClass() throws Exception {
        DatabaseClass.getDmaap().init(DMAAP_OBJECT_FACTORY.genDmaap());

        testContainer = new FastJerseyTestContainer(new ResourceConfig()
                .register(DR_NodeResource.class));
        testContainer.init();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        testContainer.destroy();
        /*TODO: Cannot cleanup yet until still other Resources tests depends on the static DB content

        DatabaseClass.clearDatabase();
        DatabaseClass.getDmaap().remove();*/
    }

    @Test
    public void getDr_Nodes_test() {
        Response response = testContainer.target("dr_nodes").request().get(Response.class);
        System.out.println("GET dr_subs response=" + response.getStatus());

        assertEquals(200, response.getStatus());
        assertTrue(response.hasEntity());
    }

    @Test
    public void addDr_Node_shouldReturnError_whenNoLocationAndFqdnProvided() {
        DR_Node node = new DR_Node(null, null, "hostName", "1.0");
        Entity<DR_Node> requestedEntity = entity(node, APPLICATION_JSON);

        Response response = testContainer.target("dr_nodes")
                .request()
                .post(requestedEntity, Response.class);

        assertEquals(400, response.getStatus());
        ApiError responseError = response.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("dcaeLocation, fqdn", responseError.getFields());
    }

    @Test
    public void addDr_Node_shouldReturnError_whenDrNodeWithGiveFqdnAlreadyExists() {
        DR_Node node = new DR_Node("fqdn", "location", "hostName", "1.0");

        addDrNode(node);
        Response response = addDrNode(node);

        assertEquals(409, response.getStatus());
        ApiError responseError = response.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("fqdn", responseError.getFields());
        assertEquals("Node fqdn already exists", responseError.getMessage());
    }

    @Test
    public void addDr_Node_shouldExecuteSuccessfully() {
        DR_Node node = new DR_Node("fqdn", "location", "hostName", "1.0");

        Response response = addDrNode(node);

        assertEquals(200, response.getStatus());
        assertTrue(response.hasEntity());
        assertDrNodeExistInDB(response.readEntity(DR_Node.class));
    }

    @Test
    public void updateDr_Node_shouldReturnError_whenNoLocationAndFqdnProvided() {
        DR_Node node = new DR_Node(null, null, "hostName", "1.0");
        Entity<DR_Node> requestedEntity = entity(node, APPLICATION_JSON);

        Response response = testContainer.target("dr_nodes")
                .path("fqdn")
                .request()
                .put(requestedEntity, Response.class);

        assertEquals(400, response.getStatus());
        ApiError responseError = response.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("dcaeLocation, fqdn", responseError.getFields());
    }

    @Test
    public void updateDr_Node_shouldReturnError_whenDrNodeForUpdateDoesNotExistInDb() {
        DR_Node node = new DR_Node("fqdn", "location", "hostName", "1.0");
        Entity<DR_Node> requestedEntity = entity(node, APPLICATION_JSON);

        Response response = testContainer.target("dr_nodes")
                .path(node.getFqdn())
                .request()
                .put(requestedEntity, Response.class);

        assertEquals(404, response.getStatus());
        ApiError responseError = response.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("fqdn", responseError.getFields());
        assertEquals("Node " + node.getFqdn() + " does not exist", responseError.getMessage());
    }

    @Test
    public void updateDr_Node_ShouldExecuteSuccessfully() {
        DR_Node toUpdate = new DR_Node("fqdn", "location", "hostName", "1.0");
        Entity<DR_Node> requestedEntity = entity(toUpdate, APPLICATION_JSON);

        addDrNode(new DR_Node("fqdn", "old_location", "old_hostName", "old_1.0"));
        Response response = testContainer.target("dr_nodes")
                .path(toUpdate.getFqdn())
                .request()
                .put(requestedEntity, Response.class);

        assertEquals(200, response.getStatus());
        assertTrue(response.hasEntity());
        assertEquals(toUpdate, response.readEntity(DR_Node.class));
    }

    @Test
    public void deleteDr_Node_shouldReturnError_whenDrNodeForDeleteDoesNotExistInDb() {
        Response response = testContainer.target("dr_nodes")
                .path("fqdn")
                .request()
                .delete();

        assertEquals(404, response.getStatus());
        ApiError responseError = response.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("fqdn", responseError.getFields());
        assertEquals("Node fqdn does not exist", responseError.getMessage());
    }

    @Test
    public void deleteDr_Node_shouldReturnError_whenNoExistingFqdnProvided() {
        Response response = testContainer.target("dr_nodes")
                .path("")
                .request()
                .delete();

        assertEquals(405, response.getStatus());
    }

    @Test
    public void deleteDr_Node_shouldExecuteSuccessfully() {
        DR_Node node = new DR_Node("fqdn", "location", "hostName", "1.0");

        addDrNode(node);
        Response response = testContainer.target("dr_nodes")
                .path("fqdn")
                .request()
                .delete();

        assertEquals(204, response.getStatus());
    }

    @Test
    public void getDr_Node_shouldReturnError_whenDrNodeForDeleteDoesNotExistInDb() {
        Response response = testContainer.target("dr_nodes")
                .path("fqdn")
                .request()
                .get();

        assertEquals(404, response.getStatus());
        ApiError responseError = response.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("fqdn", responseError.getFields());
        assertEquals("Node fqdn does not exist", responseError.getMessage());
    }

    private Response addDrNode(DR_Node node) {
        return testContainer.target("dr_nodes")
                .request()
                .post(entity(node, APPLICATION_JSON), Response.class);
    }

    private void assertDrNodeExistInDB(DR_Node created) {
        Response response = testContainer.target("dr_nodes")
                .path(created.getFqdn())
                .request()
                .get();
        assertEquals(200, response.getStatus());
        assertTrue(response.hasEntity());
        assertEquals(created, response.readEntity(DR_Node.class));
    }

    @Before
    public void cleanupDatabase() {
        DatabaseClass.clearDatabase();
    }

}

