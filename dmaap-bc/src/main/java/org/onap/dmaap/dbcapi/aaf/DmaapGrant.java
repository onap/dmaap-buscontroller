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

import java.util.Objects;

public class DmaapGrant extends AafObject {

    private DmaapPerm perm;
    private String role;

    public DmaapGrant() {

    }

    public DmaapGrant(DmaapPerm p, String r) {
        this.perm = p;
        this.role = r;
    }

    public DmaapPerm getPerm() {
        return perm;
    }

    public void setPerm(DmaapPerm perm) {
        this.perm = perm;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String toJSON() {

        String postJSON = String.format(" { \"perm\":  %s, \"role\": \"%s\"}",
                this.perm.toJSON(),
                this.getRole());
        logger.info("returning JSON: " + postJSON);

        return postJSON;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DmaapGrant that = (DmaapGrant) o;
        return Objects.equals(perm, that.perm) &&
                Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {

        return Objects.hash(perm, role);
    }
}
