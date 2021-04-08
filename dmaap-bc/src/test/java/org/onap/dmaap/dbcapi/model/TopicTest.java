/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (c) 2019 IBM
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
package org.onap.dmaap.dbcapi.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

public class TopicTest {

    ReflectionHarness rh = new ReflectionHarness();

    String f, t, d, e, o;

    @Before
    public void setUp() throws Exception {
        f = "org.onap.dmaap.interestingTopic";
        t = "interestingTopic";
        d = "A so very interesting topic";
        e = "Yes";
        o = "m12345";
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() {
        rh.reflect("org.onap.dmaap.dbcapi.model.Topic", "get", null);
    }

    @Test
    public void test2() {
        Topic obj = new Topic(f, t, d, e, o);
        assertTrue(f.equals(obj.getFqtn()));
        assertTrue(t.equals(obj.getTopicName()));
        assertTrue(d.equals(obj.getTopicDescription()));
        assertTrue(e.equals(obj.getTnxEnabled()));
        assertTrue(o.equals(obj.getOwner()));
    }

    @Test
    public void test3() {
        String v = "Validate";
        rh.reflect("org.onap.dmaap.dbcapi.model.Topic", "set", v);
    }

    @Test
    public void getNumClientsHavingMRClientListNull() {
        Topic obj = new Topic(f, t, d, e, o);
        obj.setClients(null);
        assertEquals(0, obj.getNumClients());
    }

    @Test
    public void testTopicInitializationWithInvalidJsonString() {
        String json = "{\"key\":\"value\"";
        Topic obj = new Topic(json);
        assertEquals(DmaapObject_Status.INVALID, obj.getStatus());
    }

}
