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
package org.onap.dmaap.dbcapi.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

import java.util.ArrayList;


public class MirrorMakerTest {

	private static final String  fmt = "%24s: %s%n";

	ReflectionHarness rh = new ReflectionHarness();


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test1() {


		rh.reflect( "org.onap.dmaap.dbcapi.model.MirrorMaker", "get", null );	
	
	}
	@Test
	public void test2() {

		String v = "Validate";
		rh.reflect( "org.onap.dmaap.dbcapi.model.MirrorMaker", "set", v );
	}

	@Test
	public void test3() {
		String f = "org.onap.interestingTopic";
		String c1 =  "cluster1.onap.org";
		String c2 =  "cluster2.onap.org";
		MirrorMaker t = new MirrorMaker( c1, c2 );
		String m = t.getMmName();

		MirrorMaker.genKey( c1, c2 );

		assertTrue( c1.equals( t.getSourceCluster() ));
		assertTrue( c2.equals( t.getTargetCluster() ));
	}


	@Test
	public void test4() {
		String f = "org.onap.interestingTopic";
		String c1 =  "cluster1.onap.org";
		String c2 =  "cluster2.onap.org";
		String p1 = "9092";
		String p2 = "2081";
		MirrorMaker t = new MirrorMaker( c1, c2 );
		String m = t.getMmName();
	

		ArrayList<String> topics = new ArrayList<String>();
		topics.add( f );
		t.setTopics( topics );
		t.addTopic( "org.onap.topic2" );

		int i = t.getTopicCount();

		String s = t.getWhitelistUpdateJSON();

		s = t.createMirrorMaker(p1, p2);

	}

}
