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

public class DR_NodeServiceTest {

	private static final String  fmt = "%24s: %s%n";

	ReflectionHarness rh = new ReflectionHarness();
	static DmaapObjectFactory factory = new DmaapObjectFactory();

	DR_NodeService ns;

	@Before
	public void setUp() throws Exception {
		ns = new DR_NodeService();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void test1() {


		rh.reflect( "org.onap.dmaap.dbcapi.service.DR_NodeService", "get", null );	
	
	}

	@Test
	public void test2() {
		String v = "Validate";
		rh.reflect( "org.onap.dmaap.dbcapi.service.DR_NodeService", "set", v );

	}

	@Test
	public void test3() {
		String f = "drsn01.onap.org";
		String locname = "central-demo";

		DcaeLocationService dls = new DcaeLocationService();
		DcaeLocation loc = factory.genDcaeLocation( "central" ); 
		dls.addDcaeLocation( loc );

		ApiError err = new ApiError();
		DR_Node node = new DR_Node( f, locname, "zplvm009.onap.org", "1.0.46" );
		DR_Node n2 = ns.addDr_Node( node, err );	

		if ( n2 != null ) {
			n2 = ns.getDr_Node( f,  err );
		}

		List<DR_Node> l = ns.getAllDr_Nodes();
		if ( n2 != null ) {
			n2.setVersion( "1.0.47" );
			n2 = ns.updateDr_Node( n2, err );
		}

		n2 = ns.removeDr_Node( f,  err );
				

	}


}
