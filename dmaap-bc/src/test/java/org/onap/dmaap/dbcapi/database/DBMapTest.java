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
package org.onap.dmaap.dbcapi.database;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.Dmaap;
import org.onap.dmaap.dbcapi.util.Singleton;

public class DBMapTest {

    private static Singleton<Dmaap> dmaap;
    private static Map<String, DcaeLocation> dcaeLocations;

	@Before
	public void setUp() throws Exception {
		System.setProperty("ConfigFile", "src/test/resources/dmaapbc.properties");
	}

	@Test
	public void test1() {
		try {
                dmaap = new DBSingleton<>(Dmaap.class, "dmaap");
				Dmaap nd = new Dmaap.DmaapBuilder().createDmaap();
				dmaap.update(nd);
		} catch (Exception e ) {
		}
		try {
                dcaeLocations = new DBMap<>(DcaeLocation.class, "dcae_location", "dcae_location_name");
		} catch (Exception e ) {
		}
	}
}

