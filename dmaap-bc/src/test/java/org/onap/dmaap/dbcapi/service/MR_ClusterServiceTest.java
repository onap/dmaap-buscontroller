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

public class MR_ClusterServiceTest {

	private static final String  fmt = "%24s: %s%n";

	ReflectionHarness rh = new ReflectionHarness();

	MR_ClusterService ns;

	@Before
	public void setUp() throws Exception {
		ns = new MR_ClusterService();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test1() {


		rh.reflect( "org.onap.dmaap.dbcapi.service.MR_ClusterService", "get", null );	
	
	}

	@Test
	public void test2() {
		String v = "Validate";
		rh.reflect( "org.onap.dmaap.dbcapi.service.MR_ClusterService", "set", v );

	}

	@Test
	public void test3() {
		String f = "mrsn01.onap.org";
		String locname = "central-demo";

		DcaeLocationService dls = new DcaeLocationService();
		DcaeLocation loc = new DcaeLocation( "CLLI1234", "some-onap", locname, "aZone", "10.10.10.0/24" );
		dls.addDcaeLocation( loc );

		ApiError err = new ApiError();
		String[] h = { "zplvm009.onap.org", "zplvm007.onap.org", "zplvm008.onap.org" };
		MR_Cluster node = new MR_Cluster( locname, f,  "http", "3904");
		MR_Cluster n2 = ns.addMr_Cluster( node, err );	

		if ( n2 != null ) {
			n2 = ns.getMr_Cluster( f,  err );
		}

		List<MR_Cluster> l = ns.getAllMr_Clusters();
		if ( n2 != null ) {
			n2 = ns.updateMr_Cluster( n2, err );
		}

		n2 = ns.removeMr_Cluster( f,  err );
				

	}

/*
	@Test
	public void test4() {
		List<MR_Client> l = cls.getAllMr_Clients();

		ArrayList<MR_Client> al = cls.getAllMrClients( "foo" );

		ArrayList<MR_Client> al2 = cls.getClientsByLocation( "central" );
	}

	@Test
	public void test5() {
		Topic topic = new Topic();
		ApiError err = new ApiError();
		topic.setTopicName( "test3" );
		topic.setFqtnStyle( FqtnType.Validator("none") );
		topic.getFqtn();
		Topic nTopic = ts.addTopic( topic, err );
		if ( nTopic != null ) {
			assertTrue( nTopic.getTopicName().equals( topic.getTopicName() ));
		}
		String[] actions = { "pub", "view" };
		MR_Client c = new MR_Client( "central-onap", "org.onap.dmaap.demo.interestingTopic2", "org.onap.clientApp.publisher", actions );

		c = cls.addMr_Client( c, topic, err );
		if ( c != null ) {
				actions[0] = "sub";
				c.setAction( actions );
				c = cls.updateMr_Client( c, err );
				assertTrue( err.getCode() == 200 );
		}
	}
*/

}
