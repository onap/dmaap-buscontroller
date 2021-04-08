/*-
 * ============LICENSE_START=======================================================
 *  org.onap.dmaap
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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
package org.onap.dmaap.dbcapi.model;

import ch.qos.logback.classic.Logger;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.onap.dmaap.dbcapi.logging.BaseLoggingClass;

public class TestRunner extends BaseLoggingClass {

    private static Logger logger;

    public static void main(String[] args) {

        Result result = JUnitCore.runClasses(JUnitTestSuite.class);
        for (Failure failure : result.getFailures()) {
            logger.info(failure.toString());

        }
        logger.info(String.valueOf(result.wasSuccessful()));
    }
}
