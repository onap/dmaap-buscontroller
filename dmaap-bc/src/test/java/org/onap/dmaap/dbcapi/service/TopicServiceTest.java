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

package org.onap.dmaap.dbcapi.service;

import com.google.common.collect.ImmutableMap;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.MR_Client;
import org.onap.dmaap.dbcapi.model.Topic;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.onap.dmaap.dbcapi.model.ReplicationType.REPLICATION_GLOBAL_TO_FQDN;

@RunWith(MockitoJUnitRunner.class)
public class TopicServiceTest {

    private static final String TOPIC_FQTN = "topic_1";
    private static final String GLOBAL_MR_HOST = "global.mr.host";
    private TopicService topicService;
    @Mock
    private MR_ClientService clientService;
    @Mock
    private DmaapConfig dmaapConfig;
    @Mock
    private MR_ClusterService clusters;
    @Mock
    private DcaeLocationService locations;
    @Mock
    private MirrorMakerService bridge;
    @Mock
    private AafTopicSetupService aafTopicSetupService;

    @Before
    public void setUp() throws Exception {
        given(dmaapConfig.getProperty("MR.globalHost", "global.host.not.set")).willReturn(GLOBAL_MR_HOST);
        given(aafTopicSetupService.aafTopicSetup(any(Topic.class))).willReturn(new ApiError(200, "OK"));
        given(aafTopicSetupService.aafTopicCleanup(any(Topic.class))).willReturn(new ApiError(200, "OK"));
        createTopicService();
    }

    @Test
    public void getTopics_shouldReturnTopicsReceivedDuringServiceCreation() {

        ImmutableMap<String, Topic> topics = ImmutableMap.of(TOPIC_FQTN, new Topic());
        topicService = new TopicService(topics, clientService, dmaapConfig, clusters, locations, bridge, aafTopicSetupService);

        assertEquals(topics, topicService.getTopics());
    }

    @Test
    public void getAllTopics_shouldReturnTopicsWithClients() {

        ArrayList<MR_Client> mrClients = newArrayList(new MR_Client());
        given(clientService.getAllMrClients(TOPIC_FQTN)).willReturn(mrClients);

        List<Topic> allTopics = topicService.getAllTopics();

        assertThat(getOnlyElement(allTopics), hasCorrectFqtn(TOPIC_FQTN));
        assertEquals(mrClients, getOnlyElement(allTopics).getClients());
    }

    @Test
    public void getAllTopicsWithoutClients_shouldReturnNoClients() {

        List<Topic> allTopics = topicService.getAllTopicsWithoutClients();

        assertThat(getOnlyElement(allTopics), hasCorrectFqtn(TOPIC_FQTN));
        assertNull(getOnlyElement(allTopics).getClients());
        verifyZeroInteractions(clientService);
    }

    @Test
    public void getAllTopics_shouldCacheClients() {

        ArrayList<MR_Client> mrClients = newArrayList(new MR_Client());
        given(clientService.getAllMrClients(TOPIC_FQTN)).willReturn(mrClients);

        topicService.getAllTopics();
        List<Topic> allTopics = topicService.getAllTopicsWithoutClients();

        assertThat(getOnlyElement(allTopics), hasCorrectFqtn(TOPIC_FQTN));
        assertEquals(mrClients, getOnlyElement(allTopics).getClients());
    }

    @Test
    public void getTopic_shouldReturnTopicByFqtn() {

        ApiError apiError = new ApiError();
        Topic topic = topicService.getTopic(TOPIC_FQTN, apiError);

        assertThat(topic, hasCorrectFqtn(TOPIC_FQTN));
        assertEquals(OK.getStatusCode(), apiError.getCode());
    }

    @Test
    public void getTopic_shouldReturnTopicWithMrClients() {

        ArrayList<MR_Client> mrClients = newArrayList(new MR_Client());
        given(clientService.getAllMrClients(TOPIC_FQTN)).willReturn(mrClients);

        Topic topic = topicService.getTopic(TOPIC_FQTN, new ApiError());

        assertThat(topic, hasCorrectFqtn(TOPIC_FQTN));
        assertEquals(mrClients, topic.getClients());
    }

    @Test
    public void getTopic_shouldReturnError() {

        ApiError apiError = new ApiError();
        Topic topic = topicService.getTopic("not_existing", apiError);

        assertNull(topic);
        assertEquals(NOT_FOUND.getStatusCode(), apiError.getCode());
    }

    @Test
    public void addTopic_shouldAddNewTopic() {
        Topic newTopic = createTopic("");

        ApiError apiError = new ApiError();
        Topic addedTopic = topicService.addTopic(newTopic, apiError, true);

        assertSame(newTopic, addedTopic);
        assertEquals(OK.getStatusCode(), apiError.getCode());
        assertNotNull(topicService.getTopic(addedTopic.getFqtn(), new ApiError()));
    }

