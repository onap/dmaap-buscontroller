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
public class DcaeLocationTest {
	String c, dl, dln, osz, s, edge;

	@Before
	public void setUp() throws Exception {
		c = "ABCDE888NJ";
		dl = "central-node";
		edge = "local-node";
		dln = "hollywood";
		osz = "california";
		s = "10.10.10.1";
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testDcaeLocationDefaultConstructor() {

		DcaeLocation t = new DcaeLocation();
	
		assertTrue( t.getClli() == null  );
		assertTrue( t.getDcaeLayer() == null  );
		assertTrue( t.getDcaeLocationName() == null  );
		assertTrue( t.getOpenStackAvailabilityZone() == null  );
		assertTrue( t.getSubnet() == null  );
	
	}

	@Test
	public void testDcaeLocationClassConstructor() {

		DcaeLocation t = new DcaeLocation( c, dl, dln, osz, s );
	
		assertTrue( c.equals( t.getClli() ));
		assertTrue( dl.equals( t.getDcaeLayer() ));
		assertTrue( dln.equals( t.getDcaeLocationName() ));
		assertTrue( osz.equals( t.getOpenStackAvailabilityZone() ));
		assertTrue( s.equals( t.getSubnet() ));
	}

	@Test
	public void testDmaapClassSetters() {

		DcaeLocation t = new DcaeLocation();

		t.setClli( c );
		assertTrue( c.equals( t.getClli() ));
		t.setDcaeLayer( dl );
		assertTrue( dl.equals( t.getDcaeLayer() ));
		assertTrue( t.isCentral() );
		t.setDcaeLayer( edge );
		assertTrue( edge.equals( t.getDcaeLayer() ));
		assertTrue( t.isLocal() );
		t.setDcaeLocationName( dln );
		assertTrue( dln.equals( t.getDcaeLocationName() ));
		t.setOpenStackAvailabilityZone( osz );
		assertTrue( osz.equals( t.getOpenStackAvailabilityZone() ));
		t.setSubnet( s );
		assertTrue( s.equals( t.getSubnet() ));
	}
}
