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

public class AafRole extends AafObject  {

	private String 	namespace;
	private	String	role;
	
	public AafRole(String ns,  String role) {
		super();
		this.namespace = ns;
		this.role = role;
	}
	public void setNamespace( String ns ) {
		this.namespace = ns;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getRole() {
		return role;
	}
	public String getFullyQualifiedRole() {
		return namespace + "." + role;
	}

	public String toJSON() {

		String postJSON = String.format(" { \"name\": \"%s.%s\"}", 
				this.getNamespace(), 
				this.getRole() );
		logger.info( "returning JSON: " + postJSON);
			
		return postJSON;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AafRole aafRole = (AafRole) o;
		return Objects.equals(namespace, aafRole.namespace) &&
				Objects.equals(role, aafRole.role);
	}

	@Override
	public int hashCode() {
		return Objects.hash(namespace, role);
	}
}
