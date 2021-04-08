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

import org.onap.dmaap.dbcapi.aaf.AafNamespace;
import org.onap.dmaap.dbcapi.aaf.AafRole;
import org.onap.dmaap.dbcapi.aaf.AafService;
import org.onap.dmaap.dbcapi.aaf.DmaapGrant;
import org.onap.dmaap.dbcapi.aaf.DmaapPerm;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.model.ApiError;
import org.onap.dmaap.dbcapi.model.Topic;
import org.onap.dmaap.dbcapi.util.DmaapConfig;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNumeric;

class AafTopicSetupService extends BaseLoggingClass {

    private final AafService aafService;
    private final DmaapService dmaapService;
    private final DmaapConfig dmaapConfig;

    AafTopicSetupService(AafService aafService, DmaapService dmaapService, DmaapConfig dmaapConfig) {
        this.aafService = aafService;
        this.dmaapService = dmaapService;
        this.dmaapConfig = dmaapConfig;
    }

    ApiError aafTopicSetup(Topic topic) {

        try {
            String instance = ":topic." + topic.getFqtn();
            String topicPerm = dmaapService.getTopicPerm();
            DmaapPerm pubPerm = createPermission(topicPerm, instance, "pub");
            DmaapPerm subPerm = createPermission(topicPerm, instance, "sub");
            DmaapPerm viewPerm = createPermission(topicPerm, instance, "view");

            // creating Topic Roles was not an original feature.
            // For backwards compatibility, only do this if the feature is enabled.
            // Also, if the namespace of the topic is a foreign namespace, (i.e. not the same as our root ns)
            // then we likely don't have permission to create sub-ns and Roles so don't try.
            if (createTopicRoles() && topic.getFqtn().startsWith(getTopicsNsRoot())) {
                createNamespace(topic);

                AafRole pubRole = createRole(topic, "publisher");
                topic.setPublisherRole(pubRole.getFullyQualifiedRole());

                AafRole subRole = createRole(topic, "subscriber");
                topic.setSubscriberRole(subRole.getFullyQualifiedRole());

                grantPermToRole(pubRole, pubPerm);
                grantPermToRole(pubRole, viewPerm);

                grantPermToRole(subRole, subPerm);
                grantPermToRole(subRole, viewPerm);
            }

        } catch (TopicSetupException ex) {
            logger.error("Exception in topic setup {}", ex.getMessage());
            return new ApiError(ex.getCode(), ex.getMessage(), ex.getFields());
        }
        return okStatus();
    }

    ApiError aafTopicCleanup(Topic topic) {
        try {
            if (performCleanup()) {
                String instance = ":topic." + topic.getFqtn();
                String topicPerm = dmaapService.getTopicPerm();
                removePermission(topicPerm, instance, "pub");
                removePermission(topicPerm, instance, "sub");
                removePermission(topicPerm, instance, "view");

                if (createTopicRoles() && topic.getFqtn().startsWith(getTopicsNsRoot())) {
                    removeNamespace(topic);
                }
            }
        } catch (TopicSetupException ex) {
            return new ApiError(ex.getCode(), ex.getMessage(), ex.getFields());
        }
        return okStatus();
    }

    private String getTopicsNsRoot() throws TopicSetupException {
        String nsr = dmaapService.getDmaap().getTopicNsRoot();
        if (nsr == null) {
            throw new TopicSetupException(500,
                    "Unable to establish AAF namespace root: (check /dmaap object)", "topicNsRoot");
        }
        return nsr;
    }

    private DmaapPerm createPermission(String permission, String instance, String action) throws TopicSetupException {
        DmaapPerm perm = new DmaapPerm(permission, instance, action);
        int rc = aafService.addPerm(perm);
        if (rc != 201 && rc != 409) {
            throw new TopicSetupException(500,
                    format("Unexpected response from AAF: %d permission=%s instance=%s action=%s",
                            rc, perm, instance, action));
        }
        return perm;
    }

    private void grantPermToRole(AafRole aafRole, DmaapPerm perm) throws TopicSetupException {
        DmaapGrant g = new DmaapGrant(perm, aafRole.getFullyQualifiedRole());
        int rc = aafService.addGrant(g);
        if (rc != 201 && rc != 409) {
            String message = format("Grant of %s failed for %s", perm.toString(), aafRole.getFullyQualifiedRole());
            logger.warn(message);
            throw new TopicSetupException(rc, message);
        }
    }

    private void createNamespace(Topic topic) throws TopicSetupException {
        AafNamespace ns = new AafNamespace(topic.getFqtn(), aafService.getIdentity());
        int rc = aafService.addNamespace(ns);
        if (rc != 201 && rc != 409) {
            throw new TopicSetupException(500,
                    format("Unexpected response from AAF: %d namespace=%s identity=%s",
                            rc, topic.getFqtn(), aafService.getIdentity()));
        }
    }

    private AafRole createRole(Topic topic, String roleName) throws TopicSetupException {
        AafRole role = new AafRole(topic.getFqtn(), roleName);
        int rc = aafService.addRole(role);
        if (rc != 201 && rc != 409) {
            throw new TopicSetupException(500,
                    format("Unexpected response from AAF: %d topic=%s role=%s",
                            rc, topic.getFqtn(), roleName));
        }
        return role;
    }

    private void removePermission(String permission, String instance, String action) throws TopicSetupException {
        DmaapPerm perm = new DmaapPerm(permission, instance, action);
        int rc = aafService.delPerm(perm, true);
        if (rc != 200 && rc != 404) {
            throw new TopicSetupException(500,
                    format("Unexpected response from AAF: %d permission=%s instance=%s action=%s",
                            rc, perm, instance, action));
        }
    }

    private void removeNamespace(Topic topic) throws TopicSetupException {
        AafNamespace ns = new AafNamespace(topic.getFqtn(), aafService.getIdentity());
        int rc = aafService.delNamespace(ns, true);
        if (rc != 200 && rc != 404) {
            throw new TopicSetupException(500,
                    format("Unexpected response from AAF: %d namespace=%s identity=%s",
                            rc, topic.getFqtn(), aafService.getIdentity()));
        }
    }

    private ApiError okStatus() {
        return new ApiError(200, "OK");
    }

    private boolean createTopicRoles() {
        return "true".equalsIgnoreCase(dmaapConfig.getProperty("aaf.CreateTopicRoles", "true"));
    }

    private boolean performCleanup() {
        String deleteLevel = dmaapConfig.getProperty("MR.ClientDeleteLevel", "0");
        if (!isNumeric(deleteLevel)) {
            return false;
        }
        return Integer.valueOf(deleteLevel) >= 2;
    }

    private class TopicSetupException extends Exception {

        private final int code;
        private final String message;
        private final String fields;

        TopicSetupException(int code, String message) {
            this(code, message, "");
        }

        TopicSetupException(int code, String message, String fields) {
            this.code = code;
            this.message = message;
            this.fields = fields;
        }

        public int getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }

        public String getFields() {
            return fields;
        }
    }
}
