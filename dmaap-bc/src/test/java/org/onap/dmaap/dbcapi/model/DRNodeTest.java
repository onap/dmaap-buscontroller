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
public class DRNodeTest {
	String f, d, h, v;

	@Before
	public void setUp() throws Exception {
		v = "1";
		f = "node01.onap.org";
		h = "zlpdrns01.cloud.onap.org";
		d = "central-onap";
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testDRNodeClassDefaultConstructor() {

		DR_Node t = new DR_Node();
	
		assertTrue( t.getFqdn() == null  );
		assertTrue( t.getDcaeLocationName() == null  );
		assertTrue( t.getHostName() == null  );
		assertTrue( t.getVersion() == null  );
	
	}

	@Test
	public void testDRNodeClassConstructor() {

		DR_Node t = new DR_Node( f, d, h, v );
	
		assertTrue( f.equals( t.getFqdn() ));
		assertTrue( d.equals( t.getDcaeLocationName() ));
		assertTrue( h.equals( t.getHostName() ));
		assertTrue( v.equals( t.getVersion() ));
	
	}

	@Test
	public void testDRNodeClassSetters() {

		DR_Node t = new DR_Node();

		t.setFqdn( f );
		assertTrue( f.equals( t.getFqdn() ));
		t.setDcaeLocationName( d );
		assertTrue( d.equals( t.getDcaeLocationName() ));
		t.setHostName( h );
		assertTrue( h.equals( t.getHostName() ));
		t.setVersion( v );
		assertTrue( v.equals( t.getVersion() ));
	
	}
}
