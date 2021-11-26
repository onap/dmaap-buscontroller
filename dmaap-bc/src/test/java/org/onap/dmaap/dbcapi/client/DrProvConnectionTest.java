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
package org.onap.dmaap.dbcapi.client;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DR_Pub;
import org.onap.dmaap.dbcapi.model.DR_Sub;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.Feed;
import org.onap.dmaap.dbcapi.service.DcaeLocationService;
import org.onap.dmaap.dbcapi.service.MR_ClusterService;
import org.onap.dmaap.dbcapi.service.TopicService;
import org.onap.dmaap.dbcapi.testframework.DmaapObjectFactory;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

public class DrProvConnectionTest {

	private static final String  fmt = "%24s: %s%n";
	private static DmaapObjectFactory factory = new DmaapObjectFactory();

	ReflectionHarness rh = new ReflectionHarness();

	DrProvConnection ns;
	MR_ClusterService mcs;
	TopicService ts;

	@Before
	public void setUp() throws Exception {
		System.setProperty("ConfigFile", "src/test/resources/dmaapbc.properties");
		ns = new DrProvConnection();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test1() {
		rh.reflect( "org.onap.dmaap.dbcapi.client.DrProvConnection", "get", "idNotSet@namespaceNotSet:pwdNotSet" );
	}

	@Test
	public void test2() {
		String v = "Validate";
		rh.reflect( "org.onap.dmaap.dbcapi.client.DrProvConnection", "set", v );

	}

	@Test
	public void test3() {
		String locname = "central-demo";

		DcaeLocationService dls = new DcaeLocationService();
		DcaeLocation loc = factory.genDcaeLocation( "central" );
		dls.addDcaeLocation( loc );

		ApiError err = new ApiError();
		String[] hl = { "host1", "host2", "host3" };
		ns.makeFeedConnection( );
		ns.makeFeedConnection( "01" );
		ns.makeSubPostConnection( "part0/part1/part2/part3/part4" );
		ns.makeSubPutConnection( "44" );
		ns.makeIngressConnection( "01", "aUser", "10.10.10.10", "aNode" );
		ns.makeEgressConnection( "01", "aNode" );
		ns.makeNodesConnection( "someVar" );
		Feed feed = new Feed( "dgl feed 1" ,
                                "v1.0",
                                "dgl feed 1 for testing",
                                "TEST",
                                "unclassified"
                    );
            ArrayList<DR_Pub> pubs = new ArrayList<DR_Pub>();
            pubs.add( new DR_Pub( "central-demo" ) );
            feed.setPubs(pubs);

		String resp = ns.doPostFeed( feed, err );
		resp = ns.doPutFeed( feed, err );
		resp = ns.doDeleteFeed( feed, err );

		int i = ns.doXgressPost( err );

		DR_Sub sub = factory.genDrSub( "central", feed.getFeedId() );
		assertTrue( sub != null );
		String sr = ns.doPostDr_Sub( sub, err );
		/*
		 * TODO:
			- create a new DR_Sub based on a simulated response
			- update using ns.doPutDr_Sub( sub, err );
		 */
	}

	@Test
	public void test4() {
		ApiError err = new ApiError();
		String resp = ns.doGetNodes( err );
		ns.makeNodesConnection( "someVar", "host1|host2" );
		resp = ns.doPutNodes( err );
		try {
			InputStream is = new FileInputStream(new File("/src/test/resources/dmaapbc.properties"));
			String body = ns.bodyToString( is );
		} catch ( FileNotFoundException fnfe ) {
		}
	}

}

