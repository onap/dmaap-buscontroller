/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2019 Nokia Intellectual Property. All rights reserved.
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

import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status;
import org.onap.dmaap.dbcapi.model.MR_Cluster;
import org.onap.dmaap.dbcapi.testframework.DmaapObjectFactory;

public class MR_ClusterResourceTest {

	private static final DmaapObjectFactory DMAAP_OBJECT_FACTORY = new DmaapObjectFactory();
	private static FastJerseyTestContainer testContainer;
	private static final String MR_CLUSTERS_TARGET = "mr_clusters";

	@BeforeClass
	public static void setUpClass() throws Exception {
		DatabaseClass.getDmaap().init(DMAAP_OBJECT_FACTORY.genDmaap());

		testContainer = new FastJerseyTestContainer(new ResourceConfig()
			.register(MR_ClusterResource.class).register(DcaeLocationResource.class));
		testContainer.init();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		testContainer.destroy();
        /*TODO: Cannot cleanup yet until still other Resources tests depends on the static DB content

        DatabaseClass.getDmaap().remove();
        DatabaseClass.clearDatabase();*/
	}

	@Before
	public void setUpClusterAndLocation() {
		DatabaseClass.clearDatabase();
	}

	@Test
	public void getMrClusters_shouldReturnEmptyList_whenNoMrClustersInDataBase() {
		//when
		Response resp = testContainer.target(MR_CLUSTERS_TARGET).request().get(Response.class);

		//then
		assertEquals(HttpStatus.OK_200, resp.getStatus());
		assertTrue(resp.hasEntity());

		List<MR_Cluster> mrClusters = resp.readEntity(new GenericType<List<MR_Cluster>>() {
		});
		assertTrue(mrClusters.isEmpty());
	}

	@Test
	public void addMrCluster_shouldReturnValidationError_whenDcaeLocationNameNotProvided() {
		//given
		Entity<MR_Cluster> requestEntity = entity(new MR_Cluster(), APPLICATION_JSON);

		//when
		Response resp = testContainer.target(MR_CLUSTERS_TARGET).request().post(requestEntity, Response.class);

		//then
		assertEquals(HttpStatus.BAD_REQUEST_400, resp.getStatus());
		assertTrue(resp.hasEntity());
		ApiError errorObj = resp.readEntity(ApiError.class);
		assertEquals("dcaeLocationName", errorObj.getFields());
	}

	@Test
	public void addMrCluster_shouldReturnValidationError_whenFqdnNotProvided() {
		//given
		MR_Cluster mr_cluster = new MR_Cluster();
		mr_cluster.setDcaeLocationName("central-cloud");
		Entity<MR_Cluster> requestEntity = entity(mr_cluster, APPLICATION_JSON);

		//when
		Response resp = testContainer.target(MR_CLUSTERS_TARGET).request().post(requestEntity, Response.class);

		//then
		assertEquals(HttpStatus.BAD_REQUEST_400, resp.getStatus());
		assertTrue(resp.hasEntity());
		ApiError errorObj = resp.readEntity(ApiError.class);
		assertEquals("fqdn", errorObj.getFields());
	}

	@Test
	public void addMrCluster_shouldAddMrClusterToDatabase() {
		//given
		MR_Cluster mrCluster = DMAAP_OBJECT_FACTORY.genMR_Cluster("edge");
		Entity<MR_Cluster> requestEntity = entity(mrCluster, APPLICATION_JSON);

		//when
		Response resp = testContainer.target(MR_CLUSTERS_TARGET).request().post(requestEntity, Response.class);

		//then
		assertEquals(HttpStatus.CREATED_201, resp.getStatus());
		assertTrue(resp.hasEntity());
		MR_Cluster respEntity = resp.readEntity(MR_Cluster.class);
		assertTrue(respEntity.isStatusValid());
	}

	@Test
	public void addMrCluster_shouldReturnInvalidMrCluster_whenClusterCannotBeAddedToDatabase() {
		//given
		MR_Cluster mrCluster = DMAAP_OBJECT_FACTORY.genMR_Cluster("central");
		Entity<MR_Cluster> requestEntity = entity(mrCluster, APPLICATION_JSON);
		prepareDcaeLocationForCentralCluster();

		//when
		Response resp = testContainer.target(MR_CLUSTERS_TARGET).request().post(requestEntity, Response.class);

		//then
		assertEquals(HttpStatus.OK_200, resp.getStatus());
		assertTrue(resp.hasEntity());
		MR_Cluster respEntity = resp.readEntity(MR_Cluster.class);
		assertFalse(respEntity.isStatusValid());
	}

	private void prepareDcaeLocationForCentralCluster() {
		DcaeLocation centralDcaeLoc = DMAAP_OBJECT_FACTORY.genDcaeLocation("central");
		centralDcaeLoc.setStatus(DmaapObject_Status.VALID);
		DatabaseClass.getDcaeLocations().put(centralDcaeLoc.getDcaeLocationName(), centralDcaeLoc);
	}

