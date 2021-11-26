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

package org.onap.dmaap.dbcapi.aaf;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verifyZeroInteractions;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(JUnitParamsRunner.class)
public class AafServiceImplTest {

    private static final String AAF_URL = "https://aaf.url/";
    private static final String IDENTITY = "dmaap-bc@onap.org";
    private static final boolean USE_AAF = true;
    private static final int CREATED = 201;
    private static final int OK = 200;
    @Mock
    private AafConnection aafConnection;
    private AafServiceImpl aafService;

    @Before
    public void setUp() throws Exception {
        System.setProperty("ConfigFile", "src/test/resources/dmaapbc.properties");
        MockitoAnnotations.initMocks(this);
        given(aafConnection.postAaf(any(AafObject.class), anyString())).willReturn(CREATED);
        given(aafConnection.delAaf(any(AafObject.class), anyString())).willReturn(OK);
        aafService = new AafServiceImpl(USE_AAF, AAF_URL, IDENTITY, aafConnection);
    }

    @Test
    public void shouldReturnCorrectIdentity() {
        assertEquals(IDENTITY, aafService.getIdentity());
    }

    @Test
    public void shouldAddPermission() {
        DmaapPerm perm = new DmaapPerm("perm", "type", "action");

        int status = aafService.addPerm(perm);

        then(aafConnection).should().postAaf(perm, AAF_URL + "authz/perm");
        assertEquals(CREATED, status);
    }


    @Test
    public void shouldAddDmaapGrant() {
        DmaapGrant grant = new DmaapGrant(new DmaapPerm("perm", "type", "action"), "roles");

        int status = aafService.addGrant(grant);

        then(aafConnection).should().postAaf(grant, AAF_URL + "authz/role/perm");
        assertEquals(CREATED, status);
    }

    @Test
    public void shouldAddUserRole() {
        AafUserRole userRole = new AafUserRole("ident", "role");

        int status = aafService.addUserRole(userRole);

        then(aafConnection).should().postAaf(userRole, AAF_URL + "authz/userRole");
        assertEquals(CREATED, status);
    }

    @Test
    public void shouldAddRole() {
        AafRole role = new AafRole("ns", "role");

        int status = aafService.addRole(role);

        then(aafConnection).should().postAaf(role, AAF_URL + "authz/role");
        assertEquals(CREATED, status);
    }

    @Test
    public void shouldAddNamespace() {
        AafNamespace ns = new AafNamespace("ns", "ident");

        int status = aafService.addNamespace(ns);

        then(aafConnection).should().postAaf(ns, AAF_URL + "authz/ns");
        assertEquals(CREATED, status);
    }

    @Test
    public void shouldNotConnectToAafDuringCreate() {
        aafService = new AafServiceImpl(false, AAF_URL, IDENTITY, aafConnection);
        DmaapPerm perm = new DmaapPerm("perm", "type", "action");

        int status = aafService.addPerm(perm);

        verifyZeroInteractions(aafConnection);
        assertEquals(CREATED, status);
    }

    @Test
    @Parameters({"401", "403", "409", "200", "500"})
    public void shouldHandleErrorDuringCreate(int aafServiceReturnedCode) {
        given(aafConnection.postAaf(any(AafObject.class), anyString())).willReturn(aafServiceReturnedCode);
        DmaapPerm perm = new DmaapPerm("perm", "type", "action");

        int status = aafService.addPerm(perm);

        assertEquals(aafServiceReturnedCode, status);
    }

    @Test
    @Parameters({"401", "403", "404", "200", "500"})
    public void shouldHandleErrorDuringDelete(int aafServiceReturnedCode) {
        given(aafConnection.delAaf(any(AafObject.class), anyString())).willReturn(aafServiceReturnedCode);
        DmaapPerm perm = new DmaapPerm("perm", "type", "action");

        int status = aafService.delPerm(perm, false);

        assertEquals(aafServiceReturnedCode, status);
    }

    @Test
    public void shouldDeletePermission() {
        DmaapPerm perm = new DmaapPerm("permName", "type", "action");

        int status = aafService.delPerm(perm, false);

        then(aafConnection).should().delAaf(any(AafEmpty.class), eq(AAF_URL + "authz/perm/permName/type/action"));
        assertEquals(OK, status);
    }

    @Test
    public void shouldDeletePermissionWithForce() {
        DmaapPerm perm = new DmaapPerm("permName", "type", "action");

        int status = aafService.delPerm(perm, true);

        then(aafConnection).should().delAaf(any(AafEmpty.class), eq(AAF_URL + "authz/perm/permName/type/action?force=true"));
        assertEquals(OK, status);
    }

    @Test
    public void shouldDeleteNamespace() {
        AafNamespace ns = new AafNamespace("nsName", "ident");

        int status = aafService.delNamespace(ns, false);

        then(aafConnection).should().delAaf(any(AafEmpty.class), eq(AAF_URL + "authz/ns/nsName"));
        assertEquals(OK, status);
    }

    @Test
    public void shouldDeleteNamespaceWithForce() {
        AafNamespace ns = new AafNamespace("nsName", "ident");

        int status = aafService.delNamespace(ns, true);

        then(aafConnection).should().delAaf(any(AafEmpty.class), eq(AAF_URL + "authz/ns/nsName?force=true"));
        assertEquals(OK, status);
    }

    @Test
    public void shouldReturnExpectedCodeDuringPostWhenUseAffIsSetToFalse() {
        aafService = new AafServiceImpl(false, AAF_URL, IDENTITY, aafConnection);
        DmaapPerm perm = new DmaapPerm("perm", "type", "action");

        int status = aafService.addPerm(perm);

        assertEquals(CREATED, status);
    }

    @Test
    public void shouldReturnExpectedCodeDuringDeleteWhenUseAffIsSetToFalse() {
        aafService = new AafServiceImpl(false, AAF_URL, IDENTITY, aafConnection);
        DmaapPerm perm = new DmaapPerm("perm", "type", "action");

        int status = aafService.delPerm(perm, false);

        assertEquals(OK, status);
    }
}