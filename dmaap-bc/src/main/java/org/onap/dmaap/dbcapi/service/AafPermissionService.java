/*-
 * ============LICENSE_START=======================================================
 * org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2019 Nokia Intellectual Property. All rights reserved.
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

package org.onap.dmaap.dbcapi.service;

import org.onap.dmaap.dbcapi.aaf.AafService;
import org.onap.dmaap.dbcapi.aaf.AafUserRole;
import org.onap.dmaap.dbcapi.aaf.DmaapGrant;
import org.onap.dmaap.dbcapi.aaf.DmaapPerm;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status;
import org.onap.dmaap.dbcapi.model.MR_Client;

import static java.lang.String.format;

class AafPermissionService extends BaseLoggingClass {

    private static final String INSTANCE_PREFIX = ":topic.";
    private final AafService aafService;
    private final DmaapService dmaapService;

    AafPermissionService(AafService aafService, DmaapService dmaapService) {
        this.aafService = aafService;
        this.dmaapService = dmaapService;
    }

    ApiError assignClientToRole(MR_Client client, String role) {
        AafUserRole ur = new AafUserRole(client.getClientIdentity(), role);
        int rc = aafService.addUserRole(ur);
        if (rc != 201 && rc != 409) {
            return handleErrorStatus(rc, client,
                    format("Failed to add user %s to role %s", client.getClientIdentity(), role));
        }
        return handleOkStatus(client);
    }

    ApiError grantClientRolePerms(MR_Client client) {
        return forEachClientAction(client, this::grantPermForClientRole);
    }

    private ApiError forEachClientAction(MR_Client client, PermissionUpdate permissionUpdate) {
        try {
            String instance = INSTANCE_PREFIX + client.getFqtn();

            for (String action : client.getAction()) {
                permissionUpdate.execute(client.getClientRole(), instance, action);
            }

        } catch (PermissionServiceException e) {
            return handleErrorStatus(e.getCode(), client, e.getMessage());
        }
        return handleOkStatus(client);
    }

    private void grantPermForClientRole(String clientRole, String instance, String action) throws PermissionServiceException {
        if (clientRole != null) {
            DmaapPerm perm = new DmaapPerm(dmaapService.getTopicPerm(), instance, action);
            DmaapGrant g = new DmaapGrant(perm, clientRole);
            int code = aafService.addGrant(g);
            if (code != 201 && code != 409) {
                throw new PermissionServiceException(code, format("Grant of %s|%s|%s failed for %s",
                        dmaapService.getTopicPerm(), instance, action, clientRole));
            }
        } else {
            logger.warn("No Grant of {}|{}|{} because role is null ", dmaapService.getTopicPerm(), instance, action);
        }
    }

    private ApiError handleErrorStatus(int code, MR_Client client, String message) {
        ApiError apiError = new ApiError(code, message);
        client.setStatus(DmaapObject_Status.INVALID);
        logger.warn(apiError.getMessage());
        return apiError;
    }

    private ApiError handleOkStatus(MR_Client client) {
        client.setStatus(DmaapObject_Status.VALID);
        return new ApiError(200, "OK");
    }

    private class PermissionServiceException extends Exception {
        private final int code;
        private final String message;

        PermissionServiceException(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    @FunctionalInterface
    interface PermissionUpdate {
        void execute(String clientRole, String instance, String action) throws PermissionServiceException;
    }
}
