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
import org.onap.dmaap.dbcapi.model.*;
import org.onap.dmaap.dbcapi.service.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.*;
import java.sql.*;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.server.ResourceConfig;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Path;
import javax.ws.rs.GET;


public class FeedResourceTest extends JerseyTest {

	@Override
	protected Application configure() {
		return new ResourceConfig( FeedResource.class );
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

/*
	@Test
	public void PutTest() {
		
		Feed feed = new Feed( "aPutTest", "1.0", "a unit test", "dgl", "unrestricted" );
		Entity<Feed> reqEntity = Entity.entity( feed, MediaType.APPLICATION_JSON );
		Response resp = target( "feeds").request().post( reqEntity, Response.class );
		System.out.println( "POST feed resp=" + resp.getStatus() );
		String postResp = resp.readEntity( String.class );
		System.out.println( "postResp=" + postResp );
		Feed rFeed = new Feed( postResp );  getting a null pointer here
		rFeed.setSuspended( true );
		String target = new String ("feeds/" + rFeed.getFeedId() );
		System.out.println( "PUT feed target=" + target );
		reqEntity = Entity.entity( rFeed, MediaType.APPLICATION_JSON );
		resp = target( target ).request().put( reqEntity, Response.class );
		System.out.println( "PUT feed resp=" + resp.getStatus() );
		assertTrue( resp.getStatus() == 200 );
	}
*/



}

