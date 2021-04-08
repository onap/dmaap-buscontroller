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

package org.onap.dmaap.dbcapi.service;

import org.onap.dmaap.dbcapi.database.DatabaseClass;
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.DmaapObject.DmaapObject_Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DcaeLocationService {

    private static final String DEFAULT_CENTRAL_LOCATION = "aCentralLocation"; // default value that is obvious to see is wrong
    private final Map<String, DcaeLocation> dcaeLocations;

    public DcaeLocationService() {
        this(DatabaseClass.getDcaeLocations());
    }

    DcaeLocationService(Map<String, DcaeLocation> dcaeLocations) {
        this.dcaeLocations = dcaeLocations;
    }

    public List<DcaeLocation> getAllDcaeLocations() {
        return new ArrayList<>(dcaeLocations.values());
    }

    public DcaeLocation getDcaeLocation(String name) {
        return dcaeLocations.get(name);
    }

    public DcaeLocation addDcaeLocation(DcaeLocation location) {
        location.setLastMod();
        location.setStatus(DmaapObject_Status.VALID);
        dcaeLocations.put(location.getDcaeLocationName(), location);
        return location;
    }

    public DcaeLocation updateDcaeLocation(DcaeLocation location) {
        if (location.getDcaeLocationName().isEmpty()) {
            return null;
        }
        location.setLastMod();
        dcaeLocations.put(location.getDcaeLocationName(), location);
        return location;
    }

    public DcaeLocation removeDcaeLocation(String locationName) {
        return dcaeLocations.remove(locationName);
    }

    String getCentralLocation() {

        Optional<DcaeLocation> firstCentralLocation =
                dcaeLocations.values().stream().filter(DcaeLocation::isCentral).findFirst();

        return firstCentralLocation.isPresent() ? firstCentralLocation.get().getDcaeLocationName() : DEFAULT_CENTRAL_LOCATION;
    }

    boolean isEdgeLocation(String aName) {
        return dcaeLocations.get(aName) != null && !dcaeLocations.get(aName).isCentral();
    }

}
