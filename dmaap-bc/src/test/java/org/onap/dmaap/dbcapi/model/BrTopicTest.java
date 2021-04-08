/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright 2019 IBM
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

import org.junit.Before;
import org.junit.Test;

public class BrTopicTest {

    BrTopic brTopic;

    @Before
    public void setUp() {
        brTopic = new BrTopic();
    }

    @Test
    public void testGetBrSource() {
        brTopic.setBrSource("brSource");
        assertEquals("brSource", brTopic.getBrSource());
    }

    @Test
    public void testGetBrTarget() {
        brTopic.setBrTarget("brTarget");
        assertEquals("brTarget", brTopic.getBrTarget());
    }

    @Test
    public void testGetTopicCount() {
        brTopic.setTopicCount(1);
        assertEquals(1, brTopic.getTopicCount());
    }

    @Test
    public void testGetMmAgentName() {
        brTopic.setMmAgentName("Test");
        assertEquals("Test", brTopic.getMmAgentName());
    }
}
