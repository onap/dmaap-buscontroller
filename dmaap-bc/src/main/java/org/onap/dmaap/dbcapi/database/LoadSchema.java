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

package org.onap.dmaap.dbcapi.database;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.Statement;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;

public class LoadSchema	extends BaseLoggingClass {

	private LoadSchema(){}

	static void loadSchema() {
		ConnectionFactory cf = ConnectionFactory.getDefaultInstance();
		try (LineNumberReader lineReader = new LineNumberReader(new FileReader("/opt/app/dmaapbc/misc/schema_all.sql"));
			Connection c = cf.get(true);
			Statement stmt = c.createStatement()) {
			StringBuilder strBuilder = new StringBuilder();
			String line;
			while ((line = lineReader.readLine()) != null) {
				if (!line.startsWith("--")) {
					line = line.trim();
					strBuilder.append(line);
					if (line.endsWith(";")) {
						String sql = strBuilder.toString();
						strBuilder.setLength(0);
						stmt.execute(sql);
						appLogger.debug("SQL EXECUTE SUCCESS: " + sql);
					}
				}
			}
			strBuilder.setLength(0);
		} catch (Exception e) {
			errorLogger.error("Error when initializing table: " + e.getMessage(), e);
		}
	}
}
