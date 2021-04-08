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

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.onap.dmaap.dbcapi.model.Dmaap;
import org.onap.dmaap.dbcapi.testframework.DmaapObjectFactory;


public class DmaapResourceTest extends JerseyTest {

	static DmaapObjectFactory factory = new DmaapObjectFactory();

	@Override
	protected Application configure() {
		return new ResourceConfig( DmaapResource.class );
		//return new ResourceConfig( HelloResource.class );
	}

	private static final String  fmt = "%24s: %s%n";



/*  may conflict with test framework! 
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
*/



	@Test
	public void GetTest() {
		Response resp = target( "dmaap").request().get( Response.class );
		assertTrue( resp.getStatus() == 200 );
	}
	@Test
	public void PostTest() {

		Dmaap dmaap = factory.genDmaap();
		Entity<Dmaap> reqEntity = Entity.entity( dmaap, MediaType.APPLICATION_JSON );
		Response resp = target( "dmaap").request().post( reqEntity, Response.class );
		System.out.println( resp.getStatus() );
		assertTrue( resp.getStatus() == 200 );
	}

	@Test
	public void PutTest() {

		Dmaap dmaap = factory.genDmaap();
		Entity<Dmaap> reqEntity = Entity.entity( dmaap, MediaType.APPLICATION_JSON );
	
		dmaap.setVersion( "2" );	
		Response resp = target( "dmaap").request().put( reqEntity, Response.class );
		System.out.println( resp.getStatus() );
		assertTrue( resp.getStatus() == 200 );
	}




}

