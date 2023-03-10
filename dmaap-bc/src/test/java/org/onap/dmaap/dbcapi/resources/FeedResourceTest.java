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

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.dmaap.dbcapi.model.Feed;


public class FeedResourceTest extends JerseyTest {

	@BeforeClass
	public static void setUpClass(){
		System.setProperty("ConfigFile", "src/test/resources/dmaapbc.properties");
	}
	@Override
	protected Application configure() {
		return new ResourceConfig( FeedResource.class );
	}

	@Test
	public void GetTest() {
		Response resp = target( "feeds").request().get( Response.class );
		System.out.println( "GET feed resp=" + resp.getStatus() );

		assertTrue( resp.getStatus() == 200 );
	}
	@Test
	public void PostTest() {
		Feed feed = new Feed( "aPostTest", "1.0", "a unit test", "dgl", "unrestricted" );
		Entity<Feed> reqEntity = Entity.entity( feed, MediaType.APPLICATION_JSON );
		Response resp = target( "feeds").request().post( reqEntity, Response.class );
		System.out.println( "POST feed resp=" + resp.getStatus() );
		assertTrue( resp.getStatus() == 200 );
	}

}

