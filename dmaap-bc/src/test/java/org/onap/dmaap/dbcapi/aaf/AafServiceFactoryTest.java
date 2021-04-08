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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.dmaap.dbcapi.aaf.AafService.ServiceType;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AafServiceFactoryTest {

    private static final String USE_AAF = "true";
    private static final String AAF_URL = "https://aaf.url/api";
    private static final String ADMIN_USER = "admin_user";
    private static final String TOPIC_MANAGER = "topic_manager";
    private static final String ADMIN_PASS = "admin_pass";
    private static final String MANAGER_PASS = "manager_pass";
    @Mock
    private DmaapConfig dmaapConfig;
    private AafServiceFactory aafServiceFactory;

    @Before
    public void setUp() throws Exception {
        aafServiceFactory = new AafServiceFactory(dmaapConfig);
    }

    @Test
    public void shouldBuildAafServiceForAafAdmin() {
        givenDmaapConfig();

        AafServiceImpl aafService = (AafServiceImpl) aafServiceFactory.initAafService(ServiceType.AAF_Admin);

        assertEquals(ADMIN_USER, aafService.getIdentity());
        assertEquals(AAF_URL, aafService.getAafUrl());
        assertTrue(aafService.isUseAAF());
    }

    @Test
    public void shouldBuildAafServiceForTopicManager() {
        givenDmaapConfig();

        AafServiceImpl aafService = (AafServiceImpl) aafServiceFactory.initAafService(ServiceType.AAF_TopicMgr);

        assertEquals(TOPIC_MANAGER, aafService.getIdentity());
        assertEquals(AAF_URL, aafService.getAafUrl());
        assertTrue(aafService.isUseAAF());
    }

    @Test
    public void shouldCorrectlyCreateCredentialsForAafAdmin() {
        givenDmaapConfig();

        AafServiceFactory.AafCred cred = aafServiceFactory.getCred(ServiceType.AAF_Admin);

        assertEquals(ADMIN_USER, cred.getIdentity());
        assertEquals(ADMIN_USER + ":" + new AafDecrypt().decrypt(ADMIN_PASS), cred.toString());
    }

    @Test
    public void shouldCorrectlyCreateCredentialsForTopicManager() {
        givenDmaapConfig();

        AafServiceFactory.AafCred cred = aafServiceFactory.getCred(ServiceType.AAF_TopicMgr);

        assertEquals(TOPIC_MANAGER, cred.getIdentity());
        assertEquals(TOPIC_MANAGER + ":" + new AafDecrypt().decrypt(MANAGER_PASS), cred.toString());
    }

    private void givenDmaapConfig() {
        given(dmaapConfig.getProperty("UseAAF", "false")).willReturn(USE_AAF);
        given(dmaapConfig.getProperty("aaf.URL", "https://authentication.domain.netset.com:8100/proxy/")).willReturn(AAF_URL);
        given(dmaapConfig.getProperty("aaf.AdminUser", "noMechId@domain.netset.com")).willReturn(ADMIN_USER);
        given(dmaapConfig.getProperty("aaf.TopicMgrUser", "noMechId@domain.netset.com")).willReturn(TOPIC_MANAGER);
        given(dmaapConfig.getProperty("aaf.AdminPassword", "notSet")).willReturn(ADMIN_PASS);
        given(dmaapConfig.getProperty("aaf.TopicMgrPassword", "notSet")).willReturn(MANAGER_PASS);
    }
}