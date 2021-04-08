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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
public class DRPubTest {
	String d, un, up, f, p;

	@Before
	public void setUp() throws Exception {
		d = "central-onap";
		un = "user1";
		up = "secretW0rd";
		f = "234";
		p = "678";
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testDRPubClassDefaultConstructor() {

		DR_Pub t = new DR_Pub();
	
		assertTrue( t.getDcaeLocationName() == null  );
		assertTrue( t.getUsername() == null  );
		assertTrue( t.getUserpwd() == null  );
		assertTrue( t.getFeedId() == null  );
		assertTrue( t.getPubId() == null  );
	
	}

	@Test
	public void testDRPubClassConstructor() {

		DR_Pub t = new DR_Pub( d, un, up, f, p );
	
		assertTrue( d.equals( t.getDcaeLocationName() ));
		assertTrue( un.equals( t.getUsername() ));
		assertTrue( up.equals( t.getUserpwd() ));
		assertTrue( f.equals( t.getFeedId() ));
		assertTrue( p.equals( t.getPubId() ));
	}

	@Test
	public void testDRPubClassSetters() {

		DR_Pub t = new DR_Pub();

		t.setDcaeLocationName( d );
		assertTrue( d.equals( t.getDcaeLocationName() ));
		t.setUsername( un );
		assertTrue( un.equals( t.getUsername() ));
		t.setUserpwd( up );
		assertTrue( up.equals( t.getUserpwd() ));
		t.setFeedId( f );
		assertTrue( f.equals( t.getFeedId() ));
		t.setPubId( p );
		assertTrue( p.equals( t.getPubId() ));
	
	}
}
