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

import static com.att.eelf.configuration.Configuration.MDC_RESPONSE_CODE;
import static com.att.eelf.configuration.Configuration.MDC_RESPONSE_DESC;
import static com.att.eelf.configuration.Configuration.MDC_STATUS_CODE;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import javax.ws.rs.core.Response;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.slf4j.MDC;

public class ResponseBuilder extends BaseLoggingClass {

    Response success(Object d) {
        return buildSuccessResponse(d, Response.Status.OK.getStatusCode());
    }

    Response success(int code, Object d) {
        return buildSuccessResponse(d, code);
    }

    Response error(ApiError err) {
        return buildErrResponse(err);
    }

    Response unauthorized(String msg) {
        return buildErrResponse(new ApiError(UNAUTHORIZED.getStatusCode(), msg, "Authorization"));
    }

    Response unavailable() {
        return buildErrResponse(new ApiError(SERVICE_UNAVAILABLE.getStatusCode(),
                "Request is unavailable due to unexpected condition"));
    }

    Response notFound() {
        return buildErrResponse(new ApiError(NOT_FOUND.getStatusCode(),"Requested object not found"));
    }

    private Response buildSuccessResponse(Object d, int code) {
        MDC.put(MDC_STATUS_CODE, "COMPLETE");
        MDC.put(MDC_RESPONSE_DESC, "");
        return buildResponse(d, code);
    }

    private Response buildErrResponse(ApiError err) {
        MDC.put(MDC_STATUS_CODE, "ERROR");
        MDC.put(MDC_RESPONSE_DESC, err.getMessage());

        return buildResponse(err, err.getCode());
    }

    private Response buildResponse(Object obj, int code) {
        MDC.put(MDC_RESPONSE_CODE, String.valueOf(code));

        auditLogger.auditEvent("");
        return Response.status(code)
                .entity(obj)
                .build();
    }
}
