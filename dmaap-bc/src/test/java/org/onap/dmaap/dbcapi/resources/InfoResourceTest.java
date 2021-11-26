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
package org.onap.dmaap.dbcapi.resources;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;


public class InfoResourceTest extends JerseyTest {

	@Override
	protected Application configure() {
		return new ResourceConfig( InfoResource.class );
	}
	@BeforeClass
	public static void setUpClass(){
		System.setProperty("ConfigFile", "src/test/resources/dmaapbc.properties");
	}

	@Test
	public void GetTest() {
		Response resp = target( "info").request().get( Response.class );
		assertTrue( resp.getStatus() == 204 );
	}

}

