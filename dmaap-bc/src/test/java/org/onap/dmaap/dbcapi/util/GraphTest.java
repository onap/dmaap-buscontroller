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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.MR_Client;
import org.onap.dmaap.dbcapi.service.DcaeLocationService;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

public class GraphTest {

	ReflectionHarness rh = new ReflectionHarness();

	Graph g;

	@Before
	public void setUp() throws Exception {
		System.setProperty("ConfigFile", "src/test/resources/dmaapbc.properties");
		HashMap<String, String> hm = new HashMap<>();
		g = new Graph( hm );
	}

	@Test
	public void test1() {
		rh.reflect( "org.onap.dmaap.dbcapi.util.Graph", "get", "idNotSet@namespaceNotSet:pwdNotSet" );
	}

	@Test
	public void test3() {
		String loc = "central-onap";
		String[] actions = { "pub", "sub" };
		DcaeLocationService dls = new DcaeLocationService();
		DcaeLocation dl = new DcaeLocation( "CLLI123", "central-layer", loc, "aZone", "10.10.10.10" );
		dls.addDcaeLocation( dl );
		MR_Client mrc = new MR_Client();
		mrc.setAction( actions );
		List<MR_Client> cl = new ArrayList<MR_Client>();
		cl.add( mrc );
		cl.add( new MR_Client( loc, "aTopic", "ignore", actions ) );
		
		g = new Graph( cl, true );

		HashMap<String, String> hm = new HashMap<String, String>();

		String s = g.put( "aKey", "aVal" );
		s = g.get( "aKey" );

		s = g.getCentralLoc();		
		g.setHasCentral( true );
		g.hasCentral();

		hm = g.getGraph();

		Collection<String> k = g.getKeys();
	}

}

