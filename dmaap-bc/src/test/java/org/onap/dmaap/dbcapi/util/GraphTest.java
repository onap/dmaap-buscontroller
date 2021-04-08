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

import org.onap.dmaap.dbcapi.model.*;
import org.onap.dmaap.dbcapi.service.*;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

public class GraphTest {

	private static final String  fmt = "%24s: %s%n";

	ReflectionHarness rh = new ReflectionHarness();

	Graph g;


	@Before
	public void setUp() throws Exception {
		HashMap<String, String> hm = new HashMap<String,String>();
		g = new Graph( hm );
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test1() {


		rh.reflect( "org.onap.dmaap.dbcapi.util.Graph", "get", "idNotSet@namespaceNotSet:pwdNotSet" );	
	
	}

	@Test
	public void test2() {
		String v = "Validate";
		//rh.reflect( "org.onap.dmaap.dbcapi.util.Graph", "set", v );

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

