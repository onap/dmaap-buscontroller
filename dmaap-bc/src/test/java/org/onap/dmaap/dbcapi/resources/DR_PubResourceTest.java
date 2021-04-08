
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DR_Pub;
import org.onap.dmaap.dbcapi.model.Feed;
import org.onap.dmaap.dbcapi.testframework.DmaapObjectFactory;

public class DR_PubResourceTest {

    private static final DmaapObjectFactory DMAAP_OBJECT_FACTORY = new DmaapObjectFactory();

    private static final String DCAE_LOCATION_NAME = "central-onap";
    private static final String USERNAME = "user1";
    private static final String USRPWD = "secretW0rd";
    private static final String FEED_ID = "someFakeFeedId";
    private static final String PUB_ID = "0";
    private static FastJerseyTestContainer testContainer;
    private static TestFeedCreator testFeedCreator;

    @BeforeClass
    public static void setUpClass() throws Exception {
        //TODO: init is still needed here to assure that dmaap is not null
        DatabaseClass.getDmaap().init(DMAAP_OBJECT_FACTORY.genDmaap());

        testContainer = new FastJerseyTestContainer(new ResourceConfig()
            .register(DmaapResource.class)
            .register(DR_PubResource.class)
            .register(FeedResource.class));

        testContainer.init();
        testFeedCreator = new TestFeedCreator(testContainer);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        testContainer.destroy();
        /*TODO: Cannot cleanup yet until still other Resources tests depends on the static DB content

        DatabaseClass.clearDatabase();
        DatabaseClass.getDmaap().remove();*/
    }

    @Before
    public void cleanupDatabase() {
        DatabaseClass.clearDatabase();
    }

    @Test
    public void getDr_Pub_test() {
        Response resp = testContainer.target("dr_pubs").request().get(Response.class);
        assertTrue(resp.getStatus() == 200);
        assertTrue(resp.hasEntity());
    }

