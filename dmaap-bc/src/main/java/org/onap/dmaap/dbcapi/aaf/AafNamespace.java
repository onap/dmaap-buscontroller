/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

package org.onap.dmaap.dbcapi.aaf;

import java.util.ArrayList;
import java.util.Objects;
import org.onap.dmaap.dbcapi.util.DmaapConfig;


public class AafNamespace extends AafObject  {

	private String 	name;
	private	ArrayList<String> admin;
	private	ArrayList<String> responsible;

	// in some environments, an AAF Namespace must be owned by a human.
	// So, when needed, this var can be set via a property
	private static String NsOwnerIdentity;

	public AafNamespace(String ns, String identity ) {
		super();
		DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
		NsOwnerIdentity = p.getProperty( "aaf.NsOwnerIdentity", "");
		this.admin = new ArrayList<>();
		this.responsible = new ArrayList<>();

		this.name = ns;
		this.admin.add( identity );
		this.responsible.add( NsOwnerIdentity );
	}
	public void setName( String ns ) {
		this.name = ns;
	}
	public String getName() {
		return name;
	}
	public ArrayList<String> getAdmin() {
		return admin;
	}
	public void setAdmin(ArrayList<String> admin) {
		this.admin = admin;
	}
	public ArrayList<String> getResponsible() {
		return responsible;
	}
	public void setResponsible(ArrayList<String> responsible) {
		this.responsible = responsible;
	}


	// given an Array of Strings, return a String that is a separated list of quoted strings.
	// e.g. input [ a, b, c ]
	//       output  "a", "b", "c"
	private String separatedList( ArrayList<String> list, String sep ) {
		if (list.isEmpty()) return null;
		String aList = "";
		String delim = "";
		for( String item: list) {
			if( ! item.isEmpty()) {
				aList += String.format( "%s\"%s\"", delim, item );
				delim = sep;
			}
		}
		return aList;
	}

	public String toJSON() {

		String postJSON = String.format(" { \"name\": \"%s\", \"admin\": [",
				this.getName()
				 );
		postJSON += separatedList( this.getAdmin(), "," );
		postJSON += "], \"responsible\":[";
		postJSON += separatedList( this.getResponsible(), ",");
		postJSON += "]}";
		logger.info( "returning JSON: " + postJSON);

		return postJSON;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AafNamespace that = (AafNamespace) o;
		return Objects.equals(name, that.name) &&
				Objects.equals(admin, that.admin) &&
				Objects.equals(responsible, that.responsible);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, admin, responsible);
	}
}
