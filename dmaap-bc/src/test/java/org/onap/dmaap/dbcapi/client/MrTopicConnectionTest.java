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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.MR_Cluster;
import org.onap.dmaap.dbcapi.service.DcaeLocationService;
import org.onap.dmaap.dbcapi.service.MR_ClusterService;
import org.onap.dmaap.dbcapi.service.TopicService;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

public class MrTopicConnectionTest {

	private static final String  fmt = "%24s: %s%n";

	ReflectionHarness rh = new ReflectionHarness();

	MrTopicConnection ns;
	MR_ClusterService mcs;
	TopicService ts;

	@Before
	public void setUp() throws Exception {
		System.setProperty("ConfigFile", "src/test/resources/dmaapbc.properties");
		ns = new MrTopicConnection( "aUser", "aPwd" );
		ts = new TopicService();
		mcs = new MR_ClusterService();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test1() {


		rh.reflect( "org.onap.dmaap.dbcapi.client.MrTopicConnection", "get", "idNotSet@namespaceNotSet:pwdNotSet" );
	
	}

	@Test
	public void test2() {
		String v = "Validate";
		rh.reflect( "org.onap.dmaap.dbcapi.client.MrTopicConnection", "set", v );

	}

	@Test
	public void test3() {
		String locname = "central-demo";

		DcaeLocationService dls = new DcaeLocationService();
		DcaeLocation loc = new DcaeLocation( "CLLI1234", "central-onap", locname, "aZone", "10.10.10.0/24" );
		dls.addDcaeLocation( loc );

		ApiError err = new ApiError();
		
		MR_Cluster cluster = new MR_Cluster( locname, "localhost", "http", "3904");
		mcs.addMr_Cluster( cluster, err );
		ns.makeTopicConnection( cluster, "org.onap.dmaap.anInterestingTopic", "" );
		String msg = "{ 'key': '1234', 'val': 'hello world' }";
		ApiError e2 = ns.doPostMessage( msg );

		try {
			InputStream is = new FileInputStream(new File("/src/test/resources/dmaapbc.properties"));
			String body = ns.bodyToString( is );
		} catch ( FileNotFoundException fnfe ) {
		}

	}



}

