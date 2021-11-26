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

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;
public class MR_ClusterTest {
	String d, fqdn, repGrp, p1, p2, prot, p0;

	ReflectionHarness rh = new ReflectionHarness();

	@Before
	public void setUp() throws Exception {
		System.setProperty("ConfigFile", "src/test/resources/dmaapbc.properties");
		d = "central-onap";
		fqdn = "mr.onap.org";
		repGrp = "zeppelin";
		prot = "http";
		p0 = "3904";
		p1 = "9092";
		p2 = "2323";
		
	
		
	}

	@Test
	public void testMR_ClusterClassDefaultConstructor() {
		MR_Cluster t = new MR_Cluster();
	
		assertTrue( t.getDcaeLocationName() == null  );
		assertTrue( t.getFqdn() == null  );
	
	}

	@Test
	public void testMR_ClusterClassConstructor() {

		MR_Cluster t = new MR_Cluster( d, fqdn, prot, p0);
	
		assertTrue( t.getDcaeLocationName() == d  );
		assertTrue( t.getFqdn() == fqdn  );
		assertTrue( t.getTopicProtocol() == prot );
		assertTrue( t.getTopicPort() == p0 );
		
		// pass null params to trigger default settings
		 t = new MR_Cluster( d, fqdn, null, null );
		
		assertTrue( t.getDcaeLocationName() == d  );
		assertTrue( t.getFqdn() == fqdn  );
		assertTrue( t.getTopicProtocol() != null );
		assertTrue( t.getTopicPort() != null );
	}
	
	@Test
	public void testMR_ClusterManyArgsClassConstructor() {

		MR_Cluster t = new MR_Cluster( d, fqdn, prot, p0, repGrp, p1, p2 );
	
		assertTrue( t.getDcaeLocationName() == d  );
		assertTrue( t.getFqdn() == fqdn  );
		assertTrue( t.getTopicProtocol() == prot );
		assertTrue( t.getTopicPort() == p0 );
		assertTrue( t.getReplicationGroup() == repGrp  );
		assertTrue( t.getSourceReplicationPort() == p1  );
		assertTrue( t.getTargetReplicationPort() == p2 );
		
		// pass null params to trigger default settings
		t = new MR_Cluster( d, fqdn, null, null, null, null, null );
		
		assertTrue( t.getDcaeLocationName() == d  );
		assertTrue( t.getFqdn() == fqdn  );
		assertTrue( t.getTopicProtocol() != null );
		assertTrue( t.getTopicPort() != null );
		assertTrue( t.getReplicationGroup() != null  );
		assertTrue( t.getSourceReplicationPort() != null  );
		assertTrue( t.getTargetReplicationPort() != null );
	}

	@Test
	public void testw3() {

		MR_Cluster t = new MR_Cluster();
	
		assertTrue( t.getDcaeLocationName() == null  );
		assertTrue( t.getFqdn() == null  );

		String override = "cluster2.onap.org";
		String	topic2 = "org.onap.topic2";
		String fqtn = t.genTopicURL( override, topic2 );	
		assertTrue( fqtn.contains( override) && fqtn.contains(topic2));
		
		fqtn = t.genTopicURL( null, "org.onap.topic2" );
		assertTrue(fqtn.contains(topic2));
	}

	@Test
	public void testsetter() {
		String v = "validate";
		rh.reflect( "org.onap.dmaap.dbcapi.model.MR_Cluster", "set", v );
	}

}
