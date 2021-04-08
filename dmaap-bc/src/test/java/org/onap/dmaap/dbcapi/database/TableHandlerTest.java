/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.dmaap.dbcapi.database;

import org.onap.dmaap.dbcapi.model.*;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.*;

public class TableHandlerTest {

	private static final String  fmt = "%24s: %s%n";

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



	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test1() {


		rh.reflect( "org.onap.dmaap.dbcapi.aaf.client.MrTopicConnection", "get", "idNotSet@namespaceNotSet:pwdNotSet" );	
	
	}

	@Test
	public void test2() {
		String v = "Validate";
		//rh.reflect( "org.onap.dmaap.dbcapi.aaf.client.MrTopicConnection", "set", v );

	}

	@Test
	public void test3() {
		DBFieldHandler.SqlOp trth = new TopicReplicationTypeHandler();
		TableHandler.setSpecialCase("topic", "replication_case", trth);

		try {
		ConnectionFactory cf = new ConnectionFactory();
		TableHandler th = new TableHandler( cf, TopicReplicationTypeHandler.class, "foo", "bar" );
		DBFieldHandler.SqlOp t = th.getSpecialCase( "foo", "bar" );
		assert( trth == t );
		} catch (Exception e ) {
		}
		try {

		TableHandler th = new TableHandler( TopicReplicationTypeHandler.class, "foo", "bar" );
		DBFieldHandler.SqlOp t = th.getSpecialCase( "foo", "bar" );
		assert( trth == t );
		} catch (Exception e ) {
		}

	}



}

