/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 *
 * Modifications Copyright (C) 2019 IBM.
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

package org.onap.dmaap.dbcapi.util;

import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.MR_Client;
import org.onap.dmaap.dbcapi.model.MR_Cluster;
import org.onap.dmaap.dbcapi.service.MR_ClusterService;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Graph {
	private HashMap<String, String> graph;
	private boolean	hasCentral;
	
	private Map<String, DcaeLocation> locations = DatabaseClass.getDcaeLocations();
	
	//TODO add to properties file
	private static String centralDcaeLayerName = "central";

	
	public Graph(HashMap<String, String> graph) {
		super();
		this.graph = graph;
	}

	public Graph( List<MR_Client> clients, boolean strict ) {
		if ( clients == null )
			return;
		initGraph( clients, strict, "" );

	}
	public Graph( List<MR_Client> clients, boolean strict, String group ) {
		if ( clients == null )
			return;
		initGraph( clients, strict, group );
	}
	
	private void initGraph(List<MR_Client> clients, boolean strict, String group ) {
		MR_ClusterService clusters = new MR_ClusterService();
		this.graph = new HashMap<>();
		this.hasCentral = false;
		for( MR_Client client: clients ) {
			if ( ! strict || client.isStatusValid()) {
				String loc = client.getDcaeLocationName();
				DcaeLocation dcaeLoc = locations.get(loc);
				if ( dcaeLoc == null ) continue;
				MR_Cluster c = clusters.getMr_ClusterByLoc(loc);
				if ( group != null &&  ! group.isEmpty() && ! group.equals(c.getReplicationGroup())) continue;
				
				for( String action : client.getAction() ){			
					if ( ! action.equals("view") && dcaeLoc != null ) {
						String layer = dcaeLoc.getDcaeLayer();
						if ( layer != null && layer.contains(centralDcaeLayerName) ) {
							this.hasCentral = true;
						}
						graph.put(loc, layer);
					}
				}
	
			}		
		}		
	}
	
	public HashMap<String, String> getGraph() {
		return graph;
	}

	public void setGraph(HashMap<String, String> graph) {
		this.graph = graph;
	}
	
	public String put( String key, String val ) {
		return graph.put(key, val);
	}
	
	public String get( String key ) {
		return graph.get(key);
	}
	
	public Collection<String> getKeys() {
		return graph.keySet();
	}
	public boolean hasCentral() {
		return hasCentral;
	}
	public void setHasCentral(boolean hasCentral) {
		this.hasCentral = hasCentral;
	}
	
	public String getCentralLoc() {
		if ( ! hasCentral ) {
			return null;
		}
		for( String loc : graph.keySet()) {
			if ( graph.get(loc).contains(centralDcaeLayerName)) {
				return loc;
			}
		}
		return null;
	}
	public boolean isEmpty() {
		return graph.isEmpty();
	}
	
	
}
