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
import org.onap.dmaap.dbcapi.testframework.DmaapObjectFactory;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;

public class MirrorMakerServiceTest {

	private static final String  fmt = "%24s: %s%n";
	private static DmaapObjectFactory factory = new DmaapObjectFactory();
	ReflectionHarness rh = new ReflectionHarness();

	private	MirrorMakerService mms;
	private TopicService ts;
	private MR_ClusterService mcs;
	private MR_ClientService cls;
	private DcaeLocationService dls;
	
	private Topic replicationTopic;


	DmaapService ds;
	String locname;

	@Before
	public void setUp() throws Exception {
		mms = new MirrorMakerService();
		ts = new TopicService();
		assert( ts != null );
		mcs = new MR_ClusterService();
		assert( mcs != null );
		Dmaap nd = factory.genDmaap();
		ds = new DmaapService();
		ds.addDmaap( nd );
		ts = new TopicService();
		mcs = new MR_ClusterService();
		cls = new MR_ClientService();

		dls = new DcaeLocationService();
		DcaeLocation loc = factory.genDcaeLocation( "central" );
		locname = loc.getDcaeLocationName();
		dls.addDcaeLocation( loc );
		loc = factory.genDcaeLocation( "edge");
		dls.addDcaeLocation( loc );

		ApiError err = new ApiError();
		
		MR_Cluster node = factory.genMR_Cluster( "central" );
		mcs.addMr_Cluster( node, err);
		node = factory.genMR_Cluster("edge" );
		mcs.addMr_Cluster(node,  err);


		String t = "org.onap.dmaap.bridgingTopic";
		replicationTopic = factory.genSimpleTopic(t);
		replicationTopic.setReplicationCase( ReplicationType.REPLICATION_EDGE_TO_CENTRAL );

		String c = "publisher";
		String[] a = { "sub", "view" };
		MR_Client sub = factory.genMR_Client("central",  replicationTopic.getFqtn(), c, a );
		String[] b = { "pub", "view" };
		MR_Client pub = factory.genMR_Client( "edge", replicationTopic.getFqtn(), c, b );
		ArrayList<MR_Client> clients = new ArrayList<MR_Client>();

		clients.add( sub );
		clients.add( pub );

		replicationTopic.setClients( clients );

	}

	@After
	public void tearDown() throws Exception {
	}


//	@Test
//	public void test_getters() {
//
//
//		rh.reflect( "org.onap.dmaap.dbcapi.service.MirrorMakerService", "get", null );	
//	
//	}

	@Test
	public void test_setters() {
		String v = "Validate";
		rh.reflect( "org.onap.dmaap.dbcapi.service.MirrorMakerService", "set", v );

	}

	
	
	@Test
	public void CreateMirrorMakerWithSingleTopic() {
		ApiError err = new ApiError();


		Topic nTopic = ts.addTopic(replicationTopic, err, true );

		assertTrue( err.getCode() == 200 );
		
		List<String> mma = mms.getAllMirrorMakers();
	}
	
	@Test
	public void DeleteMirrorMakerWithSingleTopic() {

		ApiError err = new ApiError();
		Topic nTopic = ts.addTopic(replicationTopic, err, true );
		replicationTopic.setTopicDescription("modified topic");
		nTopic = ts.updateTopic( replicationTopic, err );

		assertTrue( err.getCode() == 200 );

		
		List<String> mma = mms.getAllMirrorMakers();
		
		int nMM = mma.size();
		assertTrue( nMM >= 1);
		
		String name = mma.get(0);
		
		MirrorMaker mm = mms.getMirrorMaker(name);
		
		mms.delMirrorMaker(mm);
		
		mma = mms.getAllMirrorMakers();
		
		assertTrue( mma.size() == (nMM-1) );
	}
	
	@Test
	public void SplitMirrorMakerWithSingleTopic() {

		ApiError err = new ApiError();


		Topic nTopic = ts.addTopic( replicationTopic, err, true );
		replicationTopic.setTopicDescription("modified topic");
		nTopic = ts.updateTopic( replicationTopic, err );


		assertTrue( err.getCode() == 200 );
		List<String> mma = mms.getAllMirrorMakers();
		
		int nMM = mma.size();
		assertTrue( nMM >= 1);
		
		String name = mma.get(0);
		
		MirrorMaker mm = mms.getMirrorMaker(name);
		
		MirrorMaker mm2 = mms.splitMM(mm);	

	}

}
