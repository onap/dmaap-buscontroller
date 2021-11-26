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
package org.onap.dmaap.dbcapi.server;

import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

public class JettyServerTest {

	ReflectionHarness rh = new ReflectionHarness();
	JettyServer m = null;

	@Before
	public void setUp() throws Exception {
		System.setProperty("ConfigFile", "src/test/resources/dmaapbc.properties");
		Properties p = DmaapConfig.getConfig();
		try {
			m = new JettyServer(p);
		} catch (Exception e ) {
		}
	}

	@After
	public void tearDown() throws Exception {
		try {
			m.getServer().stop();
		} catch (Exception e ) {
		}
	}

	@Test
	public void test1() {
		rh.reflect( "org.onap.dmaap.dbcapi.server.JettyServer", "get", null );
	}

	@Test
	public void test2() {
		String v = "Validate";
		rh.reflect( "org.onap.dmaap.dbcapi.server.JettyServer", "set", v );
	}

}
