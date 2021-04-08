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
import org.onap.dmaap.dbcapi.aaf.AafService;
import org.onap.dmaap.dbcapi.aaf.AafUserRole;
import org.onap.dmaap.dbcapi.aaf.DmaapGrant;
import org.onap.dmaap.dbcapi.aaf.DmaapPerm;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.MR_Client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status.INVALID;
import static org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status.VALID;

@RunWith(JUnitParamsRunner.class)
public class AafPermissionServiceTest {

    private static final String ROLE = "dmaap.mr.demoTopic.publisher";
    private static final String IDENTITY = "dmaap-bc@dmaap-bc.onap.org";
    private static final String TOPIC_PERM = "org.onap.dmaap.mr.topic";
    private static final String FQTN = "org.onap.dmaap.mr.demoTopic";
    private static final String PUB_ACTION = "pub";
    private static final int INTERNAL_SERVER_ERROR = 500;
    @Mock
    private AafService aafService;
    @Mock
    private DmaapService dmaapService;
    @Mock
    private MR_Client mrClient;
    private AafPermissionService aafPermissionService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        aafPermissionService = new AafPermissionService(aafService, dmaapService);
        given(mrClient.getClientIdentity()).willReturn(IDENTITY);
        given(mrClient.getFqtn()).willReturn(FQTN);
        given(mrClient.getAction()).willReturn(new String[]{PUB_ACTION});
        given(dmaapService.getTopicPerm()).willReturn(TOPIC_PERM);
    }

    @Test
    @Parameters({"201", "409"})
    public void shouldAssignClientToRole(int aafServiceReturnedCode) {
        AafUserRole userRole = new AafUserRole(IDENTITY, ROLE);
        given(aafService.addUserRole(userRole)).willReturn(aafServiceReturnedCode);

        ApiError apiError = aafPermissionService.assignClientToRole(mrClient, ROLE);

        then(aafService).should().addUserRole(userRole);
        then(mrClient).should().setStatus(VALID);
        assertOkStatus(apiError);
    }

    @Test
    public void shouldReturnErrorStatusWhenClientWasNotAssignedToRole() {
        AafUserRole userRole = new AafUserRole(IDENTITY, ROLE);
        given(aafService.addUserRole(userRole)).willReturn(INTERNAL_SERVER_ERROR);

        ApiError apiError = aafPermissionService.assignClientToRole(mrClient, ROLE);

        then(mrClient).should().setStatus(INVALID);
        assertErrorStatus(apiError, INTERNAL_SERVER_ERROR);
    }

    @Test
    @Parameters({"201", "409"})
    public void shouldGrantActionPermissionForClientRole(int aafServiceReturnedCode) {
        DmaapGrant grant = new DmaapGrant(new DmaapPerm(TOPIC_PERM, ":topic." + FQTN, PUB_ACTION), ROLE);
        given(mrClient.getClientRole()).willReturn(ROLE);
        given(aafService.addGrant(grant)).willReturn(aafServiceReturnedCode);

        ApiError apiError = aafPermissionService.grantClientRolePerms(mrClient);

        then(aafService).should().addGrant(grant);
        then(mrClient).should().setStatus(VALID);
        assertOkStatus(apiError);
    }

    @Test
    public void shouldReturnErrorStatusWhenPermissionWasNotGrantToRole() {
        DmaapGrant grant = new DmaapGrant(new DmaapPerm(TOPIC_PERM, ":topic." + FQTN, PUB_ACTION), ROLE);
        given(mrClient.getClientRole()).willReturn(ROLE);
        given(aafService.addGrant(grant)).willReturn(INTERNAL_SERVER_ERROR);

        ApiError apiError = aafPermissionService.grantClientRolePerms(mrClient);

        then(mrClient).should().setStatus(INVALID);
        assertErrorStatus(apiError, INTERNAL_SERVER_ERROR);
    }

    @Test
    public void shouldReturnOkStatusWhenClientRoleIsNull() {
        given(mrClient.getClientRole()).willReturn(null);

        ApiError apiError = aafPermissionService.grantClientRolePerms(mrClient);

        verifyZeroInteractions(aafService);
        then(mrClient).should().setStatus(VALID);
        assertOkStatus(apiError);
    }

    private void assertErrorStatus(ApiError apiError, int code) {
        assertEquals(code, apiError.getCode());
    }

    private void assertOkStatus(ApiError apiError) {
        assertTrue(apiError.is2xx());
        assertEquals("OK", apiError.getMessage());
    }
}