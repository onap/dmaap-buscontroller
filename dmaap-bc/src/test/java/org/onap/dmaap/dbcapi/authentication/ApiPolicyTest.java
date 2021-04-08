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

package org.onap.dmaap.dbcapi.authentication;

import org.junit.Test;
import org.onap.dmaap.dbcapi.aaf.DmaapPerm;

import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ApiPolicyTest {

    private Properties properties = new Properties();
    private ApiPolicy apiPolicy;

    @Test
    public void check_shouldExecuteAuthorizationApi() throws Exception {
        properties.put("ApiPermission.Class", "org.onap.dmaap.dbcapi.authentication.ApiPolicyTest$DummyApiAuthorization");
        apiPolicy = new ApiPolicy(properties);

        apiPolicy.check("mechId", "pwd", new DmaapPerm("api.perm", "*", "GET"));

        assertTrue(((DummyApiAuthorization) apiPolicy.getPerm()).isCheckExecuted());
    }

    @Test
    public void isPermissionClassSet_shouldReturnTrueForValidApiPermClass() {
        properties.put("ApiPermission.Class", "org.onap.dmaap.dbcapi.authentication.ApiPolicyTest$DummyApiAuthorization");
        apiPolicy = new ApiPolicy(properties);

        assertTrue(apiPolicy.isPermissionClassSet());
    }

    @Test
    public void isPermissionClassSet_shouldReturnFalseWhenPropertyIsNotSet() {
        apiPolicy = new ApiPolicy(properties);

        assertFalse(apiPolicy.isPermissionClassSet());
    }

    @Test
    public void isPermissionClassSet_shouldReturnFalseWhenWrongClassIsSet() {
        properties.put("ApiPermission.Class", "org.onap.dmaap.dbcapi.authentication.NotExisting");
        apiPolicy = new ApiPolicy(properties);

        assertFalse(apiPolicy.isPermissionClassSet());
    }

    public static class DummyApiAuthorization implements ApiAuthorizationCheckInterface {

        private boolean checkExecuted = false;

        @Override
        public void check(String mechid, String pwd, DmaapPerm p) {
            checkExecuted = true;
        }

        boolean isCheckExecuted() {
            return checkExecuted;
        }
    }
}