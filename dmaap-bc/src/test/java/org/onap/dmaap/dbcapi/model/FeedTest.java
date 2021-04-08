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


public class FeedTest {

	private static final String  fmt = "%24s: %s%n";

	ReflectionHarness rh = new ReflectionHarness();

	String n, v, d, o, a;

	@Before
	public void setUp() throws Exception {
		System.setProperty("ConfigFile", "src/test/resources/dmaapbc.properties");
		n = "Chicken Feed";
		v = "1.0";
		d = "A daily helping of chicken eating metrics";
		o = "ab123";
		a = "Unrestricted";
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test1() {


		rh.reflect( "org.onap.dmaap.dbcapi.model.Feed", "get", null );	
	
	}

	@Test
	public void test2() {
		Feed t = new Feed( n, v, d, o, a );

		ArrayList<DR_Sub> subs = new ArrayList<DR_Sub>();
		DR_Sub sub = new DR_Sub( "central", "user", "pwd", "22", "server.onap.org/deliv", "log.onap.org/logs", true );
		subs.add( sub );
		t.setSubs( subs );

		assertTrue( n.equals( t.getFeedName() ));
		assertTrue( v.equals( t.getFeedVersion() ));
		assertTrue( d.equals( t.getFeedDescription() ));
		assertTrue( o.equals( t.getOwner() ));
		assertTrue( a.equals( t.getAsprClassification() ) );
		assertTrue( ! t.isSuspended() );
	}

	@Test
	public void test3() {

		String v = "Validate";
		rh.reflect( "org.onap.dmaap.dbcapi.model.Feed", "set", v );
	}

	@Test
	public void test4() {
		String s = String.format( "{ \"%s\": \"%s\", \"%s\": \"%s\", \"%s\": \"%s\", \"%s\": \"%s\", \"%s\": false, \"%s\": { \"%s\": \"%s\", \"%s\": \"%s\", \"%s\": \"%s\" }, \"%s\": { \"%s\": \"%s\", \"%s\": [ { \"%s\": \"%s\", \"%s\": \"%s\" } ] } }",
				"name", n,
				"version", v,
				"description", d,
				"publisher", a,
				"suspend", 
				"links",
					"publish", "https://feed.onap.org/publish/22",
					"subscribe" , Feed.getSubProvURL( "22" ),
					"log" , "https://feed.onap.org/log/22",
				"authorization",
					"classification", a,
					"endpoint_ids" , "id", "king", "password", "henry" );


		Feed t = new Feed( s );

		assertTrue( n.equals( t.getFeedName() ));
		assertTrue( v.equals( t.getFeedVersion() ));
		assertTrue( d.equals( t.getFeedDescription() ));
		assertTrue( a.equals( t.getAsprClassification() ) );
		assertTrue( ! t.isSuspended() );

		String o = t.toString();

	}

}
