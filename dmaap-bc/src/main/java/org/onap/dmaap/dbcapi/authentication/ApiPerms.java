/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * 
 * Modifications Copyright (C) 2018 IBM.
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

package org.onap.dmaap.dbcapi.authentication;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import org.onap.dmaap.dbcapi.aaf.AafService;
import org.onap.dmaap.dbcapi.aaf.AafServiceFactory;
import org.onap.dmaap.dbcapi.aaf.DmaapGrant;
import org.onap.dmaap.dbcapi.aaf.DmaapPerm;
import org.onap.dmaap.dbcapi.aaf.AafService.ServiceType;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.logging.DmaapbcLogMessageEnum;
import org.onap.dmaap.dbcapi.model.Dmaap;
import org.onap.dmaap.dbcapi.service.DmaapService;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

public  class ApiPerms extends BaseLoggingClass {
	static String topic = "topics";
	static String mrClusters = "mr_clusters";
	static String mrClients = "mr_clients";
	static String feed = "feeds";
	static String drSubs = "dr_subs";
	static String drPubs = "dr_pubs";
	static String drNodes = "dr_nodes";
	static String dcaeLocations = "dcaeLocations";
	static String inventory = "Inventory";
	static String portalUser = "PortalUser";
	static String orchestrator = "Orchestrator";
	static String delete = "DELETE";
	static String dmaap = "dmaap";
	static String controller = "Controller";
	
	private static class PermissionMap {
		static final EELFLogger logger = EELFManager.getInstance().getLogger( PermissionMap.class );
		static final EELFLogger errorLogger = EELFManager.getInstance().getErrorLogger();
		String uri;
		String action;
		String[] roles;
		
		private PermissionMap( String u, String a, String[] r ) {
			this.setUri(u);
			this.setAction(a);
			this.setRoles(r);
		}	
		
		public String getUri() {
			return uri;
		}
		public void setUri(String uri) {
			this.uri = uri;
		}
		public String getAction() {
			return action;
		}
		public void setAction(String action) {
			this.action = action;
		}

		public String[] getRoles() {
			return roles;
		}
		public void setRoles(String[] roles) {
			this.roles = roles;
		}

		public static void initMap( PermissionMap[] pmap, String instance ) {

			DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
			String api = p.getProperty("ApiNamespace", "apiNamespace.not.set");

			AafService aaf = new AafServiceFactory().initAafService(ServiceType.AAF_Admin);
			
			for ( int i = 0; i < pmap.length ; i++ ) {
				String uri = new String( api + "." + pmap[i].getUri());
				DmaapPerm perm = new DmaapPerm( uri, instance, pmap[i].getAction() );
				int rc = aaf.addPerm( perm );
				if ( rc != 201 &&  rc != 409 ) {
					errorLogger.error( DmaapbcLogMessageEnum.AAF_UNEXPECTED_RESPONSE,  Integer.toString(rc), "add perm",  perm.toString() );

				}
				for( String r: pmap[i].getRoles()) {
					String fr = new String( api + "." + r );
					logger.debug( "i:" + i + " granting perm " + perm.toString()+ " to role=" + fr );
					DmaapGrant grant = new DmaapGrant( perm, fr );
					rc = aaf.addGrant( grant );
					if ( rc != 201 && rc != 409 ) {
						errorLogger.error( DmaapbcLogMessageEnum.AAF_UNEXPECTED_RESPONSE,  Integer.toString(rc), "grant perm",  perm.toString() );
					}
				}
				
			}
		}
	}
	
	static PermissionMap[] bootMap = {
		new PermissionMap( dmaap, "GET", new String[] { controller }),
		new PermissionMap( dmaap, "POST", new String[] { controller }),	
		new PermissionMap( dmaap, "PUT", new String[] { controller }),
		new PermissionMap( dmaap, delete, new String[] { controller })
	
	};

