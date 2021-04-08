/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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
package org.onap.dmaap.dbcapi.server;

import org.glassfish.jersey.server.ResourceConfig;
import org.onap.dmaap.dbcapi.resources.RequestTimeLogFilter;
import org.onap.dmaap.dbcapi.resources.AuthorizationFilter;

public class ApplicationConfig extends ResourceConfig {
	
	/*
	 * Register JAX-RS application components
	 */
	public ApplicationConfig() {
		
        register(AuthorizationFilter.class).
					register(RequestTimeLogFilter.class);
  
    }
}
