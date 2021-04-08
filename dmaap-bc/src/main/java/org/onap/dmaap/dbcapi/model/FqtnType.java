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
package org.onap.dmaap.dbcapi.model;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public enum FqtnType {
	FQTN_NOT_SPECIFIED(0),
	FQTN_LEGACY_FORMAT(1),
	FQTN_PROJECTID_FORMAT(2),
	FQTN_PROJECTID_VERSION_FORMAT(3);


    private int value;
    private static Map map = new HashMap<>();

    private FqtnType(int value) {
        this.value = value;
    }

    static {
        for (FqtnType repType : FqtnType.values()) {
            map.put(repType.value, repType);
        }
    }

    public static FqtnType valueOf(int repType) {
        return (FqtnType) map.get(repType);
    }

    public int getValue() {
        return value;
    }

    static public FqtnType Validator( String input ){
   
		FqtnType t;
		try {
			t = FqtnType.valueOf( input );
		} catch ( IllegalArgumentException e ) {
			t = FQTN_NOT_SPECIFIED;
		}
		return t;
	}

}