	static PermissionMap[] envMap = {
		new PermissionMap( dmaap, "GET", new String[] { controller, orchestrator, inventory, "Metrics", portalUser }),
		new PermissionMap( dmaap, "POST", new String[] { controller } ),		
		new PermissionMap( dmaap, "PUT", new String[] { controller }),
		new PermissionMap( dmaap, delete, new String[] { controller }),
		new PermissionMap( "bridge", "GET", new String[] {  "Metrics" }),
		//new PermissionMap( "bridge", "POST", new String[] { "Metrics" } ),		
		//new PermissionMap( "bridge", "PUT", new String[] { "Metrics" }),
		//new PermissionMap( "bridge", delete, new String[] { "Metrics" }),
		new PermissionMap( dcaeLocations, "GET", new String[] { controller, orchestrator, inventory, "Metrics", portalUser }),
		new PermissionMap( dcaeLocations, "POST", new String[] { controller } ),		
		new PermissionMap( dcaeLocations, "PUT", new String[] { controller }),
		new PermissionMap( dcaeLocations, delete, new String[] { controller }),
		new PermissionMap( drNodes, "GET", new String[] { controller, orchestrator, inventory,  portalUser }),
		new PermissionMap( drNodes, "POST", new String[] { controller } ),		
		new PermissionMap( drNodes, "PUT", new String[] { controller }),
		new PermissionMap( drNodes, delete, new String[] { controller }),
		new PermissionMap( drPubs, "GET", new String[] { controller, orchestrator, inventory, "Metrics", portalUser }),
		new PermissionMap( drPubs, "POST", new String[] { controller, orchestrator,portalUser } ),		
		new PermissionMap( drPubs, "PUT", new String[] { controller, orchestrator,portalUser }),
		new PermissionMap( drPubs, delete, new String[] { controller, orchestrator,portalUser }),
		new PermissionMap( drSubs, "GET", new String[] { controller, orchestrator, inventory, "Metrics", portalUser }),
		new PermissionMap( drSubs, "POST", new String[] { controller, orchestrator,portalUser } ),		
		new PermissionMap( drSubs, "PUT", new String[] { controller, orchestrator,portalUser }),
		new PermissionMap( drSubs, delete, new String[] { controller, orchestrator,portalUser }),
		new PermissionMap( feed, "GET", new String[] { controller, orchestrator, inventory, "Metrics", portalUser }),
		new PermissionMap( feed, "POST", new String[] { controller, orchestrator,portalUser } ),		
		new PermissionMap( feed, "PUT", new String[] { controller, orchestrator, portalUser }),
		new PermissionMap( feed, delete, new String[] { controller, portalUser }),
		new PermissionMap( mrClients, "GET", new String[] { controller, orchestrator, inventory, "Metrics", portalUser }),
		new PermissionMap( mrClients, "POST", new String[] { controller,orchestrator, portalUser } ),		
		new PermissionMap( mrClients, "PUT", new String[] { controller, orchestrator,portalUser }),
		new PermissionMap( mrClients, delete, new String[] { controller,orchestrator, portalUser }),
		new PermissionMap( mrClusters, "GET", new String[] { controller, orchestrator, inventory, "Metrics", portalUser }),
		new PermissionMap( mrClusters, "POST", new String[] { controller } ),		
		new PermissionMap( mrClusters, "PUT", new String[] { controller }),
		new PermissionMap( mrClusters, delete, new String[] { controller }),
		new PermissionMap( topic, "GET", new String[] { controller, orchestrator, inventory, "Metrics", portalUser }),
		new PermissionMap( topic, "POST", new String[] { controller, orchestrator } ),		
		new PermissionMap( topic, "PUT", new String[] { controller, orchestrator }),
		new PermissionMap( topic, delete, new String[] { controller, orchestrator })
	};
	
	public void setBootMap() {
		String instance = "boot";
		PermissionMap.initMap( bootMap, instance );
	}
	
	public void setEnvMap() {
		Dmaap dmaapVar = new DmaapService().getDmaap();
		String dmaapName = dmaapVar.getDmaapName();
		PermissionMap.initMap( envMap, dmaapName );
	}
	

}
