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

import org.junit.Test;
import org.onap.dmaap.dbcapi.model.ApiError;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

public class ResponseBuilderTest {

    private static final String OBJECT = "Objcect";
    private static final String MESSAGE = "msg";
    private static final int CODE = 100;
    private ResponseBuilder responseBuilder = new ResponseBuilder();

    @Test
    public void success_shouldCreateResponseWithOKStatusCode() {

        Response response = responseBuilder.success(OBJECT);

        assertEquals(OBJECT, response.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void success_shouldCreateResponseWithDefinedStatusCode() {

        Response response = responseBuilder.success(CODE, OBJECT);

        assertEquals(OBJECT, response.getEntity());
        assertEquals(CODE, response.getStatus());
    }

    @Test
    public void unauthorized_shouldCreateCorrectResponse() {

        ApiError error = new ApiError(UNAUTHORIZED.getStatusCode(), MESSAGE, "Authorization");
        Response response = responseBuilder.unauthorized(MESSAGE);

        assertEquals(error, response.getEntity());
        assertEquals(error.getCode(), response.getStatus());
    }

    @Test
    public void unavailable_shouldCreateCorrectResponse() {

        ApiError error = new ApiError(SERVICE_UNAVAILABLE.getStatusCode(),
                "Request is unavailable due to unexpected condition");
        Response response = responseBuilder.unavailable();

        assertEquals(error, response.getEntity());
        assertEquals(error.getCode(), response.getStatus());
    }

    @Test
    public void notFound_shouldCreateCorrectResponse() {
        ApiError error = new ApiError(NOT_FOUND.getStatusCode(), "Requested object not found");
        Response response = responseBuilder.notFound();

        assertEquals(error, response.getEntity());
        assertEquals(error.getCode(), response.getStatus());
    }

    @Test
    public void error_shouldCreateCorrectResponse() {
        ApiError error = new ApiError(CODE, "Some Error");
        Response response = responseBuilder.error(error);

        assertEquals(error, response.getEntity());
        assertEquals(error.getCode(), response.getStatus());
    }
}