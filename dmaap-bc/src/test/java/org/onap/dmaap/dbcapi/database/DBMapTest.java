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
import org.onap.dmaap.dbcapi.util.Singleton;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

public class DBMapTest {

	private static final String  fmt = "%24s: %s%n";

	ReflectionHarness rh = new ReflectionHarness();


    private static Singleton<Dmaap> dmaap;
    private static Map<String, DcaeLocation> dcaeLocations;


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test1() {


		//rh.reflect( "org.onap.dmaap.dbcapi.aaf.client.MrTopicConnection", "get", "idNotSet@namespaceNotSet:pwdNotSet" );	
	
	}

	@Test
	public void test2() {
		String v = "Validate";
		//rh.reflect( "org.onap.dmaap.dbcapi.aaf.client.MrTopicConnection", "set", v );

	}

	@Test
	public void test3() {
		try {
                dmaap = new DBSingleton<Dmaap>(Dmaap.class, "dmaap");
				Dmaap nd = new Dmaap.DmaapBuilder().createDmaap();
				dmaap.update(nd);
		} catch (Exception e ) {
		}
		try {
                dcaeLocations = new DBMap<DcaeLocation>(DcaeLocation.class, "dcae_location", "dcae_location_name");
		} catch (Exception e ) {
		}

	}



}

