/*-
 * ============LICENSE_START=======================================================
  * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.dmaap.dbcapi.authentication;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.onap.aaf.cadi.CadiException;
import org.onap.aaf.cadi.LocatorException;
import org.onap.aaf.cadi.PropAccess;
import org.onap.aaf.misc.env.APIException;
import org.onap.dmaap.dbcapi.aaf.AafLurService;
import org.onap.dmaap.dbcapi.aaf.DmaapPerm;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.util.DmaapConfig;


public class AafLurAndFish extends BaseLoggingClass implements ApiAuthorizationCheckInterface {
	private AafLurService svc;
	private static String apiNamespace;
	private static final String ERROR="Error";

	AafLurAndFish()  throws AuthenticationErrorException  {

		DmaapConfig p = (DmaapConfig)DmaapConfig.getConfig();
		apiNamespace = p.getProperty( "ApiNamespace", "org.onap.dmaap-bc.api");

		String cadiprop = p.getProperty( "cadi.properties", "/opt/app/osaaf/local/org.onap.dmaap-bc.props");
		logger.info( "cadiprops in " + cadiprop );
		Properties props = new Properties();
		try {
			FileInputStream fis = new FileInputStream( cadiprop );
			try {
				props.load( fis );
			} finally {
				fis.close();
			}
		} catch ( IOException e ) {
			errorLogger.error( "Unable to load " + cadiprop );
			errorLogger.error(ERROR, e);
			throw new AuthenticationErrorException( );
		}
		try {
			PropAccess myAccess = new PropAccess( props );

			svc =  AafLurService.getInstance(myAccess);
		} catch (APIException | CadiException | LocatorException e ) {
			errorLogger.error(ERROR, e);
			errorLogger.error( e.toString() );
			throw new AuthenticationErrorException();
		}

	}

	public void check( String mechid, String pwd, DmaapPerm p ) throws AuthenticationErrorException {

		try {
		    if (mechid.isEmpty() || pwd.isEmpty()) {
                throw new AuthenticationErrorException("No basic authorization value provided");
            }

			if (!svc.checkPerm( apiNamespace, mechid, pwd, p )) {
				throw new AuthenticationErrorException();
			}
		} catch ( IOException | CadiException  e ) {
			errorLogger.error(ERROR, e);
			errorLogger.error( e.toString() );
			throw new AuthenticationErrorException();
		}

	}

	 public static void main(String[] args) throws Exception {
	        AafLurAndFish alaf = new AafLurAndFish();
	        DmaapPerm p = new DmaapPerm( "org.onap.dmaap-bc.api.dmaap", "boot", "GET");

	        try {
	        	alaf.check("mmanager@people.osaaf.org", "demo123456!", p);
	        } catch (AuthenticationErrorException aee ) {
				errorLogger.error(aee.getMessage());
				errorLogger.error( "Check failed for: " + p.toJSON());
	        	System.exit(-1);
	        }
		 errorLogger.info("Check succeeded for: " + p.toJSON());
	    }
}
