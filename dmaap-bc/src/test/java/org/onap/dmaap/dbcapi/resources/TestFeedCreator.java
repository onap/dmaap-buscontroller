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

import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.onap.dmaap.dbcapi.model.Feed;


public class TestFeedCreator {


    private final FastJerseyTestContainer testContainer;

    public TestFeedCreator(FastJerseyTestContainer testContainer) {
        this.testContainer = testContainer;
    }

    Feed addFeed(String name, String desc) {
        Feed feed = new Feed(name, "1.0", desc, "dgl", "unrestricted");
        Entity<Feed> reqEntity = Entity.entity(feed, MediaType.APPLICATION_JSON);
        Response resp = testContainer.target("feeds").request().post(reqEntity, Response.class);
        int rc = resp.getStatus();
        System.out.println("POST feed resp=" + rc);
        assertTrue(rc == 200 || rc == 409);
        feed = resp.readEntity(Feed.class);
        return feed;
    }
}
