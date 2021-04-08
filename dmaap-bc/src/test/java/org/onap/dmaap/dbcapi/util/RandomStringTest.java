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

package org.onap.dmaap.dbcapi.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RandomStringTest {

    private static final int LENGTH = 10;
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private RandomString randomString = new RandomString(LENGTH);

    @Test
    public void nextString_shouldReturnStringWithGivenLength() {

        String nextString = randomString.nextString();

        assertEquals(LENGTH, nextString.length());
    }

    @Test
    public void nextString_shouldReturnAlphanumeric() {

        String nextString = randomString.nextString();

        assertTrue(nextString.matches("[a-z0-9]*"));
    }

    @Test
    public void constructor_shouldThrowExceptionForNegativeLength() {

        thrown.expect(IllegalArgumentException.class);

        new RandomString(-1);
    }
}