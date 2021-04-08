/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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

package org.onap.dmaap.dbcapi.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.junit.Test;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ReplicationType;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

public class DBFieldHandlerTest extends BaseLoggingClass {

    private static final String fmt = "%24s: %s%n";

    ReflectionHarness rh = new ReflectionHarness();

    private static class TopicReplicationTypeHandler implements DBFieldHandler.SqlOp {
        public Object get(ResultSet rs, int index) throws Exception {
            int val = rs.getInt(index);

            return (ReplicationType.valueOf(val));
        }

        public void set(PreparedStatement ps, int index, Object val) throws Exception {
            if (val == null) {
                ps.setInt(index, 0);
                return;
            }
            @SuppressWarnings("unchecked")
            ReplicationType rep = (ReplicationType) val;
            ps.setInt(index, rep.getValue());
        }
    }

    @Test
    public void test1() {
        // rh.reflect( "org.onap.dmaap.dbcapi.aaf.client.MrTopicConnection", "get",
        // "idNotSet@namespaceNotSet:pwdNotSet" );
    }

    @Test
    public void test2() {
        String v = "Validate";
        // rh.reflect( "org.onap.dmaap.dbcapi.aaf.client.MrTopicConnection", "set", v );
    }

    @Test
    public void test3() {
        try {
            DBFieldHandler fh = new DBFieldHandler(String.class, "aString", 1);
        } catch (Exception e) {
            errorLogger.error("Error", e);
        }
    }

    @Test
    public void test4() {
        try {
            DBFieldHandler fh = new DBFieldHandler(String.class, "aString", 1, null);
        } catch (Exception e) {
            errorLogger.error("Error", e);
        }
    }

    @Test
    public void testfesc() {
        String sampleString = "@xyz,ww;,";
        String finalString = DBFieldHandler.fesc(sampleString);
        assertEquals("@axyz@cww@s@c", finalString);
    }

    @Test
    public void testfunesc() {
        String sampleString = "@axyz@cww@s@c";
        String convertedString = DBFieldHandler.funesc(sampleString);
        assertEquals("@xyz,ww;,", convertedString);
    }

    @Test
    public void testfescWithNull() {
        String sampleString1 = DBFieldHandler.fesc(null);
        String sampleString2 = DBFieldHandler.funesc(null);
        assertNull(null, sampleString1);
        assertNull(null, sampleString2);
    }
}
