/*-
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

package org.onap.dmaap.dbcapi.util;

import java.util.regex.Pattern;

import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;

public class Fqdn extends BaseLoggingClass {
	// regexp value sourced from https://www.regextester.com/23
	static String regexp = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";
	
	
	public static boolean isValid( String s ) {
		appLogger.info( "Fqdn testing: " + s );
		boolean b = false;
		if ( s != null ) {
			b = Pattern.matches( regexp, s);
		}
		appLogger.info( "Fqdn isValid=" + b );
		return b;
	}

}
