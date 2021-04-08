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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status;
import org.onap.dmaap.dbcapi.model.FqtnType;
import org.onap.dmaap.dbcapi.model.MR_Cluster;
import org.onap.dmaap.dbcapi.model.ReplicationType;
import org.onap.dmaap.dbcapi.model.Topic;
import org.onap.dmaap.dbcapi.testframework.DmaapObjectFactory;

public class TopicResourceTest {

    private static final DmaapObjectFactory DMAAP_OBJECT_FACTORY = new DmaapObjectFactory();
    private static final String TOPICS_TARGET = "topics";

    private static FastJerseyTestContainer testContainer;

    @BeforeClass
    public static void setUpClass() throws Exception {
        //TODO: init is still needed here to assure that dmaap is not null
        DatabaseClass.getDmaap().init(DMAAP_OBJECT_FACTORY.genDmaap());

        testContainer = new FastJerseyTestContainer(new ResourceConfig()
            .register(TopicResource.class));
        testContainer.init();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        testContainer.destroy();
    }

    @Before
    public void setUpClusterAndLocation() {
        DatabaseClass.clearDatabase();

        DcaeLocation centralDcaeLoc = DMAAP_OBJECT_FACTORY.genDcaeLocation("central");
        centralDcaeLoc.setStatus(DmaapObject_Status.VALID);
        DatabaseClass.getDcaeLocations().put(centralDcaeLoc.getDcaeLocationName(), centralDcaeLoc);

        MR_Cluster cluster = DMAAP_OBJECT_FACTORY.genMR_Cluster("central");
        cluster.setStatus(DmaapObject_Status.VALID);
        DatabaseClass.getMr_clusters().put(cluster.getDcaeLocationName(), cluster);
    }

    @Test
    public void getTopics_shouldReturnEmptyList_whenNoTopicsInDataBase() {
        //when
        Response resp = testContainer.target(TOPICS_TARGET).request().get(Response.class);

        //then
        assertEquals(HttpStatus.OK_200, resp.getStatus());
        assertTrue(resp.hasEntity());

        List<Topic> topics = resp.readEntity(new GenericType<List<Topic>>() {
        });
        assertTrue(topics.isEmpty());
    }

    @Test
    public void getTopics_shouldReturnTopicsRegisteredInDataBase() {
        //given
        Topic topic1 = DMAAP_OBJECT_FACTORY.genSimpleTopic("testTopic1");
        Topic topic2 = DMAAP_OBJECT_FACTORY.genSimpleTopic("testTopic2");
        DatabaseClass.getTopics().put(topic1.getFqtn(), topic1);
        DatabaseClass.getTopics().put(topic2.getFqtn(), topic2);

        //when
        Response resp = testContainer.target(TOPICS_TARGET).request().get(Response.class);

        //then
        assertEquals(HttpStatus.OK_200, resp.getStatus());
        assertTrue(resp.hasEntity());

        List<Topic> topics = resp.readEntity(new GenericType<List<Topic>>() {
        });
        assertEquals(2, topics.size());
        assertTrue(topics.contains(topic1));
        assertTrue(topics.contains(topic2));
    }

    @Test
    public void getTopics_shouldReturnValidationError_whenTopicNameIsInvalid() {
        //given
        String topicName = "wrong Topic Name";

        //when
        Response resp = testContainer.target(TOPICS_TARGET).path(topicName).request().get(Response.class);

        //then
        assertEquals(HttpStatus.BAD_REQUEST_400, resp.getStatus());
        assertTrue(resp.hasEntity());
        ApiError errorObj = resp.readEntity(ApiError.class);
        assertEquals("topicName", errorObj.getFields());
    }

    @Test
    public void getTopic_shouldReturnError_whenRequestedTopicNotFound() {
        //given
        String topicName = "notExistingTopic";

        //when
        Response resp = testContainer.target(TOPICS_TARGET).path(topicName).request().get(Response.class);

        //then
        assertEquals(HttpStatus.NOT_FOUND_404, resp.getStatus());
        assertTrue(resp.hasEntity());
        ApiError errorObj = resp.readEntity(ApiError.class);
        assertEquals("fqtn", errorObj.getFields());
    }

