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

package org.onap.dmaap.dbcapi.resources;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.onap.dmaap.dbcapi.authentication.AuthenticationErrorException;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.service.ApiService;
import org.onap.dmaap.dbcapi.util.DmaapConfig;


@Authorization
public class AuthorizationFilter extends BaseLoggingClass implements ContainerRequestFilter   {

	private static final String AAF_CADI_FLAG = "enableCADI";
	private final ResponseBuilder responseBuilder = new ResponseBuilder();
	private final boolean isCadiEnabled;


	public AuthorizationFilter() {
		DmaapConfig dmaapConfig = (DmaapConfig) DmaapConfig.getConfig();
		String flag = dmaapConfig.getProperty(AAF_CADI_FLAG, "false");
		isCadiEnabled = "true".equalsIgnoreCase(flag);
	}

	@Override
	public void filter(ContainerRequestContext requestContext) {

		if(!isCadiEnabled) {
			ApiService apiResp = new ApiService()
				.setAuth(requestContext.getHeaderString("Authorization"))
				.setUriPath(requestContext.getUriInfo().getPath())
				.setHttpMethod(requestContext.getMethod())
				.setRequestId(requestContext.getHeaderString("X-ECOMP-RequestID"));

			try {
				apiResp.checkAuthorization();
			} catch (AuthenticationErrorException ae) {
				errorLogger.error("Error", ae);
				requestContext.abortWith(responseBuilder.unauthorized(apiResp.getErr().getMessage()));
			} catch (Exception e) {
				errorLogger.error("Error", e);
				requestContext.abortWith(responseBuilder.unavailable());
			}
		}
	}

}
