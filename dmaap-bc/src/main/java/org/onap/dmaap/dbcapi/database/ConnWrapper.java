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

import java.sql.*;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;


public abstract class ConnWrapper<T, U>	{
	EELFLogger logger = EELFManager.getInstance().getLogger( ConnWrapper.class );
	protected Connection c;
	protected PreparedStatement ps;
	protected ResultSet	rs;
	protected abstract T run(U u) throws Exception;
	public T protect(ConnectionFactory cf, U u) {
		try {
			try {
				return(attempt(cf, u, false));
			} catch (SQLException sqle) {
				logger.error("Error", sqle);
				return(attempt(cf, u, true));
			}
		} catch (RuntimeException rte) {
			throw rte;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}
	private T attempt(ConnectionFactory cf, U u, boolean fresh) throws Exception {
		c = null;
		ps = null;
		rs = null;
		try {
			c = cf.get(fresh);
			T ret = run(u);
			cf.release(c);
			c = null;
			return(ret);
		} finally {
			if (rs != null) { 
				try { 
					rs.close(); 
				} catch (Exception e) {
					logger.error("Error", e);
				}}
			rs = null;
			if (ps != null) { 
				try { 
					ps.close(); 
				} catch (Exception e) {
					logger.error("Error", e);
				}}
			ps = null;
			if (c != null) { 
				try { 
					c.close(); 
				} catch (Exception e) {
					logger.error("Error", e);
				}}
			c = null;
		}
	}
}