    @Test
    public void addDr_Pub_shallReturnError_whenNoFeedIdAndFeedNameInPubProvided() {
        //given
        DR_Pub drPub = new DR_Pub(DCAE_LOCATION_NAME, USERNAME, USRPWD, null, PUB_ID);
        Entity<DR_Pub> requestedEntity = Entity.entity(drPub, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target("dr_pubs")
            .request()
            .post(requestedEntity, Response.class);

        //then
        assertEquals(400, resp.getStatus());
        ApiError responseError = resp.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("feedName", responseError.getFields());
    }

    @Test
    public void addDr_Pub_shallReturnError_whenFeedNameProvided_butFeedNotExist() {
        //given
        DR_Pub drPub = new DR_Pub(DCAE_LOCATION_NAME, USERNAME, USRPWD, null, PUB_ID);
        Entity<DR_Pub> requestedEntity = Entity.entity(drPub, MediaType.APPLICATION_JSON);
        drPub.setFeedName("feed_name");


        //when
        Response resp = testContainer.target("dr_pubs")
            .request()
            .post(requestedEntity, Response.class);

        //then
        assertEquals(404, resp.getStatus());
        ApiError responseError = resp.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("feedName", responseError.getFields());

    }

    @Test
    public void addDr_Pub_shallReturnError_whenFeedIdProvided_butFeedNotExist() {
        //given
        DR_Pub drPub = new DR_Pub(DCAE_LOCATION_NAME, USERNAME, USRPWD, FEED_ID, PUB_ID);
        Entity<DR_Pub> requestedEntity = Entity.entity(drPub, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target("dr_pubs")
            .request()
            .post(requestedEntity, Response.class);

        //then
        assertEquals(404, resp.getStatus());
        ApiError responseError = resp.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("feedId=" + FEED_ID, responseError.getFields());
    }

    @Test
    public void addDr_Pub_shallExecuteSuccessfully_whenValidFeedIdProvided() {
        //given
        String feedId = assureFeedIsInDB();
        DR_Pub drPub = new DR_Pub(DCAE_LOCATION_NAME, USERNAME, USRPWD, feedId);
        Entity<DR_Pub> requestedEntity = Entity.entity(drPub, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target("dr_pubs")
            .request()
            .post(requestedEntity, Response.class);

        //then
        assertEquals(201, resp.getStatus());
    }

    @Test
    public void addDr_Pub_shallExecuteSuccessfully_whenValidFeedNameProvided() {
        //given
        String feedName = "testFeed";
        testFeedCreator.addFeed(feedName, "test feed");
        DR_Pub drPub = new DR_Pub(DCAE_LOCATION_NAME, USERNAME, USRPWD, null, PUB_ID);
        drPub.setFeedName(feedName);
        Entity<DR_Pub> requestedEntity = Entity.entity(drPub, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target("dr_pubs")
            .request()
            .post(requestedEntity, Response.class);

        //then
        assertEquals(201, resp.getStatus());
    }

    @Test
    public void updateDr_Pub_shallExecuteSuccessfully_whenAddingNewPublisher() {
        //given
        String pubId = "5";
        DR_Pub drPub = new DR_Pub(DCAE_LOCATION_NAME, USERNAME, USRPWD, "feedId", PUB_ID);
        Entity<DR_Pub> reqEntity2 = Entity.entity(drPub, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target("dr_pubs")
            .path(pubId)
            .request()
            .put(reqEntity2, Response.class);

        //then
        assertEquals(200, resp.getStatus());

    }
 /*//
 //   When this test is included, the following error is generated:
 Exception in thread "HTTP-Dispatcher" java.lang.AssertionError: State is not RESPONSE (REQUEST)
    at jdk.httpserver/sun.net.httpserver.ServerImpl.responseCompleted(ServerImpl.java:814)
    at jdk.httpserver/sun.net.httpserver.ServerImpl$Dispatcher.handleEvent(ServerImpl.java:297)
    at jdk.httpserver/sun.net.httpserver.ServerImpl$Dispatcher.run(ServerImpl.java:356)
    at java.base/java.lang.Thread.run(Thread.java:830)
//  I can't figure it out, so created a Jira for now.  DMAAP-1358
    @Test
    public void updateDr_Pub_shallReturnError_whenPathIsWrong() {
        //given
        DR_Pub drPub = new DR_Pub(DCAE_LOCATION_NAME, USERNAME, USRPWD, FEED_ID, PUB_ID);
        Entity<DR_Pub> reqEntity2 = Entity.entity(drPub, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target("dr_pubs")
            .path("")
            .request()
            .put(reqEntity2, Response.class);

        //then
        assertEquals(405, resp.getStatus());
    }*/
    @Test
    public void deleteDr_Pub_shouldDeleteObjectWithSuccess() {
        //given
        String feedId = assureFeedIsInDB();
        DR_Pub dr_pub = addPub(DCAE_LOCATION_NAME, USERNAME, USRPWD, feedId);

        //when
        Response resp = testContainer.target("dr_pubs")
            .path(dr_pub.getPubId())
            .request()
            .delete();

        //then
        assertEquals("Shall delete publisher with success", 204, resp.getStatus());
        assertFalse("No entity object shall be returned", resp.hasEntity());
    }

    @Test
    public void deleteDr_Pub_shouldReturnErrorResponse_whenGivenPubIdNotFound() {
        //given
        String notExistingPubId = "6789";

        //when
        Response resp = testContainer.target("dr_pubs")
            .path(notExistingPubId)
            .request()
            .delete();

        //then
        assertEquals("Shall return error, when trying to delete not existing publisher", 404, resp.getStatus());
        ApiError responseError = resp.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("pubId", responseError.getFields());
    }

    @Test
    public void get_shallReturnExistingObject() {
        //given
        String feedId = assureFeedIsInDB();
        DR_Pub dr_Pub = addPub(DCAE_LOCATION_NAME, USERNAME, USRPWD, feedId);

        //when
        Response resp = testContainer.target("dr_pubs")
                .path(dr_Pub.getPubId())
                .request()
                .get();

        //then
        assertEquals("Publisher shall be found", 200, resp.getStatus());
        assertEquals("Retrieved object shall be equal to eh one put into DB", dr_Pub, resp.readEntity(DR_Pub.class));
    }

    private DR_Pub addPub(String d, String un, String up, String feedId) {
        DR_Pub dr_pub = new DR_Pub(d, un, up, feedId, "");
        Entity<DR_Pub> reqEntity2 = Entity.entity(dr_pub, MediaType.APPLICATION_JSON);
        Response resp = testContainer.target("dr_pubs").request().post(reqEntity2, Response.class);
        System.out.println("POST dr_pubs resp=" + resp.getStatus());
        assertTrue(resp.getStatus() == 201);
        dr_pub = resp.readEntity(DR_Pub.class);
        return dr_pub;
    }

    private String assureFeedIsInDB() {
        Feed feed = testFeedCreator.addFeed("PublisherTestFeed", "feed for DR_Pub testing");
        assertNotNull("Feed shall be added into DB properly", feed);
        return feed.getFeedId();
    }


}


