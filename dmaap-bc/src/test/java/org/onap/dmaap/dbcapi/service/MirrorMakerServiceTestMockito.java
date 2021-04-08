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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
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

    @Before
    public void setUp() throws Exception {
   
    }

    @Test
    public void init_normalConstructor() throws Exception {
        //given
        

        //when
        

        //then
        assertEquals( MirrorMakerService.getProvUserPwd(), MirrorMakerService.PROV_PWD_DEFAULT);
        assertEquals( MirrorMakerService.getDefaultConsumerPort(), MirrorMakerService.TARGET_REPLICATION_PORT_DEFAULT);
        assertEquals( MirrorMakerService.getDefaultProducerPort(), MirrorMakerService.SOURCE_REPLICATION_PORT_DEFAULT);
    }

  // Todo: learn how to make more tests in Mockito


}
