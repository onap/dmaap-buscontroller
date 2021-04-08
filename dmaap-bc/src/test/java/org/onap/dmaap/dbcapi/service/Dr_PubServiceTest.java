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

import  org.onap.dmaap.dbcapi.model.*;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;

public class Dr_PubServiceTest {

	private static final String  fmt = "%24s: %s%n";

	ReflectionHarness rh = new ReflectionHarness();

	DR_PubService ns;
	FeedService fs;

	@Before
	public void setUp() throws Exception {
		ns = new DR_PubService();
		fs = new FeedService();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test1() {


		rh.reflect( "org.onap.dmaap.dbcapi.service.DR_PubService", "get", null );	
	
	}

	@Test
	public void test2() {
		String v = "Validate";
		rh.reflect( "org.onap.dmaap.dbcapi.service.DR_PubService", "set", v );

	}

	@Test
	public void test3() {
		String locname = "central-demo";

		DcaeLocationService dls = new DcaeLocationService();
		DcaeLocation loc = new DcaeLocation( "CLLI1234", "central-onap", locname, "aZone", "10.10.10.0/24" );
		dls.addDcaeLocation( loc );

		ApiError err = new ApiError();
		Feed f = new Feed( "aTest", "1.0", "a unit test", "dgl", "unrestricted" );
		f = fs.addFeed( f, 	err );

		assertTrue( f != null );
		DR_Pub node = new DR_Pub( locname, "aUser", "aPwd", f.getFeedId(), "pubId01" );
		DR_Pub n2 = ns.addDr_Pub( node );	
		DR_Pub node2 = new DR_Pub( locname, "aUser", "aPwd", f.getFeedId() );
		n2 = ns.addDr_Pub( node2 );	

		if ( n2 != null ) {
			n2 = ns.getDr_Pub( n2.getPubId(),  err );
		}

		List<DR_Pub> l = ns.getAllDr_Pubs();
		if ( n2 != null ) {
			n2 = ns.updateDr_Pub( n2 );
		}

		n2 = ns.removeDr_Pub( n2.getPubId(),  err );
			

	}

	@Test
	public void test4() {
		ArrayList<DR_Pub> l = ns.getDr_PubsByFeedId( "1" );


	}


}
