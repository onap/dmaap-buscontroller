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
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.testframework.DmaapObjectFactory;


public class DcaeLocationResourceTest extends JerseyTest {

	static DmaapObjectFactory factory = new DmaapObjectFactory();

	@Override
	protected Application configure() {
		return new ResourceConfig( DcaeLocationResource.class );
	}

	@BeforeClass
	public static void setUpClass(){
		System.setProperty("ConfigFile", "src/test/resources/dmaapbc.properties");
	}

	@Test
	public void GetTest() {
		Response resp = target( "dcaeLocations").request().get( Response.class );
		System.out.println( "GET feed resp=" + resp.getStatus() );

		assertTrue( resp.getStatus() == 200 );
	}
	@Test
	public void PostTest() {
		DcaeLocation loc = factory.genDcaeLocation( "central" );
		Entity<DcaeLocation> reqEntity = Entity.entity( loc, MediaType.APPLICATION_JSON );
		Response resp = target( "dcaeLocations").request().post( reqEntity, Response.class );
		System.out.println( "POST dcaeLocation resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
		if ( resp.getStatus() != 409 ) {
			assertTrue( resp.getStatus() == 201 );
		}
		
		resp = target( "dcaeLocations").
				path( loc.getDcaeLocationName()).request().get( Response.class );
		System.out.println( "GET feed resp=" + resp.getStatus() );

		assertTrue( resp.getStatus() == 200 );
	}

	@Test
	public void PutTest() {
		DcaeLocation loc = factory.genDcaeLocation( "edge" );
		Entity<DcaeLocation> reqEntity = Entity.entity( loc, MediaType.APPLICATION_JSON );
		Response resp = target( "dcaeLocations").request().post( reqEntity, Response.class );
		System.out.println( "POST dcaeLocation resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
		if ( resp.getStatus() != 409 ) {
			assertTrue( resp.getStatus() == 201 );
		}

		
		loc.setClli("ATLCTYNJ9999");
		reqEntity = Entity.entity( loc, MediaType.APPLICATION_JSON );
		resp = target( "dcaeLocations").
				path( loc.getDcaeLocationName()).request().put( reqEntity, Response.class );
		System.out.println( "PUT dcaeLocation resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
		assertTrue( resp.getStatus() == 201 );
		
	}

	@Test
	public void DelTest() {
		DcaeLocation loc = factory.genDcaeLocation( "edge" );
		Entity<DcaeLocation> reqEntity = Entity.entity( loc, MediaType.APPLICATION_JSON );
		Response resp = target( "dcaeLocations").request().post( reqEntity, Response.class );
		System.out.println( "POST dcaeLocation resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
		if ( resp.getStatus() != 409 ) {
			assertTrue( resp.getStatus() == 201 );
		}
		
		resp = target( "dcaeLocations").
				path( loc.getDcaeLocationName()).request().delete( Response.class );
		System.out.println( "DELETE dcaeLocation resp=" + resp.getStatus() + " " + resp.readEntity( String.class ) );
		assertTrue( resp.getStatus() == 204 );
	}
}

