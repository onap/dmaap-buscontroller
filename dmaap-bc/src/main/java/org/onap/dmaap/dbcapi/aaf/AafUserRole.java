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

package org.onap.dmaap.dbcapi.aaf;

import java.util.Objects;


public class AafUserRole extends AafObject  {

	private String 	identity;
	private	String	role;


	
	public AafUserRole(String identity,  String role ) {
		super();
		this.identity = identity;
		this.role = role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	public String getRole() {
		return role;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String toJSON() {

		String postJSON = String.format(" { \"user\": \"%s\", \"role\": \"%s\" }",  
				this.getIdentity(), 
				this.getRole()
				);
		logger.info( "returning JSON: " + postJSON);
			
		return postJSON;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AafUserRole that = (AafUserRole) o;
		return Objects.equals(identity, that.identity) &&
				Objects.equals(role, that.role);
	}

	@Override
	public int hashCode() {

		return Objects.hash(identity, role);
	}
}
