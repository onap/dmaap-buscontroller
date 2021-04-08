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

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.MR_Client;
import org.onap.dmaap.dbcapi.model.MR_Cluster;
import org.onap.dmaap.dbcapi.model.Topic;
import org.onap.dmaap.dbcapi.testframework.DmaapObjectFactory;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

public class MR_ClientServiceTest {

	private static final String  fmt = "%24s: %s%n";
	
	private static DmaapObjectFactory factory = new DmaapObjectFactory();

	ReflectionHarness rh = new ReflectionHarness();

	private TopicService ts;
	private MR_ClusterService mcs;
	private MR_ClientService cls;
	private DcaeLocationService dls;

	private String f;
	private	String locname;

	@Before
	public void setUp() throws Exception {
		ts = new TopicService();
		mcs = new MR_ClusterService();
		cls = new MR_ClientService();
		f = "mrsn01.onap.org";
		locname = "central-demo";

		dls = new DcaeLocationService();
		DcaeLocation loc = factory.genDcaeLocation( "central" );
		dls.addDcaeLocation( loc );

		ApiError err = new ApiError();
		String[] h = { "zplvm009.onap.org", "zplvm007.onap.org", "zplvm008.onap.org" };
		MR_Cluster node = factory.genMR_Cluster( "central" );
		MR_Cluster n2 = mcs.addMr_Cluster( node, err );	
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test1() {


		rh.reflect( "org.onap.dmaap.dbcapi.service.MR_ClientService", "get", null );	
	
	}

	@Test
	public void test2() {
		String v = "Validate";
		rh.reflect( "org.onap.dmaap.dbcapi.service.MR_ClientService", "set", v );

	}

	@Test
	public void test3() {
		Topic topic = factory.genSimpleTopic( "test3" );
		ApiError err = new ApiError();
		Topic nTopic = ts.addTopic( topic, err, false );
		if ( nTopic != null ) {
			assertTrue( nTopic.getTopicName().equals( topic.getTopicName() ));
		}

		MR_Client c = factory.genPublisher( "edge",  topic.getFqtn() );

		c = cls.addMr_Client( c, topic, err );

	}

	@Test
	public void test4() {
		List<MR_Client> l = cls.getAllMr_Clients();

		List<MR_Client> al = cls.getAllMrClients( "foo" );

		List<MR_Client> al2 = cls.getClientsByLocation( "central" );
	}

	@Test
	public void AddSubscriberToTopic() {
		Topic topic = factory.genSimpleTopic( "test5" );
		ApiError err = new ApiError();
		Topic nTopic = ts.addTopic( topic, err, false );
		if ( nTopic != null ) {
			assertTrue( nTopic.getTopicName().equals( topic.getTopicName() ));
		}
		MR_Client c = factory.genPublisher( "central", topic.getFqtn() );

		c = cls.addMr_Client( c, topic, err );
		assertTrue( c != null );

		c = factory.genSubscriber( "central", topic.getFqtn() );
		c = cls.addMr_Client( c, topic, err );
		assertTrue( err.getCode() == 200 );

		
	}
	
}
