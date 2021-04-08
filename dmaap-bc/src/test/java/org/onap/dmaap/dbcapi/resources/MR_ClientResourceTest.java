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
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.MR_Client;
import org.onap.dmaap.dbcapi.model.MR_Cluster;
import org.onap.dmaap.dbcapi.model.Topic;
import org.onap.dmaap.dbcapi.testframework.DmaapObjectFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MR_ClientResourceTest {

    private static final DmaapObjectFactory DMAAP_OBJECT_FACTORY = new DmaapObjectFactory();
    private static FastJerseyTestContainer testContainer;

    @BeforeClass
    public static void setUpClass() throws Exception {
        DatabaseClass.getDmaap().init(DMAAP_OBJECT_FACTORY.genDmaap());

        testContainer = new FastJerseyTestContainer(new ResourceConfig()
                .register(MR_ClientResource.class).register(MR_ClusterResource.class).register(TopicResource.class));
        testContainer.init();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        testContainer.destroy();
        /*TODO: Cannot cleanup yet until still other Resources tests depends on the static DB content

        DatabaseClass.getDmaap().remove();
        DatabaseClass.clearDatabase();*/
    }

    @Test
    public void addMr_Client_shouldReturnErrorWhenNoFqtnProvided() {
        Entity<MR_Client> requestedEntity = entity(
                new MR_Client("dcaeLocation", null, "clientRole", new String[]{"GET"}), APPLICATION_JSON);

        Response response = testContainer.target("mr_clients")
                .request()
                .post(requestedEntity, Response.class);

        assertEquals(400, response.getStatus());
        ApiError responseError = response.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("fqtn", responseError.getFields());
    }

    @Test
    public void addMr_Client_shouldReturnErrorWhenNoLocationProvided() {
        Entity<MR_Client> requestedEntity = entity(
                new MR_Client(null, "fqtn", "clientRole", new String[]{"GET"}), APPLICATION_JSON);

        Response response = testContainer.target("mr_clients")
                .request()
                .post(requestedEntity, Response.class);

        assertEquals(400, response.getStatus());
        ApiError responseError = response.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("dcaeLocationName", responseError.getFields());
    }

    @Test
    public void addMr_Client_shouldReturnErrorWhenNoClientRoleProvided() {
        Entity<MR_Client> requestedEntity = entity(
                new MR_Client("dcaeLocation", "fqtn", null, new String[]{"GET"}), APPLICATION_JSON);

        Response response = testContainer.target("mr_clients")
                .request()
                .post(requestedEntity, Response.class);

        assertEquals(400, response.getStatus());
        ApiError responseError = response.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("clientRole or clientIdentity", responseError.getFields());
    }

    @Test
    public void addMr_Client_shouldReturnErrorWhenNoActionsProvided() {
        Entity<MR_Client> requestedEntity = entity(
                new MR_Client("dcaeLocation", "fqtn", "clientRole", null), APPLICATION_JSON);

        Response response = testContainer.target("mr_clients")
                .request()
                .post(requestedEntity, Response.class);

        assertEquals(400, response.getStatus());
        ApiError responseError = response.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("action", responseError.getFields());
    }

    @Test
    public void addMr_Client_shouldReturnErrorWhereThereIsNoMrClusterAvailable() {
        Entity<MR_Client> requestedEntity = entity(
                new MR_Client("dcaeLocationName", "fqtn", "clientRole", new String[]{"GET"}), APPLICATION_JSON);

        Response response = testContainer.target("mr_clients")
                .request()
                .post(requestedEntity, Response.class);

        assertEquals(400, response.getStatus());
        ApiError responseError = response.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("dcaeLocationName", responseError.getFields());
    }

    @Test
    public void addMr_Client_shouldReturnErrorWhereThereIsNoTopicForFqtnAvailable() {
        Entity<MR_Client> requestedEntity = entity(
                new MR_Client("dcaeLocation", "fqtn", "clientRole", new String[]{"GET"}), APPLICATION_JSON);

        createMrCluster(new MR_Cluster("dcaeLocation", "fqdn", "protocol", "port"));

        Response response = testContainer.target("mr_clients")
                .request()
                .post(requestedEntity, Response.class);

        assertEquals(404, response.getStatus());
        ApiError responseError = response.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("fqtn", responseError.getFields());
    }

    @Test
    public void addMr_Client_shouldAddMrClient() {
        Entity<MR_Client> requestedEntity = entity(
                new MR_Client("dcaeLocation2", "testTopic", "clientRole", new String[]{"GET"}), APPLICATION_JSON);

        createMrCluster(new MR_Cluster("dcaeLocation2", "fqdn", "protocol", "port"));
        createTopic("testTopic");

        Response response = testContainer.target("mr_clients")
                .request()
                .post(requestedEntity, Response.class);

        assertEquals(200, response.getStatus());
        assertTrue(response.hasEntity());
        assertMRClientExistInDB(response.readEntity(MR_Client.class));
    }

    @Test
    public void deleteMr_Client_shouldDeleteMrClient() {
        Entity<MR_Client> requestedEntity = entity(
                new MR_Client("dcaeLocation3", "testTopic", "clientRole", new String[]{"GET"}), APPLICATION_JSON);
        createMrCluster(new MR_Cluster("dcaeLocation3", "fqdn", "protocol", "port"));
        createTopic("testTopic");

        Response response = testContainer.target("mr_clients")
                .request()
                .post(requestedEntity, Response.class);
        assertEquals(200, response.getStatus());

        MR_Client mrClientEntity = response.readEntity(MR_Client.class);
        Response deleteResponse = testContainer.target("mr_clients")
                .path(mrClientEntity.getMrClientId())
                .request()
                .delete();

        assertEquals(204, deleteResponse.getStatus());
        assertMrClientNotExistInDB(mrClientEntity.getMrClientId());
    }

    @Test
    public void deleteMr_Clients_shouldReturnMethodNotAllowedCodeWhenClientIdIsMissing() {
        Response deleteResponse = testContainer.target("mr_clients")
                .request()
                .delete();

        assertEquals(405, deleteResponse.getStatus());
    }

    @Test
    public void getMr_Client_shouldReturnErrorWhenThereIsNoClient() {
        Response response = testContainer.target("mr_clients")
                .path("not_existing_id")
                .request()
                .get();

        assertEquals(404, response.getStatus());
        ApiError responseError = response.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("mrClientId", responseError.getFields());
    }

    @Test
    public void updateMr_Client_shouldReturnErrorWhenNoFqtnProvided() {
        MR_Client mrClient = new MR_Client("dcaeLocation", null, "clientRole", new String[]{"GET"});
        Entity<MR_Client> requestedEntity = entity(mrClient, APPLICATION_JSON);

        Response response = testContainer.target("mr_clients")
                .path(mrClient.getMrClientId())
                .request()
                .put(requestedEntity, Response.class);

        assertEquals(400, response.getStatus());
        ApiError responseError = response.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("fqtn", responseError.getFields());
    }

    @Test
    public void updateMr_Client_shouldUpdate() {
        Entity<MR_Client> requestedEntity = entity(
                new MR_Client("dcaeLocation4", "testTopic", "clientRole", new String[]{"GET"}), APPLICATION_JSON);

        createMrCluster(new MR_Cluster("dcaeLocation4", "fqdn", "protocol", "port"));
        createTopic("testTopic");

        Response response = testContainer.target("mr_clients")
                .request()
                .post(requestedEntity, Response.class);
        MR_Client createdMrClient = response.readEntity(MR_Client.class);
        createdMrClient.setDcaeLocationName("updatedDcaeLocation");


        Response updateResponse = testContainer.target("mr_clients")
                .path(createdMrClient.getMrClientId())
                .request()
                .put(requestedEntity, Response.class);

        assertEquals(200, updateResponse.getStatus());
        assertTrue(updateResponse.hasEntity());
        assertMRClientExistInDB(updateResponse.readEntity(MR_Client.class));

    }

    @Test
    public void getMr_Clients_test() {
        Response response = testContainer.target("mr_clients").request().get(Response.class);
        System.out.println("GET dr_subs response=" + response.getStatus());

        assertEquals(200, response.getStatus());
        assertTrue(response.hasEntity());
    }


    private void createMrCluster(MR_Cluster cluster) {
        Response response = testContainer.target("mr_clusters")
                .request()
                .post(entity(cluster, APPLICATION_JSON), Response.class);
        assertEquals(201, response.getStatus());
    }

    private void createTopic(String tname) {
        Topic topic = new Topic();
        topic.setFqtn(tname);
        topic.setFqtn(tname);
        DatabaseClass.getTopics().put(topic.getFqtn(), topic);
    }

    private void assertMRClientExistInDB(MR_Client created) {
        Response response = testContainer.target("mr_clients")
                .path(created.getMrClientId())
                .request()
                .get();
        assertEquals(200, response.getStatus());
        assertTrue(response.hasEntity());
        MR_Client receivedMrClient = response.readEntity(MR_Client.class);
        assertEquals(created.getFqtn(), receivedMrClient.getFqtn());
        assertEquals(created.getDcaeLocationName(), receivedMrClient.getDcaeLocationName());
    }

    private void assertMrClientNotExistInDB(String clientId) {
        assertEquals(404, testContainer.target("mr_clients")
                .path(clientId)
                .request()
                .get()
                .getStatus());
    }
}