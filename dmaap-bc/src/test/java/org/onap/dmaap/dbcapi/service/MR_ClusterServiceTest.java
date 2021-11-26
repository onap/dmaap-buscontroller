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
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.MR_Cluster;
import org.onap.dmaap.dbcapi.testframework.ReflectionHarness;

public class MR_ClusterServiceTest {

	ReflectionHarness rh = new ReflectionHarness();

	MR_ClusterService ns;

	@Before
	public void setUp() throws Exception {
		System.setProperty("ConfigFile", "src/test/resources/dmaapbc.properties");
		ns = new MR_ClusterService();
	}

	@Test
	public void test1() {
		rh.reflect( "org.onap.dmaap.dbcapi.service.MR_ClusterService", "get", null );
	}

	@Test
	public void test2() {
		String v = "Validate";
		rh.reflect( "org.onap.dmaap.dbcapi.service.MR_ClusterService", "set", v );
	}

	@Test
	public void test3() {
		String f = "mrsn01.onap.org";
		String locname = "central-demo";

		DcaeLocationService dls = new DcaeLocationService();
		DcaeLocation loc = new DcaeLocation( "CLLI1234", "some-onap", locname, "aZone", "10.10.10.0/24" );
		dls.addDcaeLocation( loc );

		ApiError err = new ApiError();
		String[] h = { "zplvm009.onap.org", "zplvm007.onap.org", "zplvm008.onap.org" };
		MR_Cluster node = new MR_Cluster( locname, f,  "http", "3904");
		MR_Cluster n2 = ns.addMr_Cluster( node, err );	

		if ( n2 != null ) {
			n2 = ns.getMr_Cluster( f,  err );
		}

		List<MR_Cluster> l = ns.getAllMr_Clusters();
		if ( n2 != null ) {
			n2 = ns.updateMr_Cluster( n2, err );
		}
		n2 = ns.removeMr_Cluster( f,  err );
	}

}
