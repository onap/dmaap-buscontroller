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

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Objects;

@XmlRootElement
public class ApiError implements Serializable {
	private int code;
	private String message;
	private String fields;

	public ApiError() {
		this(0, null, null);
	}

	public ApiError(int code, String message) {
		this(code, message, null);
	}

	public ApiError(int code, String message, String fields) {
		this.code = code;
		this.message = message;
		this.fields = fields;
	}

	public int getCode() {
		return code;
	}
	public void setCode(int rc) {
		this.code = rc;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getFields() {
		return fields;
	}
	public void setFields(String fields) {
		this.fields = fields;
	}
	public String toString() {
		return String.format( "code=%d msg=%s fields=%s", this.code, this.message, this.fields );
	}
	public boolean is2xx() {
		
		return code >= 200 && code < 300;
	}
	public void reset() {
		code = 0;
		message = null;
		fields = null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ApiError apiError = (ApiError) o;
		return code == apiError.code &&
				Objects.equals(message, apiError.message) &&
				Objects.equals(fields, apiError.fields);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, message, fields);
	}
}
