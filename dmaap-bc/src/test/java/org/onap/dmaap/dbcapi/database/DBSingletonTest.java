
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

import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dbcapi.model.Dmaap;

public class DBSingletonTest {
	@Before
	public void setUp() throws Exception {
		System.setProperty("ConfigFile", "src/test/resources/dmaapbc.properties");
	}

	@Test
	public void test3() {

		try {
			DBSingleton<Dmaap> dmaap = new DBSingleton<>(Dmaap.class, "dmaap");
			Dmaap d = new Dmaap.DmaapBuilder().createDmaap();
			dmaap.init( d );
			d = dmaap.get();
			d.setDmaapName( "foo" );
			dmaap.update( d );
			dmaap.remove();
		} catch (Exception e ) {
		}
	}
}