    @Test
    public void addTopic_shouldReturnErrorWhenTopicAlreadyExists() {
        Topic newTopic = createTopic("");

        ApiError apiError = new ApiError();
        Topic addedTopic = topicService.addTopic(newTopic, apiError, false);
        Topic secondAddedTopic = topicService.addTopic(addedTopic, apiError, false);

        assertNull(secondAddedTopic);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), apiError.getCode());
    }

    @Test
    public void addTopic_shouldAddTheSameTopicWhenUseExistingIsSet() {
        Topic newTopic = createTopic("");

        ApiError apiError = new ApiError();
        Topic addedTopic = topicService.addTopic(newTopic, apiError, false);
        Topic secondAddedTopic = topicService.addTopic(addedTopic, apiError, true);

        assertSame(addedTopic, secondAddedTopic);
        assertEquals(OK.getStatusCode(), apiError.getCode());
        assertNotNull(topicService.getTopic(secondAddedTopic.getFqtn(), new ApiError()));
    }


    @Test
    public void addTopic_shouldSetGlobalMrURL() {
        Topic newTopic = createTopic(TOPIC_FQTN);
        newTopic.setReplicationCase(REPLICATION_GLOBAL_TO_FQDN);

        ApiError apiError = new ApiError();
        Topic addedTopic = topicService.addTopic(newTopic, apiError, true);

        assertEquals(OK.getStatusCode(), apiError.getCode());
        assertEquals(GLOBAL_MR_HOST, addedTopic.getGlobalMrURL());
    }

    @Test
    public void addTopic_shouldReturnErrorWhenGlobalMrURLIsInvalid() {
        given(dmaapConfig.getProperty("MR.globalHost", "global.host.not.set")).willReturn("invalid@host");
        createTopicService();
        Topic newTopic = createTopic(TOPIC_FQTN);
        newTopic.setReplicationCase(REPLICATION_GLOBAL_TO_FQDN);

        ApiError apiError = new ApiError();
        Topic addedTopic = topicService.addTopic(newTopic, apiError, true);

        assertEquals(500, apiError.getCode());
        assertNull(addedTopic);
    }

    @Test
    public void removeTopic_shouldFailIfTopicDoesNotExist() {
        ApiError apiError = new ApiError();

        Topic removedTopic = topicService.removeTopic("not_existing_fqtn", apiError);

        assertNull(removedTopic);
        assertEquals(NOT_FOUND.getStatusCode(), apiError.getCode());
        assertTrue(topicService.getTopics().containsKey(TOPIC_FQTN));
    }

    @Test
    public void removeTopic_shouldExecuteAafCleanup() {
        ApiError apiError = new ApiError();

        Topic removedTopic = topicService.removeTopic(TOPIC_FQTN, apiError);

        then(aafTopicSetupService).should().aafTopicCleanup(removedTopic);
        assertEquals(OK.getStatusCode(), apiError.getCode());
    }

    @Test
    public void removeTopic_shouldRemoveEachMrClientAssignedToTopic() {
        ApiError apiError = new ApiError();
        MR_Client mrClient = new MR_Client();
        mrClient.setMrClientId("mrClientId");

        given(clientService.getAllMrClients(TOPIC_FQTN)).willReturn(newArrayList(mrClient));

        topicService.removeTopic(TOPIC_FQTN, apiError);

        then(clientService).should().removeMr_Client(mrClient.getMrClientId(), false, apiError);
        assertEquals(OK.getStatusCode(), apiError.getCode());
    }

    @Test
    public void removeTopic_shouldRemoveTopicFromCache() {
        ApiError apiError = new ApiError();

        topicService.removeTopic(TOPIC_FQTN, apiError);

        assertTrue(topicService.getTopics().isEmpty());
        assertEquals(OK.getStatusCode(), apiError.getCode());
    }

    @Test
    public void removeTopic_shouldFailIfAafCleanupWasFailed() {
        ApiError apiError = new ApiError();
        given(aafTopicSetupService.aafTopicCleanup(any(Topic.class))).willReturn(new ApiError(404, "sth went wrong"));

        Topic removedTopic = topicService.removeTopic(TOPIC_FQTN, apiError);

        assertNull(removedTopic);
        assertEquals(404, apiError.getCode());
        assertTrue(topicService.getTopics().containsKey(TOPIC_FQTN));
    }

    private void createTopicService() {
        Map<String, Topic> mrTopics = new HashMap<>();
        mrTopics.put(TOPIC_FQTN, createTopic(TOPIC_FQTN));
        topicService = new TopicService(mrTopics, clientService, dmaapConfig, clusters, locations, bridge, aafTopicSetupService);
    }

    private Topic createTopic(String fqtn) {
        return new Topic(fqtn, "name", "desc", "tnxEnabled", "owner");
    }

    public static Matcher<Topic> hasCorrectFqtn(final String fqtn) {
        return new BaseMatcher<Topic>() {
            public boolean matches(Object o) {
                return fqtn.equals(((Topic) o).getFqtn());
            }

            public void describeTo(Description description) {
                description.appendText("Topics should should be equal. Expected fqtn: ").appendValue(fqtn);
            }
        };
    }

}
