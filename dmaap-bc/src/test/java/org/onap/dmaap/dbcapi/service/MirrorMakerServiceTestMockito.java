/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.aaf.cadi.filter.CadiFilter;
import org.onap.dmaap.dbcapi.model.MirrorMaker;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

@RunWith(MockitoJUnitRunner.class)
public class MirrorMakerServiceTestMockito {

    @Spy
    private MirrorMakerService service;

    @Mock
    private CadiFilter cadiFilterMock;
    @Mock
    private HttpServletRequest servletRequest;
    @Mock
    private HttpServletResponse servletResponse;
   
    @Mock
    private DmaapConfig dmaapConfig;
    
    @Mock
    private MirrorMaker mm = new MirrorMaker();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.setProperty("ConfigFile", "src/test/resources/dmaapbc.properties");
    }

    @Test
    public void init_normalConstructor() {
        assertEquals( MirrorMakerService.getProvUserPwd(), MirrorMakerService.PROV_PWD_DEFAULT);
        assertEquals( MirrorMakerService.getDefaultConsumerPort(), MirrorMakerService.TARGET_REPLICATION_PORT_DEFAULT);
        assertEquals( MirrorMakerService.getDefaultProducerPort(), MirrorMakerService.SOURCE_REPLICATION_PORT_DEFAULT);
    }

}
