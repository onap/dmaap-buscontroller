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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CredentialsParserTest {

    private CredentialsParser credentialsParser = new CredentialsParser();

    @Test
    public void parse_shouldReturnEmptyCredentialsWhenAuthorizationHeaderIsNull() {

        Credentials credentials = credentialsParser.parse(null);

        assertTrue(credentials.getId().isEmpty());
        assertTrue(credentials.getPwd().isEmpty());
    }

    @Test
    public void parse_shouldReturnEmptyCredentialsWhenAuthorizationHeaderIsEmpty() {

        Credentials credentials = credentialsParser.parse("");

        assertTrue(credentials.getId().isEmpty());
        assertTrue(credentials.getPwd().isEmpty());
    }

    @Test
    public void parse_shouldParseCorrectCredentials() {

        Credentials credentials = credentialsParser.parse("Basic dXNlcjpwYXNzd29yZA==");

        assertEquals("user", credentials.getId());
        assertEquals("password", credentials.getPwd());
    }
}