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

import javax.xml.bind.DatatypeConverter;

class CredentialsParser {

    Credentials parse(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            return Credentials.empty();
        }

        String credentials = authorizationHeader.substring("Basic".length()).trim();
        byte[] decoded = DatatypeConverter.parseBase64Binary(credentials);
        String decodedString = new String(decoded);
        String[] actualCredentials = decodedString.split(":");
        return new Credentials(actualCredentials[0], actualCredentials[1]);
    }

}
