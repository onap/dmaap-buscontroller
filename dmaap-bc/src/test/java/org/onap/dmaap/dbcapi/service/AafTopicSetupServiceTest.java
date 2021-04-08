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

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.dmaap.dbcapi.aaf.AafNamespace;
import org.onap.dmaap.dbcapi.aaf.AafRole;
import org.onap.dmaap.dbcapi.aaf.AafService;
import org.onap.dmaap.dbcapi.aaf.AafUserRole;
import org.onap.dmaap.dbcapi.aaf.DmaapGrant;
import org.onap.dmaap.dbcapi.aaf.DmaapPerm;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.Dmaap;
import org.onap.dmaap.dbcapi.model.Topic;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

@RunWith(JUnitParamsRunner.class)
public class AafTopicSetupServiceTest {

    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int NOT_FOUND = 404;
    private static final int CREATED = 201;
    private static final int OK = 200;
    private static final String TOPIC_NS_ROOT = "org.onap.dmaap.mr";
    private static final String TOPIC_PERM = "org.onap.dmaap.mr.topic";
    private static final String TOPIC_FQTN = "org.onap.dmaap.mr.sample_topic";
    private static final String IDENTITY = "dmaap-bc@dmaap-bc.onap.org";
    private AafServiceStub aafService = new AafServiceStub();
    @Mock
    private DmaapService dmaapService;
    @Mock
    private DmaapConfig dmaapConfig;
    private AafTopicSetupService aafTopicSetupService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Dmaap dmaap = new Dmaap();
        dmaap.setTopicNsRoot(TOPIC_NS_ROOT);
        given(dmaapService.getDmaap()).willReturn(dmaap);
        given(dmaapService.getTopicPerm()).willReturn(TOPIC_PERM);
        given(dmaapConfig.getProperty("aaf.CreateTopicRoles", "true")).willReturn("true");
        given(dmaapConfig.getProperty("MR.ClientDeleteLevel", "0")).willReturn("2");
        aafTopicSetupService = new AafTopicSetupService(aafService, dmaapService, dmaapConfig);
    }

    @Test
    @Parameters({"201", "409"})
    public void shouldCreatePublisherSubscriberViewerPermissions(int aafServiceReturnedCode) {
        aafService.givenReturnCode(aafServiceReturnedCode);

        aafTopicSetupService.aafTopicSetup(givenTopic(TOPIC_FQTN));

        aafService.shouldAddPerm(new DmaapPerm(TOPIC_PERM, ":topic." + TOPIC_FQTN, "pub"));
        aafService.shouldAddPerm(new DmaapPerm(TOPIC_PERM, ":topic." + TOPIC_FQTN, "sub"));
        aafService.shouldAddPerm(new DmaapPerm(TOPIC_PERM, ":topic." + TOPIC_FQTN, "view"));
    }

    @Test
    public void shouldReturnOkStatusWhenNoError() {
        aafService.givenReturnCode(201);

        ApiError apiError = aafTopicSetupService.aafTopicSetup(givenTopic(TOPIC_FQTN));

        assertOkStatus(apiError);
    }

    @Test
    @Parameters({"201", "409"})
    public void shouldAddNamespace(int aafServiceReturnedCode) {
        aafService.givenReturnCode(aafServiceReturnedCode);
        Topic topic = givenTopic(TOPIC_FQTN);

        aafTopicSetupService.aafTopicSetup(topic);

        AafNamespace namespace = new AafNamespace(TOPIC_FQTN, IDENTITY);
        aafService.shouldAddNamespace(namespace);
    }

    @Test
    @Parameters({"201", "409"})
    public void shouldCretePublisherRoleAndSetItToTopic(int aafServiceReturnedCode) {
        aafService.givenReturnCode(aafServiceReturnedCode);
        Topic topic = givenTopic(TOPIC_FQTN);

        aafTopicSetupService.aafTopicSetup(topic);

        AafRole role = new AafRole(TOPIC_FQTN, "publisher");
        aafService.shouldAddRole(role);
        assertEquals(role.getFullyQualifiedRole(), topic.getPublisherRole());
    }

    @Test
    @Parameters({"201", "409"})
    public void shouldCreteSubscriberRoleAndSetItToTopic(int aafServiceReturnedCode) {
        aafService.givenReturnCode(aafServiceReturnedCode);
        Topic topic = givenTopic(TOPIC_FQTN);

        aafTopicSetupService.aafTopicSetup(topic);

        AafRole role = new AafRole(TOPIC_FQTN, "subscriber");
        aafService.shouldAddRole(role);
        assertEquals(role.getFullyQualifiedRole(), topic.getSubscriberRole());
    }

    @Test
    @Parameters({"201", "409"})
    public void shouldGrantPubAndViewPermissionToPublisherRole(int aafServiceReturnedCode) {
        aafService.givenReturnCode(aafServiceReturnedCode);

        aafTopicSetupService.aafTopicSetup(givenTopic(TOPIC_FQTN));

        AafRole role = new AafRole(TOPIC_FQTN, "publisher");
        DmaapPerm pubPerm = new DmaapPerm(TOPIC_PERM, ":topic." + TOPIC_FQTN, "pub");
        DmaapPerm viewPerm = new DmaapPerm(TOPIC_PERM, ":topic." + TOPIC_FQTN, "view");
        aafService.shouldAddGrant(new DmaapGrant(pubPerm, role.getFullyQualifiedRole()));
        aafService.shouldAddGrant(new DmaapGrant(viewPerm, role.getFullyQualifiedRole()));
    }

    @Test
    @Parameters({"201", "409"})
    public void shouldGrantSubAndViewPermissionToSubscriberRole(int aafServiceReturnedCode) {
        aafService.givenReturnCode(aafServiceReturnedCode);

        aafTopicSetupService.aafTopicSetup(givenTopic(TOPIC_FQTN));

        AafRole role = new AafRole(TOPIC_FQTN, "subscriber");
        DmaapPerm subPerm = new DmaapPerm(TOPIC_PERM, ":topic." + TOPIC_FQTN, "sub");
        DmaapPerm viewPerm = new DmaapPerm(TOPIC_PERM, ":topic." + TOPIC_FQTN, "view");
        aafService.shouldAddGrant(new DmaapGrant(subPerm, role.getFullyQualifiedRole()));
        aafService.shouldAddGrant(new DmaapGrant(viewPerm, role.getFullyQualifiedRole()));
    }

    @Test
    public void shouldCreateOnlyPermissionsWhenCreateTopicRolesIsFalse() {
        given(dmaapConfig.getProperty("aaf.CreateTopicRoles", "true")).willReturn("false");

        aafTopicSetupService.aafTopicSetup(givenTopic(TOPIC_FQTN));

        aafService.shouldAddPerm(new DmaapPerm(TOPIC_PERM, ":topic." + TOPIC_FQTN, "pub"));
        aafService.shouldAddPerm(new DmaapPerm(TOPIC_PERM, ":topic." + TOPIC_FQTN, "sub"));
        aafService.shouldAddPerm(new DmaapPerm(TOPIC_PERM, ":topic." + TOPIC_FQTN, "view"));
        aafService.shouldHaveNoNamespaceRolesAndGrantsAdded();
    }

    @Test
    public void shouldCreateOnlyPermissionsWhenTopicFqtnDoesntStartWithNsRoot() {

        String topicFqtn = "sample_topic";
        aafTopicSetupService.aafTopicSetup(givenTopic(topicFqtn));

        aafService.shouldAddPerm(new DmaapPerm(TOPIC_PERM, ":topic." + topicFqtn, "pub"));
        aafService.shouldAddPerm(new DmaapPerm(TOPIC_PERM, ":topic." + topicFqtn, "sub"));
        aafService.shouldAddPerm(new DmaapPerm(TOPIC_PERM, ":topic." + topicFqtn, "view"));
        aafService.shouldHaveNoNamespaceRolesAndGrantsAdded();
    }

    @Test
    public void shouldHandleExceptionWhenTopicSnRootIsNotDefined() {
        Dmaap dmaap = new Dmaap();
        dmaap.setTopicNsRoot(null);
        given(dmaapService.getDmaap()).willReturn(dmaap);

        ApiError apiError = aafTopicSetupService.aafTopicSetup(givenTopic(TOPIC_FQTN));

        assertErrorStatus(apiError, INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldHandleExceptionWhenPermissionCreationWasFailed() {
        aafService.givenAddPermStatus(NOT_FOUND);

        ApiError apiError = aafTopicSetupService.aafTopicSetup(givenTopic(TOPIC_FQTN));

        assertErrorStatus(apiError, INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldHandleExceptionWhenNamespaceCreationWasFailed() {
        aafService.givenAddNamespaceStatus(NOT_FOUND);

        ApiError apiError = aafTopicSetupService.aafTopicSetup(givenTopic(TOPIC_FQTN));

        assertErrorStatus(apiError, INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldHandleExceptionWhenRoleCreationWasFailed() {
        aafService.givenAddRoleStatus(NOT_FOUND);

        ApiError apiError = aafTopicSetupService.aafTopicSetup(givenTopic(TOPIC_FQTN));

        assertErrorStatus(apiError, INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldHandleExceptionWhenGrantPermToRoleWasFailed() {
        aafService.givenAddGrantStatus(NOT_FOUND);

        ApiError apiError = aafTopicSetupService.aafTopicSetup(givenTopic(TOPIC_FQTN));

        assertErrorStatus(apiError, NOT_FOUND);
    }

    @Test
    @Parameters({"200", "404"})
    public void shouldremovePublisherSubscriberViewerPermissions(int aafServiceReturnedCode) {
        aafService.givenReturnCode(aafServiceReturnedCode);

        aafTopicSetupService.aafTopicCleanup(givenTopic(TOPIC_FQTN));

        aafService.shouldRemovePerm(new DmaapPerm(TOPIC_PERM, ":topic." + TOPIC_FQTN, "pub"));
        aafService.shouldRemovePerm(new DmaapPerm(TOPIC_PERM, ":topic." + TOPIC_FQTN, "sub"));
        aafService.shouldRemovePerm(new DmaapPerm(TOPIC_PERM, ":topic." + TOPIC_FQTN, "view"));
    }

    @Test
    @Parameters({"200", "404"})
    public void shouldRemoveNamespace(int aafServiceReturnedCode) {
        aafService.givenReturnCode(aafServiceReturnedCode);
        Topic topic = givenTopic(TOPIC_FQTN);

        aafTopicSetupService.aafTopicCleanup(topic);

        AafNamespace namespace = new AafNamespace(TOPIC_FQTN, IDENTITY);
        aafService.shouldRemoveNamespace(namespace);
    }

    @Test
    public void shouldRemoveOnlyPermissionsWhenCreateTopicRolesIsFalse() {
        given(dmaapConfig.getProperty("aaf.CreateTopicRoles", "true")).willReturn("false");

        aafTopicSetupService.aafTopicCleanup(givenTopic(TOPIC_FQTN));

        aafService.shouldRemovePerm(new DmaapPerm(TOPIC_PERM, ":topic." + TOPIC_FQTN, "pub"));
        aafService.shouldRemovePerm(new DmaapPerm(TOPIC_PERM, ":topic." + TOPIC_FQTN, "sub"));
        aafService.shouldRemovePerm(new DmaapPerm(TOPIC_PERM, ":topic." + TOPIC_FQTN, "view"));
        aafService.shouldNotRemoveNamespace();
    }

    @Test
    public void shouldRemoveOnlyPermissionsWhenTopicFqtnDoesntStartWithNsRoot() {

        String topicFqtn = "sample_topic";
        aafTopicSetupService.aafTopicCleanup(givenTopic(topicFqtn));

        aafService.shouldRemovePerm(new DmaapPerm(TOPIC_PERM, ":topic." + topicFqtn, "pub"));
        aafService.shouldRemovePerm(new DmaapPerm(TOPIC_PERM, ":topic." + topicFqtn, "sub"));
        aafService.shouldRemovePerm(new DmaapPerm(TOPIC_PERM, ":topic." + topicFqtn, "view"));
        aafService.shouldNotRemoveNamespace();
    }

    @Test
    public void shouldHandleExceptionWhenPermissionRemovalWasFailed() {
        aafService.givenRemovePermStatus(INTERNAL_SERVER_ERROR);

        ApiError apiError = aafTopicSetupService.aafTopicCleanup(givenTopic(TOPIC_FQTN));

        assertErrorStatus(apiError, INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldHandleExceptionWhenNamespaceRemovalWasFailed() {
        aafService.givenRemoveNamespaceStatus(INTERNAL_SERVER_ERROR);

        ApiError apiError = aafTopicSetupService.aafTopicCleanup(givenTopic(TOPIC_FQTN));

        assertErrorStatus(apiError, INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldNotPerformCleanupWhenDeleteLevelIsLessThanTwo() {
        given(dmaapConfig.getProperty("MR.ClientDeleteLevel", "0")).willReturn("0");

        ApiError apiError = aafTopicSetupService.aafTopicCleanup(givenTopic(TOPIC_FQTN));

        aafService.shouldNotPerformCleanup();
        assertOkStatus(apiError);
    }

    @Test
    public void shouldNotPerformCleanupWhenDeleteLevelIsNotNumericValue() {
        given(dmaapConfig.getProperty("MR.ClientDeleteLevel", "0")).willReturn("not number");

        ApiError apiError = aafTopicSetupService.aafTopicCleanup(givenTopic(TOPIC_FQTN));

        aafService.shouldNotPerformCleanup();
        assertOkStatus(apiError);
    }

    private Topic givenTopic(String topicFqtn) {
        Topic topic = new Topic();
        topic.setFqtn(topicFqtn);
        return topic;
    }

    private void assertOkStatus(ApiError apiError) {
        assertTrue(apiError.is2xx());
        assertEquals("OK", apiError.getMessage());
    }

    private void assertErrorStatus(ApiError apiError, int code) {
        assertEquals(code, apiError.getCode());
    }

    private class AafServiceStub implements AafService {

        private AafNamespace addedNamespace;
        private AafNamespace removedNamespace;
        private List<DmaapPerm> addedPerms = newArrayList();
        private List<DmaapPerm> removedPerms = newArrayList();
        private List<AafRole> addedRoles = newArrayList();
        private List<DmaapGrant> addedGrants = newArrayList();
        private int addNamespaceStatus = CREATED;
        private int addGrantStatus = CREATED;
        private int addRoleStatus = CREATED;
        private int addPermStatus = CREATED;
        private int removePermStatus = OK;
        private int removeNamespaceStatus = OK;

        @Override
        public String getIdentity() {
            return IDENTITY;
        }

        @Override
        public int addPerm(DmaapPerm perm) {
            this.addedPerms.add(perm);
            return addPermStatus;
        }

        @Override
        public int delPerm(DmaapPerm perm, boolean force) {
            removedPerms.add(perm);
            return removePermStatus;
        }

        @Override
        public int addGrant(DmaapGrant grant) {
            addedGrants.add(grant);
            return addGrantStatus;
        }

        @Override
        public int addUserRole(AafUserRole ur) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int addRole(AafRole role) {
            this.addedRoles.add(role);
            return addRoleStatus;
        }

        @Override
        public int addNamespace(AafNamespace namespace) {
            this.addedNamespace = namespace;
            return addNamespaceStatus;
        }

        @Override
        public int delNamespace(AafNamespace namespace, boolean force) {
            this.removedNamespace = namespace;
            return removeNamespaceStatus;
        }

        void givenReturnCode(int status) {
            this.addNamespaceStatus = status;
            this.addGrantStatus = status;
            this.addRoleStatus = status;
            this.addPermStatus = status;
            this.removePermStatus = status;
            this.removeNamespaceStatus = status;
        }

        void givenAddNamespaceStatus(int addNamespaceStatus) {
            this.addNamespaceStatus = addNamespaceStatus;
        }

        void givenRemoveNamespaceStatus(int removeNamespaceStatus) {
            this.removeNamespaceStatus = removeNamespaceStatus;
        }

        void givenAddGrantStatus(int addGrantStatus) {
            this.addGrantStatus = addGrantStatus;
        }

        void givenAddRoleStatus(int addRoleStatus) {
            this.addRoleStatus = addRoleStatus;
        }

        void givenAddPermStatus(int addPermStatus) {
            this.addPermStatus = addPermStatus;
        }

        void givenRemovePermStatus(int removePermStatus) {
            this.removePermStatus = removePermStatus;
        }

        void shouldAddPerm(DmaapPerm perm) {
            assertTrue(addedPerms.contains(perm));
        }

        void shouldRemovePerm(DmaapPerm perm) {
            assertTrue(removedPerms.contains(perm));
        }

        void shouldAddNamespace(AafNamespace namespace) {
            assertEquals(namespace, this.addedNamespace);
        }

        void shouldRemoveNamespace(AafNamespace namespace) {
            assertEquals(namespace, this.removedNamespace);
        }

        void shouldAddRole(AafRole role) {
            assertTrue(addedRoles.contains(role));
        }

        void shouldAddGrant(DmaapGrant grant) {
            assertTrue(addedGrants.contains(grant));
        }

        void shouldHaveNoNamespaceRolesAndGrantsAdded() {
            assertNull(this.addedNamespace);
            assertTrue(this.addedGrants.isEmpty());
            assertTrue(this.addedRoles.isEmpty());
        }

        void shouldNotRemoveNamespace() {
            assertNull(this.removedNamespace);
        }

        void shouldNotPerformCleanup() {
            shouldNotRemoveNamespace();
            assertTrue(removedPerms.isEmpty());
        }
    }
}