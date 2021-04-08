/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2019 IBM Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (c) 2019 IBM
 * ===================================================================
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class AafUserRoleTest {

    AafUserRole aafUserRole;

    @Before
    public void setUp() {
        aafUserRole = new AafUserRole("xyz", "admin");
    }

    @Test
    public void testGetIdentity() {
        aafUserRole.setIdentity("xyz");
        assertEquals("xyz", aafUserRole.getIdentity());
    }

    @Test
    public void testGetRole() {
        aafUserRole.setRole("admin");
        assertEquals("admin", aafUserRole.getRole());
    }

    @Test
    public void toJSON() {
        AafUserRole role = new AafUserRole("test", "admin");
        assertThat(role.toJSON(), is(" { \"user\": \"test\", \"role\": \"admin\" }"));
    }
}