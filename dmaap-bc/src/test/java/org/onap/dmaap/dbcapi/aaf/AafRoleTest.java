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

package org.onap.dmaap.dbcapi.aaf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class AafRoleTest {

    AafRole aafRole;

    @Before
    public void setUp() {
        aafRole = new AafRole("testNs", "testRole");
    }

    @Test
    public void testAafRole() {
        aafRole.setNamespace("namespace");
        aafRole.setRole("role");
        assertEquals("namespace", aafRole.getNamespace());
        assertEquals("role", aafRole.getRole());
        assertEquals("namespace.role", aafRole.getFullyQualifiedRole());
        assertNotNull(aafRole.toJSON());
    }
}
