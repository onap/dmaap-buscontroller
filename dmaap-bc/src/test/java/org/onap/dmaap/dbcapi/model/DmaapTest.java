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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DmaapTest {
	private String ver, tnr, dn, dpu, lu, bat, nk, ako;

	@Before
	public void setUp() throws Exception {
		ver = "1";
		tnr = "org.onap.dmaap";
		dn = "onap";
		dpu = "https://drps.dmaap.onap.org:8081";
		lu = "http://drps.dmaap.onap.org:8080/feedlog";
		bat = "org.onap.dcae.dmaap.MM_AGENT_TOPIC";
		nk = "foo";
		ako = "bar";
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testDmaapClassDefaultConstructor() {

		Dmaap t = new Dmaap.DmaapBuilder().createDmaap();
	
		assertTrue( t.getVersion() == null  );
		assertTrue( t.getTopicNsRoot() == null  );
		assertTrue( t.getDmaapName() == null  );
		assertTrue( t.getDrProvUrl() == null  );
		assertTrue( t.getLoggingUrl() == null  );
		assertTrue( t.getBridgeAdminTopic() == null  );
		assertTrue( t.getNodeKey() == null  );
		assertTrue( t.getAccessKeyOwner() == null  );
	
	}

	@Test
	public void testDmaapClassConstructor() {

		Dmaap t = new Dmaap.DmaapBuilder().setVer(ver).setTnr(tnr).setDn(dn).setDpu(dpu).setLu(lu).setBat(bat).setNk(nk).setAko(ako).createDmaap();
	
		assertTrue( ver.equals( t.getVersion() ));
		assertTrue( tnr.equals( t.getTopicNsRoot() ));
		assertTrue( dn.equals( t.getDmaapName() ));
		assertTrue( dpu.equals( t.getDrProvUrl() ));
		assertTrue( lu.equals( t.getLoggingUrl() ));
		assertTrue( bat.equals( t.getBridgeAdminTopic() ));
		assertTrue( nk.equals( t.getNodeKey() ));
		assertTrue( ako.equals( t.getAccessKeyOwner() ));
	
	}

	@Test
	public void testDmaapClassSetters() {

		Dmaap t = new Dmaap.DmaapBuilder().createDmaap();

		t.setVersion( ver );
		assertTrue( ver.equals( t.getVersion() ));
		t.setTopicNsRoot( tnr );
		assertTrue( tnr.equals( t.getTopicNsRoot() ));
		t.setDmaapName( dn );
		assertTrue( dn.equals( t.getDmaapName() ));
		t.setDrProvUrl( dpu );
		assertTrue( dpu.equals( t.getDrProvUrl() ));
		t.setLoggingUrl( lu );	
		assertTrue( lu.equals( t.getLoggingUrl() ));
		t.setBridgeAdminTopic( bat );
		assertTrue( bat.equals( t.getBridgeAdminTopic() ));
		t.setNodeKey( nk );
		assertTrue( nk.equals( t.getNodeKey() ));
		t.setAccessKeyOwner( ako );
		assertTrue( ako.equals( t.getAccessKeyOwner() ));
	
	}
}
