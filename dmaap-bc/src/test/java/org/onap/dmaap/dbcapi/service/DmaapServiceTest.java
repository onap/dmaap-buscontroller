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
package org.onap.dmaap.dbcapi.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dbcapi.model.Dmaap;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

public class DmaapServiceTest {

	private static final String  fmt = "%24s: %s%n";

	ReflectionHarness rh = new ReflectionHarness();

	DmaapService ds;

	@Before
	public void setUp() throws Exception {
		ds = new DmaapService();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test1() {


		//rh.reflect( "org.onap.dmaap.dbcapi.service.DmaapService", "get", null );	
	
	}

	@Test
	public void test2() {
		String v = "Validate";
		rh.reflect( "org.onap.dmaap.dbcapi.service.DmaapService", "set", v );

	}

	@Test
	public void test3() {
		Dmaap nd = new Dmaap.DmaapBuilder().setVer("1").setTnr("org.onap.dmaap").setDn("onap-demo").setDpu("drps.demo.onap.org").setLu("").setBat("MMAGENT_TOPIC").setNk("").setAko("").createDmaap();
		ds.addDmaap( nd );
	}

	@Test
	public void test4() {
		Dmaap d = ds.getDmaap();

	}

	@Test
	public void test5() {
		Dmaap nd = new Dmaap.DmaapBuilder().setVer("2").setTnr("org.onap.dmaap").setDn("onap-demo").setDpu("drps.demo.onap.org").setLu("").setBat("MMAGENT_TOPIC").setNk("").setAko("").createDmaap();
		ds.updateDmaap( nd );

	}

	@Test
	public void test6() {
		String t = ds.getTopicPerm();
		String t2 = ds.getTopicPerm( "val2" );
		String t3 = ds.getBridgeAdminFqtn();

		boolean b = ds.testCreateMmaTopic();

	}

}
