/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2019 IBM.
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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

public class MRClientTest {

    private static final String fmt = "%24s: %s%n";

    ReflectionHarness rh = new ReflectionHarness();

    String d, t, f, c, m;

    @Before
    public void setUp() throws Exception {
        d = "central-onap";
        t = "org.onap.dmaap.interestingTopic";
        f = "mrc.onap.org:3904/events/org.onap.dmaap.interestingTopic";
        c = "publisher";
        m = "m12345";
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test1() {

        // can't use simple reflection to test for null since null constructor
        // initializes some fields.
        // rh.reflect( "org.onap.dmaap.dbcapi.model.MR_Client", "get", null );
        // so brute force instead...
        String[] a = { "put", "view" };
        MR_Client m = new MR_Client();

        assertTrue(null == m.getDcaeLocationName());
        assertTrue(null == m.getFqtn());
        assertTrue(null == m.getClientRole());
        assertTrue(null == m.getAction());

    }

    @Test
    public void test2() {
        String[] a = { "put", "view" };
        MR_Client m = new MR_Client(d, f, c, a);

        assertTrue(d.equals(m.getDcaeLocationName()));
        assertTrue(f.equals(m.getFqtn()));
        assertTrue(c.equals(m.getClientRole()));
        String[] ma = m.getAction();
        assertTrue(a.length == ma.length);
        for (int i = 0; i < a.length; i++) {
            assertTrue(a[i].equals(ma[i]));
        }
    }

    @Test
    public void test3() {

        String v = "Validate";
        rh.reflect("org.onap.dmaap.dbcapi.model.MR_Client", "set", v);
    }

    @Test
    public void test4() {
        MR_Client mrClient = new MR_Client();
        String stringArray[] = { "test" };
        mrClient.setAction(stringArray);
        mrClient.hasAction("");
        mrClient.setMrClientId("mrClientId");
        mrClient.setTopicURL("testTopicURL");
        mrClient.setClientIdentity("clientIdentity");

        assertEquals("clientIdentity", mrClient.getClientIdentity());
        assertEquals("testTopicURL", mrClient.getTopicURL());
        assertEquals("mrClientId", mrClient.getMrClientId());
        assertEquals(false, mrClient.isPublisher());
        assertEquals(false, mrClient.isSubscriber());
        assertEquals("test", mrClient.getAction()[0]);

    }

}
