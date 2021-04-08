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

import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.Feed;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

public class FeedServiceTest {

	private static final String  fmt = "%24s: %s%n";

	ReflectionHarness rh = new ReflectionHarness();

	FeedService ds;

	@Before
	public void setUp() throws Exception {
		ds = new FeedService();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test1() {


		rh.reflect( "org.onap.dmaap.dbcapi.service.FeedService", "get", null );	
	
	}

	@Test
	public void test2() {
		String v = "Validate";
		rh.reflect( "org.onap.dmaap.dbcapi.service.FeedService", "set", v );

	}

	@Test
	public void test3() {
		ApiError err = new ApiError();

		Feed f = new Feed( "aTest", "1.0", "a unit test", "dgl", "unrestricted" );
		f = ds.addFeed( f, 	err );
		System.out.println( "f=" + f );

		ds.updateFeed( f, err );

		ds.removeFeed( f, err );
	}

	@Test
	public void test4() {
		ApiError err = new ApiError();
		Feed f = ds.getFeed( "aName", err );

		f = ds.getFeedByName( "aName", "1.0", err );

		f = ds.getFeedPure( "aName", err );
	}

	@Test
	public void test5() {
		List<Feed> f = ds.getAllFeeds( "aName", "1.0", "startsWith" );

	}

	
	@Test 
	public void syncTestHard() {
		ApiError err = new ApiError();
		ds.sync(  true, err );
		
		assert( 200 == err.getCode());
	}


}