    @Test
    public void getTopic_shouldReturnTopicInformation_whenRequestedTopicExists() {
        //given
        Topic topic1 = DMAAP_OBJECT_FACTORY.genSimpleTopic("testTopic1");
        DatabaseClass.getTopics().put(topic1.getFqtn(), topic1);

        //when
        Response resp = testContainer.target(TOPICS_TARGET).path(topic1.getFqtn()).request().get(Response.class);

        //then
        assertEquals(HttpStatus.OK_200, resp.getStatus());
        assertTrue(resp.hasEntity());
        Topic retrievedTopic = resp.readEntity(Topic.class);
        assertEquals(topic1, retrievedTopic);
    }


    @Test
    public void deleteTopic_shouldReturnError_whenTopicNotFound() {
        //given
        String topicName = "notExisting";

        //when
        Response resp = testContainer.target(TOPICS_TARGET).path(topicName).request().delete(Response.class);

        //then
        assertEquals(HttpStatus.NOT_FOUND_404, resp.getStatus());
        assertTrue(resp.hasEntity());
        ApiError errorObj = resp.readEntity(ApiError.class);
        assertEquals("fqtn", errorObj.getFields());
    }

    @Test
    public void deleteTopic_shouldDeleteTopicFromDataBase_whenFound() {
        //given
        Topic topic = DMAAP_OBJECT_FACTORY.genSimpleTopic("testTopic");
        DatabaseClass.getTopics().put(topic.getFqtn(), topic);

        //when
        Response resp = testContainer.target(TOPICS_TARGET).path(topic.getFqtn()).request().delete(Response.class);

        //then
        assertEquals(HttpStatus.NO_CONTENT_204, resp.getStatus());
        assertFalse(resp.hasEntity());
    }

