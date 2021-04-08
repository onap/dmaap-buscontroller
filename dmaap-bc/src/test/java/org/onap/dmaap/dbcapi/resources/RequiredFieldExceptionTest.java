/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (c) 2019 IBM
 * ===================================================================
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

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dbcapi.model.ApiError;

public class RequiredFieldExceptionTest {
    ApiError apiError;
    RequiredFieldException requiredFieldException;
    String expectedValue;

    @Before
    public void setUp() {
        apiError = new ApiError(BAD_REQUEST.getStatusCode(), "value 'with white space' violates regexp check '^\\S+$'",
                "field_name");

        expectedValue = "RequiredFieldException{" + "apiError=" + apiError + '}';

        requiredFieldException = new RequiredFieldException(apiError);
    }

    @Test
    public void testRequiredFieldExceptionToString() {
        assertEquals(expectedValue, requiredFieldException.toString());
    }
}
