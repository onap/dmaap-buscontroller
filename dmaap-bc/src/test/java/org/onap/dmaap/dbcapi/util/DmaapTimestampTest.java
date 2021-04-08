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

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class DmaapTimestampTest {

    private DmaapTimestamp dmaapTimestamp = new DmaapTimestamp();

    @Test
    public void mark_shouldUpdateTimestamp() {
        dmaapTimestamp = new DmaapTimestamp(new Date(10));
        Date timestamp = dmaapTimestamp.getVal();

        dmaapTimestamp.mark();

        assertTrue(timestamp.before(dmaapTimestamp.getVal()));
    }
}