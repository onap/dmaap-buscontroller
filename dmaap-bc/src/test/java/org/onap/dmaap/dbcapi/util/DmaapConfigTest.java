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
package org.onap.dmaap.dbcapi.util;

import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DmaapConfigTest {

	private static final String  fmt = "%24s: %s%n";

	ReflectionHarness rh = new ReflectionHarness();

	DmaapConfig g;


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test1() {


		rh.reflect( "org.onap.dmaap.dbcapi.util.DmaapConfig", "get", "" );	
	
	}

	@Test
	public void test2() {
		String v = "Validate";
		rh.reflect( "org.onap.dmaap.dbcapi.util.DmaapConfig", "set", v );

	}

	@Test
	public void test3() {

		String f = g.getConfigFileName();
	}



}

