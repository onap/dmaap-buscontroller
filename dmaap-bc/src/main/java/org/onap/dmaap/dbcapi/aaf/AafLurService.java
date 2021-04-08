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

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.onap.aaf.cadi.Access;
import org.onap.aaf.cadi.CadiException;
import org.onap.aaf.cadi.LocatorException;
import org.onap.aaf.cadi.Permission;
import org.onap.aaf.cadi.aaf.AAFPermission;
import org.onap.aaf.cadi.aaf.v2_0.AAFAuthn;
import org.onap.aaf.cadi.aaf.v2_0.AAFConHttp;
import org.onap.aaf.cadi.aaf.v2_0.AAFLurPerm;
import org.onap.aaf.cadi.principal.UnAuthPrincipal;
import org.onap.aaf.misc.env.APIException;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;

/*
 * this service uses the AAF Lur object to lookup identities and perms
 */
public class AafLurService extends BaseLoggingClass {

	private static AAFConHttp aafcon;
	private static AAFLurPerm aafLur;
	private static AAFAuthn<?> aafAuthn;


	/*
	 * singleton pattern suggested by AAF
	 */
	private static AafLurService singleton;
	private AafLurService() {}



	private static void init( Access myAccess ) throws APIException, CadiException, LocatorException {
		appLogger.info( "myAccess=" + myAccess );
		try {
			aafcon = new AAFConHttp( myAccess );
		} catch ( CadiException | LocatorException e) {
			appLogger.error( "Failure of AAFConHttp: " + e.getMessage() );
			errorLogger.error( "Failure of AAFConHttp: " + e.getMessage() );
			errorLogger.error(e.getMessage());

			throw e;
		}
		try {
			aafLur = aafcon.newLur();
		} catch ( CadiException  e) {
			appLogger.error( "Failure of newLur(): " + e.getMessage() );
			errorLogger.error( "Failure of newLur(): " + e.getMessage() );
			errorLogger.error(e.getMessage());

			throw e;
		}
		aafAuthn = aafcon.newAuthn( aafLur );
	}

	public static synchronized AafLurService getInstance( Access myAccess ) throws APIException, CadiException, LocatorException{
		if ( singleton == null ) {
			singleton = new AafLurService();
			try {
				init( myAccess );
			} catch (APIException | CadiException | LocatorException e) {
				errorLogger.error(e.getMessage());
				throw e;
			}

		}
		return singleton;
	}


	public boolean checkPerm(String ns, String fqi, String pwd, DmaapPerm p) throws IOException, CadiException {

		boolean rc = false;

		if ( aafAuthn == null ) {
			appLogger.error( "AafLurService: aafAuthn not set as expected.");
			return rc;
		}

		String ok = aafAuthn.validate( fqi,  pwd );
		if ( ok != null ) {
			appLogger.info( "FAILED validation of fqi=" + fqi + "with response:" + ok );
			return rc;
		}

		Principal principal = new UnAuthPrincipal( fqi );
		// if we pass ns as first arg to AAFPermission constructor it gets prpended to the instance...
		// as in ns|instance|type|action.   we don't want that.
		Permission aafPerm = new AAFPermission( null, p.getPermission(), p.getPtype(), p.getAction());
		if ( aafLur == null ) {
			appLogger.error( "AafLurService: aafLur not set as expected.");
			return rc;
		}
		rc =  aafLur.fish( principal, aafPerm );
		boolean flag = true;
		if (rc == flag ) {
			return rc;
		}

		List<Permission> perms = new ArrayList<>();
		aafLur.fishAll( principal,  perms);
		String key = aafPerm.getKey();
		for ( Permission prm: perms ) {
			if ( prm.getKey().equals( key )) {
				appLogger.info( principal + " has MATCHING perm " + prm.getKey() );
			} else {
				appLogger.info( principal + " has non-matching perm " + prm.getKey() );
			}
		}

		return rc;

	}
}
