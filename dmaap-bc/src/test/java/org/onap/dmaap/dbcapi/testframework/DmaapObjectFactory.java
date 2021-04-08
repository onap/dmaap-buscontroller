/*
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
package org.onap.dmaap.dbcapi.testframework;

import org.onap.dmaap.dbcapi.model.*;
import org.onap.dmaap.dbcapi.util.RandomInteger;

public class DmaapObjectFactory {

	/*
	 * we use localhost for most references so that connection attempts will resolve and not retry
	 * but still we expect that requests will fail.
	 */
	private static final String  fmt = "%24s: %s%n";
	private static final String	dmaap_name = "onap-ut";
	private static final String dmaap_ver = "1";
	private static final String dmaap_topic_root = "org.onap.dmaap";
	private static final String dmaap_dr = "https://localhost:8443";
	private static final String dmaap_log_url = "http://localhost:8080/log";
	private static final String dmaap_mm_topic = "org.onap.dmaap.dcae.MM_AGENT_TOPIC";
	private static final String central_loc = "SanFrancisco";
	private static final String central_layer = "central-cloud";
	private static final String central_clli = "SFCAL19240";
	private static final String central_zone = "osaz01";
	private static final String central_subnet = "10.10.10.0/24";
	private static final String central_cluster_fqdn = "localhost";
	private static final String pub_role = "org.onap.vnfapp.publisher";
	private static final String sub_role = "org.onap.vnfapp.subscriber";
	private static final String edge_loc = "Atlanta";
	private static final String edge_layer = "edge-cloud";
	private static final String edge_clli = "ATLGA10245";
	private static final String edge_zone = "osaz02";
	private static final String edge_subnet = "10.10.20.0/24";
	private static final String edge_cluster_fqdn = "localhost";
	private static final String[]hosts = { "host1", "host2", "host3" };
	private static final String port = "3904";
	private static final String prot = "http";

	public Dmaap genDmaap() {
		return new Dmaap.DmaapBuilder().setVer(dmaap_ver).setTnr(dmaap_topic_root).setDn(dmaap_name).setDpu(dmaap_dr).setLu(dmaap_log_url).setBat(dmaap_mm_topic).setNk("nk").setAko("ako").createDmaap();
	}

	public DcaeLocation genDcaeLocation( String layer ) {
		if ( layer.contains( "edge" ) ) {
			return new DcaeLocation( edge_clli, edge_layer, edge_loc, edge_zone, edge_subnet );
		}
		return new DcaeLocation( central_clli, central_layer, central_loc, central_zone, central_subnet );
	}


	public MR_Cluster genMR_Cluster( String layer ) {
		if ( layer.contains( "edge" ) ) {
			return new MR_Cluster( edge_loc, edge_cluster_fqdn,  prot, port );
		}
		return new MR_Cluster( central_loc, central_cluster_fqdn, prot, port );
	}

	public Topic genSimpleTopic( String tname ) {
		Topic t = new Topic();
		t.setTopicName( tname );
        t.setFqtnStyle( FqtnType.Validator("none") );
        t.setTopicDescription( "a simple Topic named " + tname );
        t.setOwner( "ut");
        t.setFqtn(t.genFqtn());
		return t;
	}

	public MR_Client genMR_Client( String l, String f, String r, String[] a ) {
		if ( l.contains( "edge" ) ) {
			return new MR_Client( edge_loc, f, r, a );
		}
		return new MR_Client( central_loc, f, r, a );
	}

	public MR_Client genPublisher( String layer, String fqtn ) {
		String[] actions = { "pub", "view" };
		return genMR_Client( layer, fqtn, pub_role, actions );
	}
	public MR_Client genSubscriber( String layer, String fqtn ) {
		String[] actions = { "sub", "view" };
		return genMR_Client( layer, fqtn, sub_role, actions );
	}

	public DR_Sub genDrSub( String l, String feed ) {
        String un = "user1";
        String up = "secretW0rd";
        String du = "sub.server.onap.org:8443/deliver/here";
        String lu = "https://drps.onap.org:8443/sublog/123";
        boolean u100 = true;

		if ( l.contains( "edge" ) ) {
			return new DR_Sub( edge_loc, un, up, feed, du, lu, u100 );
		}
		return new DR_Sub( central_loc, un, up, feed, du, lu, u100 );
	}

	public DR_Node genDR_Node( String l ) {
        String version = "1.0.1";
		RandomInteger ri = new RandomInteger( 1000 );
		int i = ri.next();
		String fqdn = String.format( "drns%d.onap.org", i );
		String host = String.format( "host%d.onap.org", i );

		if ( l.contains( "edge" ) ) {
			return new DR_Node( fqdn, edge_loc, host, version );
		}
		return new DR_Node( fqdn, central_loc, host, version );
	}
				

}
