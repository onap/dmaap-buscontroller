/*
 * ============LICENSE_START=======================================================
 * PNF-REGISTRATION-HANDLER
 * ================================================================================
 * Copyright (C) 2019 NOKIA Intellectual Property. All rights reserved.
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

package org.onap.dmaap.dbcapi.resources;


import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.onap.dmaap.dbcapi.model.ApiError;

public class RequiredChecker {

    public void required(String name, Object val) throws RequiredFieldException {
        if (val == null) {
            throw new RequiredFieldException(new ApiError(BAD_REQUEST.getStatusCode(),
                    "missing required field", name));
        }
    }

    public void required(String name, String val, String expr) throws RequiredFieldException {

        required(name, val);

        if (expr != null && !expr.isEmpty()) {
            Pattern pattern = Pattern.compile(expr);
            Matcher matcher = pattern.matcher(val);
            if (!matcher.find()) {
                throw new RequiredFieldException(new ApiError(BAD_REQUEST.getStatusCode(),
                        "value '" + val + "' violates regexp check '" + expr + "'", name));
            }
        }
    }

}
