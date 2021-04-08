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

package org.onap.dmaap.dbcapi.aaf;

import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;
import org.onap.dmaap.dbcapi.logging.DmaapbcLogMessageEnum;

import static java.lang.String.format;

public class AafServiceImpl extends BaseLoggingClass implements AafService {

    private static final int CREATED = 201;
    private static final int OK = 200;
    private static final String FORCE = "?force=true";
    private final String aafUrl;
    private final String identity;
    private final boolean useAAF;
    private final AafConnection aafConnection;

    AafServiceImpl(boolean useAaf, String aafUrl, String identity, AafConnection aafConnection) {
        this.useAAF = useAaf;
        this.aafUrl = aafUrl;
        this.identity = identity;
        this.aafConnection = aafConnection;
    }

    @Override
    public String getIdentity() {
        return identity;
    }

    @Override
    public int addPerm(DmaapPerm perm) {
        logger.info("entry: addPerm() ");
        return doPost(perm, "authz/perm", CREATED);
    }

    @Override
    public int delPerm(DmaapPerm perm, boolean force) {
        logger.info("entry: delPerm()");
        return doDelete(new AafEmpty(), format(
                "authz/perm/%s/%s/%s%s",
                perm.getPermission(), perm.getPtype(), perm.getAction(), force ? FORCE : ""), OK);
    }

    @Override
    public int addGrant(DmaapGrant grant) {
        logger.info("entry: addGrant() ");
        return doPost(grant, "authz/role/perm", CREATED);
    }

    @Override
    public int addUserRole(AafUserRole ur) {
        logger.info("entry: addUserRole() ");
        return doPost(ur, "authz/userRole", CREATED);
    }

    @Override
    public int addRole(AafRole role) {
        logger.info("entry: addRole() ");
        return doPost(role, "authz/role", CREATED);
    }

    @Override
    public int addNamespace(AafNamespace ns) {
        logger.info("entry: addNamespace() ");
        return doPost(ns, "authz/ns", CREATED);
    }

    @Override
    public int delNamespace(AafNamespace ns, boolean force) {
        logger.info("entry: delNamespace()");
        return doDelete(new AafEmpty(), format(
                "authz/ns/%s%s",
                ns.getName(), force ? FORCE : ""), OK);
    }

    private int doPost(AafObject obj, String uri, int expect) {
        int rc;
        logger.info("entry: doPost() ");
        String pURL = aafUrl + uri;
        logger.info("doPost: useAAF=" + useAAF);
        if (useAAF) {
            logger.info("doPost: " + obj.toJSON());
            rc = aafConnection.postAaf(obj, pURL);
        } else {
            rc = expect;
        }
        switch (rc) {
            case 401:
            case 403:
                errorLogger.error(DmaapbcLogMessageEnum.AAF_CREDENTIAL_ERROR, identity);
                break;
            case 409:
                logger.warn("Object for " + uri + " already exists. Possible conflict.");
                break;
            default:
                if (rc == expect) {
                    logger.info("expected response: " + rc);
                } else {
                    logger.error("Unexpected response: " + rc);
                }
                break;
        }

        return rc;
    }

    private int doDelete(AafObject obj, String uri, int expect) {
        int rc;
        String pURL = aafUrl + uri;
        if (useAAF) {
            logger.info("doDelete: " + obj.toJSON());
            rc = aafConnection.delAaf(obj, pURL);
        } else {
            rc = expect;
        }
        switch (rc) {
            case 401:
            case 403:
                errorLogger.error(DmaapbcLogMessageEnum.AAF_CREDENTIAL_ERROR, identity);
                break;
            case 404:
                logger.warn("Object not found...ignore");
                break;
            case OK:
                logger.info("expected response");
                break;
            default:
                logger.error("Unexpected response: " + rc);
                break;
        }

        return rc;
    }

    String getAafUrl() {
        return aafUrl;
    }

    boolean isUseAAF() {
        return useAAF;
    }

}