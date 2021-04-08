/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2019 Nokia Intellectual Property. All rights reserved.
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
import org.onap.dmaap.dbcapi.model.DR_Sub;
import org.onap.dmaap.dbcapi.model.Feed;
import org.onap.dmaap.dbcapi.testframework.DmaapObjectFactory;

public class DR_SubResourceTest {

    private static final DmaapObjectFactory DMAAP_OBJECT_FACTORY = new DmaapObjectFactory();

    private static final String DCAE_LOCATION_NAME = "central-onap";
    private static final String USERNAME = "user1";
    private static final String USRPWD = "secretW0rd";
    private static final String DELIVERY_URL = "https://subscriber.onap.org/delivery/id";
    private static final String LOG_URL = "https://dr-prov/sublog/id";
    private static final String DELIVERY_URL_TEMPLATE = "https://subscriber.onap.org/delivery/";
    private static final String LOG_URL_TEMPLATE = "https://dr-prov/sublog/";
    private static FastJerseyTestContainer testContainer;
    private static TestFeedCreator testFeedCreator;

    @BeforeClass
    public static void setUpClass() throws Exception {
        //TODO: init is still needed here to assure that dmaap is not null
        DatabaseClass.getDmaap().init(DMAAP_OBJECT_FACTORY.genDmaap());

        testContainer = new FastJerseyTestContainer(new ResourceConfig()
            .register(DR_SubResource.class)
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

    //TODO: figure out generic entity list unmarshall to check the entity list
    @Test
    public void getDr_Subs_test() {
        Response resp = testContainer.target("dr_subs").request().get(Response.class);
        System.out.println("GET dr_subs resp=" + resp.getStatus());

        assertEquals(200, resp.getStatus());
        assertTrue(resp.hasEntity());
    }

    @Test
    public void addDr_Sub_shallReturnError_whenNoFeedIdAndFeedNameInSubProvided() {
        //given
        DR_Sub drSub = new DR_Sub(DCAE_LOCATION_NAME, USERNAME, USRPWD, null, DELIVERY_URL, LOG_URL, true);
        Entity<DR_Sub> requestedEntity = Entity.entity(drSub, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target("dr_subs")
            .request()
            .post(requestedEntity, Response.class);

        //then
        assertEquals(400, resp.getStatus());
        ApiError responseError = resp.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("feedName", responseError.getFields());
    }

    @Test
    public void addDr_Sub_shallReturnError_whenFeedNameProvided_butFeedNotExist() {
        //given
        String notExistingFeedName = "notRealFead";
        DR_Sub drSub = new DR_Sub(DCAE_LOCATION_NAME, USERNAME, USRPWD, null, DELIVERY_URL, LOG_URL, true);
        drSub.setFeedName(notExistingFeedName);
        Entity<DR_Sub> requestedEntity = Entity.entity(drSub, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target("dr_subs")
            .request()
            .post(requestedEntity, Response.class);

        //then
        assertEquals(404, resp.getStatus());
        ApiError responseError = resp.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("feedName", responseError.getFields());
    }

    @Test
    public void addDr_Sub_shallReturnError_whenFeedNameProvided_andManyFeedsWithTheSameNameInDB() {
        //given
        String notDistinctFeedName = "notDistinctFeedName";
        Feed feed1 = new Feed(notDistinctFeedName, "1.0", "description", "dgl", "unrestricted");
        Feed feed2 = new Feed(notDistinctFeedName, "2.0", "description", "dgl", "unrestricted");
        DatabaseClass.getFeeds().put("1", feed1);
        DatabaseClass.getFeeds().put("2", feed2);
        DR_Sub drSub = new DR_Sub(DCAE_LOCATION_NAME, USERNAME, USRPWD, null, DELIVERY_URL, LOG_URL, true);
        drSub.setFeedName(notDistinctFeedName);
        Entity<DR_Sub> requestedEntity = Entity.entity(drSub, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target("dr_subs")
            .request()
            .post(requestedEntity, Response.class);

        //then
        assertEquals(409, resp.getStatus());
        ApiError responseError = resp.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("feedName", responseError.getFields());
    }

    @Test
    public void addDr_Sub_shallReturnError_whenFeedIdProvided_butFeedNotExist() {
        //given
        DR_Sub drSub = new DR_Sub(DCAE_LOCATION_NAME, USERNAME, USRPWD, "someFakeFeedId", DELIVERY_URL, LOG_URL, true);
        Entity<DR_Sub> requestedEntity = Entity.entity(drSub, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target("dr_subs")
            .request()
            .post(requestedEntity, Response.class);

        //then
        assertEquals(404, resp.getStatus());
        ApiError responseError = resp.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertTrue(responseError.getFields().contains("feedId"));
    }

    @Test
    public void addDr_Sub_shallExecuteSuccessfully_whenValidFeedIdProvided() {
        //given
        String feedId = assureFeedIsInDB();
        DR_Sub drSub = new DR_Sub(DCAE_LOCATION_NAME, USERNAME, USRPWD, feedId, DELIVERY_URL, LOG_URL, true);
        Entity<DR_Sub> requestedEntity = Entity.entity(drSub, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target("dr_subs")
            .request()
            .post(requestedEntity, Response.class);

        //then
        assertEquals(201, resp.getStatus());
        assertTrue(resp.hasEntity());
        DR_Sub created = resp.readEntity(DR_Sub.class);
        assertSubscriptionExistInDB(created);
    }

    @Test
    public void addDr_Sub_shallExecuteSuccessfully_whenValidFeedNameProvided() {
        //given
        String feedName = "testFeed";
        testFeedCreator.addFeed(feedName, "test feed");
        DR_Sub drSub = new DR_Sub(DCAE_LOCATION_NAME, USERNAME, USRPWD, null, DELIVERY_URL, LOG_URL, true);
        drSub.setFeedName(feedName);
        Entity<DR_Sub> requestedEntity = Entity.entity(drSub, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target("dr_subs")
            .request()
            .post(requestedEntity, Response.class);

        //then
        assertEquals(201, resp.getStatus());
        assertTrue(resp.hasEntity());
        DR_Sub created = resp.readEntity(DR_Sub.class);
        assertSubscriptionExistInDB(created);
    }


    @Test
    public void updateDr_Sub_shallReturnError_whenNoFeedIdInSubProvided() {
        //given
        String subId = "1234";
        DR_Sub drSub = new DR_Sub(DCAE_LOCATION_NAME, USERNAME, USRPWD, null, DELIVERY_URL, LOG_URL, true);
        Entity<DR_Sub> requestedEntity = Entity.entity(drSub, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target("dr_subs")
            .path(subId)
            .request()
            .put(requestedEntity, Response.class);

        //then
        assertEquals(400, resp.getStatus());
        ApiError responseError = resp.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("feedId", responseError.getFields());
    }

    @Test
    public void updateDr_Sub_shallReturnError_whenNoDCAELocationInSubProvided() {
        //given
        String subId = "1234";
        DR_Sub drSub = new DR_Sub(null, USERNAME, USRPWD, "someFeedId", DELIVERY_URL, LOG_URL, true);
        Entity<DR_Sub> requestedEntity = Entity.entity(drSub, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target("dr_subs")
            .path(subId)
            .request()
            .put(requestedEntity, Response.class);

        //then
        assertEquals(400, resp.getStatus());
        ApiError responseError = resp.readEntity(ApiError.class);
        assertNotNull(responseError);
        assertEquals("dcaeLocationName", responseError.getFields());
    }

    @Test
    public void updateDr_Sub_shallReturnError_whenFeedWithGivenIdInSubNotExists() {
        //given
        String subId = "1234";
        DR_Sub drSub = new DR_Sub(DCAE_LOCATION_NAME, USERNAME, USRPWD, "someFeedId", DELIVERY_URL, LOG_URL, true);
        Entity<DR_Sub> requestedEntity = Entity.entity(drSub, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target("dr_subs")
            .path(subId)
            .request()
            .put(requestedEntity, Response.class);

        //then
        assertEquals(404, resp.getStatus());
        assertNotNull(resp.readEntity(ApiError.class));
    }

    @Test
    public void updateDr_Sub_shallReturnSuccess_whenAddingNewSubscription() {
        //given
        String subId = "31";
        String feedId = assureFeedIsInDB();
        DR_Sub drSub = new DR_Sub(DCAE_LOCATION_NAME, USERNAME, USRPWD, feedId, null, null, true);
        Entity<DR_Sub> requestedEntity = Entity.entity(drSub, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target("dr_subs")
            .path(subId)
            .request()
            .put(requestedEntity, Response.class);

        //then
        assertEquals(200, resp.getStatus());

        DR_Sub createdDrSub = resp.readEntity(DR_Sub.class);
        assertNotNull(createdDrSub.getLastMod());
        assertEquals(subId, createdDrSub.getSubId());
        assertEquals(DCAE_LOCATION_NAME, createdDrSub.getDcaeLocationName());
        assertEquals(USERNAME, createdDrSub.getUsername());
        assertEquals(USRPWD, createdDrSub.getUserpwd());
        assertEquals(DELIVERY_URL_TEMPLATE + subId, createdDrSub.getDeliveryURL());
        assertEquals(LOG_URL_TEMPLATE + subId, createdDrSub.getLogURL());

        assertSubscriptionExistInDB(createdDrSub);
    }

    @Test
    public void updateDr_Sub_shallReturnSuccess_whenUpdatingExistingSubscription() {
        //given
        String feedId = assureFeedIsInDB();
        DR_Sub existingDrSub = addSub(DCAE_LOCATION_NAME, USERNAME, USRPWD, feedId);
        DR_Sub drSubUpdate = new DR_Sub("newDcaeLocationName", "newUserName", "newUserPwd", feedId, null, null, false);
        Entity<DR_Sub> requestedEntity = Entity.entity(drSubUpdate, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target("dr_subs")
            .path(existingDrSub.getSubId())
            .request()
            .put(requestedEntity, Response.class);

        //then
        assertEquals(200, resp.getStatus());

        DR_Sub updatedDrSub = resp.readEntity(DR_Sub.class);
        assertNotNull(updatedDrSub.getLastMod());
        assertEquals(existingDrSub.getSubId(), updatedDrSub.getSubId());
        assertEquals(drSubUpdate.getDcaeLocationName(), updatedDrSub.getDcaeLocationName());
        assertEquals(drSubUpdate.getUsername(), updatedDrSub.getUsername());
        assertEquals(drSubUpdate.getUserpwd(), updatedDrSub.getUserpwd());
        assertEquals(DELIVERY_URL_TEMPLATE + existingDrSub.getSubId(), updatedDrSub.getDeliveryURL());
        assertEquals(LOG_URL_TEMPLATE + existingDrSub.getSubId(), updatedDrSub.getLogURL());

        assertSubscriptionExistInDB(updatedDrSub);
    }

    @Test
    public void deleteDr_Sub_shouldDeleteSubscriptionWithSuccess() {
        //given
        String feedId = assureFeedIsInDB();
        DR_Sub dr_sub = addSub(DCAE_LOCATION_NAME, USERNAME, USRPWD, feedId);

        //when
        Response resp = testContainer.target("dr_subs")
            .path(dr_sub.getSubId())
            .request()
            .delete();

        //then
        assertEquals("Shall delete subscription with success", 204, resp.getStatus());
        assertFalse("No entity object shall be returned",resp.hasEntity());
        assertSubscriptionNotExistInDB(dr_sub.getSubId());
    }

    @Test
    public void deleteDr_Sub_shouldReturnErrorResponse_whenGivenSubIdNotFound() {
        //given
        String notExistingSubId = "6789";

        //when
        Response resp = testContainer.target("dr_subs")
            .path(notExistingSubId)
            .request()
            .delete();

        //then
        assertEquals("Shall return error, when trying to delete not existing subscription", 404, resp.getStatus());
        assertNotNull(resp.readEntity(ApiError.class));
    }

    @Test
    public void get_shallReturnExistingObject() {
        //given
        String feedId = assureFeedIsInDB();
        DR_Sub dr_sub = addSub(DCAE_LOCATION_NAME, USERNAME, USRPWD, feedId);

        //when
        Response resp = testContainer.target("dr_subs")
            .path(dr_sub.getSubId())
            .request()
            .get();

        //ten
        assertEquals("Subscription shall be found",200, resp.getStatus());
        assertEquals("Retrieved object shall be equal to eh one put into DB", dr_sub, resp.readEntity(DR_Sub.class));
    }

    @Test
    public void get_shouldReturnError_whenSubWithIdNotFound() {
        //given
        String notExistingSubId = "1234";

        //when
        Response resp = testContainer.target("dr_subs")
            .path(notExistingSubId)
            .request()
            .get();

        //then
        assertEquals("Subscription shall not be found", 404, resp.getStatus());
        assertNotNull(resp.readEntity(ApiError.class));
    }

    private DR_Sub addSub(String d, String un, String up, String feedId) {
        DR_Sub dr_sub = new DR_Sub(d, un, up, feedId,
            "https://subscriber.onap.org/foo", "https://dr-prov/sublog", true);

        Entity<DR_Sub> reqEntity2 = Entity.entity(dr_sub, MediaType.APPLICATION_JSON);
        Response resp = testContainer.target("dr_subs").request().post(reqEntity2, Response.class);
        System.out.println("POST dr_subs resp=" + resp.getStatus());
        assertEquals(201, resp.getStatus());
        dr_sub = resp.readEntity(DR_Sub.class);

        return dr_sub;
    }

    private String assureFeedIsInDB() {
        Feed feed = testFeedCreator.addFeed("SubscriberTestFeed", "feed for DR_Sub testing");
        assertNotNull("Feed shall be added into DB properly", feed);
        return feed.getFeedId();
    }


    private void assertSubscriptionNotExistInDB(String subId) {
        assertEquals(404, testContainer.target("dr_subs")
            .path(subId)
            .request()
            .get()
            .getStatus());
    }

    private void assertSubscriptionExistInDB(DR_Sub sub) {
        Response response = testContainer.target("dr_subs")
            .path(sub.getSubId())
            .request()
            .get();
        assertEquals(200, response.getStatus());
        assertTrue(response.hasEntity());
        assertEquals(sub, response.readEntity(DR_Sub.class));
    }
}