    @Test
    public void addTopic_shouldReturnValidationError_whenTopicNameIsInvalid() {
        //given
        Topic topic = DMAAP_OBJECT_FACTORY.genSimpleTopic("wrong topic name with spaces");
        Entity<Topic> requestedEntity = Entity.entity(topic, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target(TOPICS_TARGET).request().post(requestedEntity, Response.class);

        //then
        assertEquals(HttpStatus.BAD_REQUEST_400, resp.getStatus());
        assertTrue(resp.hasEntity());
        ApiError errorObj = resp.readEntity(ApiError.class);
        assertEquals("topicName", errorObj.getFields());
    }

    @Test
    public void addTopic_shouldReturnValidationError_whenTopicDescriptionNotProvided() {
        //given
        Topic topic = DMAAP_OBJECT_FACTORY.genSimpleTopic("topicName");
        topic.setTopicDescription(null);
        Entity<Topic> requestedEntity = Entity.entity(topic, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target(TOPICS_TARGET).request().post(requestedEntity, Response.class);

        //then
        assertEquals(HttpStatus.BAD_REQUEST_400, resp.getStatus());
        assertTrue(resp.hasEntity());
        ApiError errorObj = resp.readEntity(ApiError.class);
        assertEquals("topicDescription", errorObj.getFields());
    }

    @Test
    public void addTopic_shouldReturnValidationError_whenTopicOwnerNotProvided() {
        //given
        Topic topic = DMAAP_OBJECT_FACTORY.genSimpleTopic("topicName");
        topic.setOwner(null);
        Entity<Topic> requestedEntity = Entity.entity(topic, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target(TOPICS_TARGET).request().post(requestedEntity, Response.class);

        //then
        assertEquals(HttpStatus.BAD_REQUEST_400, resp.getStatus());
        assertTrue(resp.hasEntity());
        ApiError errorObj = resp.readEntity(ApiError.class);
        assertEquals("owner", errorObj.getFields());
    }

    @Test
    public void addTopic_shouldReturnError_whenTopicAlreadyExist() {
        //given
        Topic topic = DMAAP_OBJECT_FACTORY.genSimpleTopic("topicName");
        DatabaseClass.getTopics().put(topic.getFqtn(), topic);
        Entity<Topic> requestedEntity = Entity.entity(topic, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target(TOPICS_TARGET).request().post(requestedEntity, Response.class);

        //then
        assertEquals(HttpStatus.CONFLICT_409, resp.getStatus());
        assertTrue(resp.hasEntity());
        ApiError errorObj = resp.readEntity(ApiError.class);
        assertEquals("fqtn", errorObj.getFields());
    }

    @Test
    public void addTopic_shouldReturnExistingTopic_whenTopicAlreadyExist_andUseExistingQueryParamUsed() {
        //given
        Topic topic = DMAAP_OBJECT_FACTORY.genSimpleTopic("topicName");
        DatabaseClass.getTopics().put(topic.getFqtn(), topic);
        Entity<Topic> requestedEntity = Entity.entity(topic, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target(TOPICS_TARGET).queryParam("useExisting", true).request()
            .post(requestedEntity, Response.class);

        //then
        assertEquals(HttpStatus.CREATED_201, resp.getStatus());
        assertTrue(resp.hasEntity());
        assertEquals(topic, resp.readEntity(Topic.class));
    }

    @Test
    public void addTopic_shouldReturnError_whenAddingTopicWithInvalidGlobalMRclusterHostname() {
        Topic topic = DMAAP_OBJECT_FACTORY.genSimpleTopic("topicName");
        topic.setReplicationCase(ReplicationType.REPLICATION_CENTRAL_TO_GLOBAL);
        topic.setGlobalMrURL("some.invalid.Glob$al.M@R.ur)l");
        Entity<Topic> requestedEntity = Entity.entity(topic, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target(TOPICS_TARGET).request().post(requestedEntity, Response.class);

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR_500, resp.getStatus());
        assertTrue(resp.hasEntity());
        ApiError errorObj = resp.readEntity(ApiError.class);
        assertEquals("globalMrURL", errorObj.getFields());
    }

    @Test
    public void addTopic_shouldAddTopicWithDefaultOptionalValues_whenNotProvided() {
        Topic topic = DMAAP_OBJECT_FACTORY.genSimpleTopic("topicName");
        Entity<Topic> requestedEntity = Entity.entity(topic, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target(TOPICS_TARGET).request().post(requestedEntity, Response.class);

        //then
        assertEquals(HttpStatus.CREATED_201, resp.getStatus());
        assertTrue(resp.hasEntity());
        Topic createdTopic = resp.readEntity(Topic.class);
        assertEquals(topic, createdTopic);
        assertEquals(FqtnType.FQTN_LEGACY_FORMAT, createdTopic.getFqtnStyle());
        assertEquals("2", createdTopic.getPartitionCount());
        assertEquals("1", createdTopic.getReplicationCount());
    }

    @Test
    public void addTopic_shouldAddTopicWithOriginalOptionalValues_whenProvided() {
        Topic topic = DMAAP_OBJECT_FACTORY.genSimpleTopic("topicName");
        topic.setFqtnStyle(FqtnType.FQTN_PROJECTID_FORMAT);
        topic.setFqtn(topic.genFqtn());
        topic.setPartitionCount("6");
        topic.setReplicationCount("9");
        Entity<Topic> requestedEntity = Entity.entity(topic, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target(TOPICS_TARGET).request().post(requestedEntity, Response.class);

        //then
        assertEquals(HttpStatus.CREATED_201, resp.getStatus());
        assertTrue(resp.hasEntity());
        Topic createdTopic = resp.readEntity(Topic.class);
        assertEquals(topic, createdTopic);
        assertEquals(FqtnType.FQTN_PROJECTID_FORMAT, createdTopic.getFqtnStyle());
        assertEquals("6", createdTopic.getPartitionCount());
        assertEquals("9", createdTopic.getReplicationCount());
    }

    @Test
    public void updateTopic_shouldReturnError_withInformationThatItIsNotSupported() {
        //given
        Topic topic = DMAAP_OBJECT_FACTORY.genSimpleTopic("topicName");
        DatabaseClass.getTopics().put(topic.getFqtn(), topic);
        topic.setOwner("newOwner");
        Entity<Topic> requestedEntity = Entity.entity(topic, MediaType.APPLICATION_JSON);

        //when
        Response resp = testContainer.target(TOPICS_TARGET).path(topic.getFqtn()).request()
            .put(requestedEntity, Response.class);

        //then
        assertEquals(HttpStatus.BAD_REQUEST_400, resp.getStatus());
        assertTrue(resp.hasEntity());
        ApiError errorObj = resp.readEntity(ApiError.class);
        assertEquals(TopicResource.UNSUPPORTED_PUT_MSG, errorObj.getMessage());
    }

}

