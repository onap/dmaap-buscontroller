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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onap.dmaap.dbcapi.model.ApiError;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.junit.Assert.fail;

public class RequiredCheckerTest {

    private static final String NAME = "field_name";
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private RequiredChecker requiredChecker = new RequiredChecker();


    @Test
    public void required_shouldThrowExceptionWhenObjectIsNull() throws RequiredFieldException {
        thrown.expect(RequiredFieldException.class);
        thrown.expect(new ApiErrorMatcher(new ApiError(BAD_REQUEST.getStatusCode(),
                "missing required field", NAME)));

        requiredChecker.required(NAME, null);
    }

    @Test
    public void required_shouldThrowExceptionWhenRegexValidationFailed() throws RequiredFieldException {
        thrown.expect(RequiredFieldException.class);
        thrown.expect(new ApiErrorMatcher(new ApiError(BAD_REQUEST.getStatusCode(),
                "value 'with white space' violates regexp check '^\\S+$'", NAME)));

        requiredChecker.required(NAME, "with white space", "^\\S+$");
    }

    @Test
    public void required_shouldPassValidation() {
        try {
            requiredChecker.required(NAME, "value", "^\\S+$");
        } catch (RequiredFieldException e) {
            fail("No exception should be thrown");
        }
    }

    class ApiErrorMatcher extends BaseMatcher {

        private final ApiError expectedApiEror;

        ApiErrorMatcher(ApiError expectedApiEror) {
            this.expectedApiEror = expectedApiEror;
        }

        @Override
        public boolean matches(Object exception) {
            return expectedApiEror.equals(((RequiredFieldException) exception).getApiError());
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Following ApiError is expected: ").appendValue(expectedApiEror);
        }
    }
}