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
package org.onap.dmaap.dbcapi.resources;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.att.eelf.configuration.EELFLogger;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RequestTimeLogFilterTest {

    private Clock clock ;
    private RequestTimeLogFilter requestTimeLogFilter;
    public static final long START = 1L;
    @Mock
    private ContainerRequestContext requestContext;
    @Mock
    private ContainerResponseContext responseContext;
    @Mock
    private EELFLogger logger;


    @Before
    public void setup() {
        clock = Clock.fixed(Instant.parse("1970-01-01T00:00:10Z"), ZoneId.systemDefault());
        requestTimeLogFilter = new RequestTimeLogFilter(logger, clock);
    }

    @Test
    public void shouldHaveDefaultConstructor() {
        assertNotNull(new RequestTimeLogFilter());
    }

    @Test
    public void filterShouldSetStartTimestampProperty() {
        requestTimeLogFilter.filter(requestContext);
        verify(requestContext).setProperty("start",clock.millis());
    }

    @Test
    public void filterShouldPrintElapsedTime() {
        when(requestContext.getProperty("start")).thenReturn(START);

        requestTimeLogFilter.filter(requestContext, responseContext);

        verify(logger).info(anyString(),eq(clock.millis() - START));
    }
}