	@Test
	public void updateMrCluster_shouldReturnValidationError_whenDcaeLocationNameNotProvided() {
		//given
		Entity<MR_Cluster> requestEntity = entity(new MR_Cluster(), APPLICATION_JSON);

		//when
		Response resp = testContainer.target(MR_CLUSTERS_TARGET).path("clusterId")
			.request().put(requestEntity, Response.class);

		//then
		assertEquals(HttpStatus.BAD_REQUEST_400, resp.getStatus());
		assertTrue(resp.hasEntity());
		ApiError errorObj = resp.readEntity(ApiError.class);
		assertEquals("dcaeLocationName", errorObj.getFields());
	}

	@Test
	public void updateMrCluster_shouldReturnApiError_whenMrClusterWithGivenIdNotFound() {
		//given
		MR_Cluster mr_cluster = new MR_Cluster();
		mr_cluster.setDcaeLocationName("central-cloud");
		Entity<MR_Cluster> requestEntity = entity(mr_cluster, APPLICATION_JSON);

		//when
		Response resp = testContainer.target(MR_CLUSTERS_TARGET).path("notExistingMrCluster")
			.request().put(requestEntity, Response.class);

		//then
		assertEquals(HttpStatus.NOT_FOUND_404, resp.getStatus());
		assertTrue(resp.hasEntity());
		ApiError errorObj = resp.readEntity(ApiError.class);
		assertEquals("dcaeLocationName", errorObj.getFields());
	}

	@Test
	public void updateMrCluster_shouldUpdateClusterInDatabase() {
		//given
		String newReplicationGroup = "someNewReplicationGroup";
		prepareDcaeLocationForEdgeCluster();
		String clusterId = provideExistingEdgeMRClusterId();
		MR_Cluster changedMrCluster = DMAAP_OBJECT_FACTORY.genMR_Cluster("edge");
		changedMrCluster.setReplicationGroup(newReplicationGroup);
		Entity<MR_Cluster> requestEntity = entity(changedMrCluster, APPLICATION_JSON);

		//when
		Response resp = testContainer.target(MR_CLUSTERS_TARGET).path(clusterId)
			.request().put(requestEntity, Response.class);

		//then
		assertEquals(HttpStatus.CREATED_201, resp.getStatus());
		assertTrue(resp.hasEntity());
		MR_Cluster respEntity = resp.readEntity(MR_Cluster.class);
		assertTrue(respEntity.isStatusValid());
		assertEquals(newReplicationGroup, respEntity.getReplicationGroup());
	}

	private void prepareDcaeLocationForEdgeCluster() {
		DcaeLocation edgeDcaeLoc = DMAAP_OBJECT_FACTORY.genDcaeLocation("edge");
		edgeDcaeLoc.setStatus(DmaapObject_Status.VALID);
		DatabaseClass.getDcaeLocations().put(edgeDcaeLoc.getDcaeLocationName(), edgeDcaeLoc);
	}

	private String provideExistingEdgeMRClusterId() {
		MR_Cluster cluster = DMAAP_OBJECT_FACTORY.genMR_Cluster("edge");
		cluster.setStatus(DmaapObject_Status.VALID);
		DatabaseClass.getMr_clusters().put(cluster.getDcaeLocationName(), cluster);
		return cluster.getDcaeLocationName();
	}

	@Test
	public void deleteMr_Cluster_shouldReturnApiError_whenTryingToDeleteNotExistingMrCluster() {
		//when
		Response resp = testContainer.target(MR_CLUSTERS_TARGET).path("notExistingClusterId")
			.request().delete(Response.class);

		//then
		assertEquals(HttpStatus.NOT_FOUND_404, resp.getStatus());
		assertTrue(resp.hasEntity());
		ApiError errorObj = resp.readEntity(ApiError.class);
		assertEquals("dcaeLocationName", errorObj.getFields());
	}

	@Test
	public void deleteMr_Cluster_shouldRemoveMrClusterFromDatabase() {
		//given
		String clusterId = provideExistingEdgeMRClusterId();

		//when
		Response resp = testContainer.target(MR_CLUSTERS_TARGET).path(clusterId)
			.request().delete(Response.class);

		//then
		assertEquals(HttpStatus.NO_CONTENT_204, resp.getStatus());
		assertFalse(resp.hasEntity());
	}

	@Test
	public void getMr_Cluster_shouldReturnApiError_whenTryingToGetNotExistingMrCluster() {
		//when
		Response resp = testContainer.target(MR_CLUSTERS_TARGET).path("notExistingClusterId")
			.request().get(Response.class);

		//then
		assertEquals(HttpStatus.OK_200, resp.getStatus());
		assertTrue(resp.hasEntity());
		ApiError errorObj = resp.readEntity(ApiError.class);
		assertEquals("dcaeLocationName", errorObj.getFields());
	}

	@Test
	public void getMr_Cluster_shouldReturnExistingMrCluster() {
		//given
		String clusterId = provideExistingEdgeMRClusterId();

		//when
		Response resp = testContainer.target(MR_CLUSTERS_TARGET).path(clusterId)
			.request().get(Response.class);

		//then
		assertEquals(HttpStatus.CREATED_201, resp.getStatus());
		assertTrue(resp.hasEntity());
		MR_Cluster mrCluster = resp.readEntity(MR_Cluster.class);
		assertEquals(clusterId, mrCluster.getDcaeLocationName());
	}

}
