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
import org.onap.dmaap.dbcapi.model.DcaeLocation;
import org.onap.dmaap.dbcapi.model.DmaapObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class DcaeLocationServiceTest {

    private static final String LOCATION_A = "locationA";
    private static final String LOCATION_B = "locationB";
    private DcaeLocationService locationService = new DcaeLocationService(new HashMap<>());

    @Test
    public void getAllDcaeLocations_shouldReturnEmptyCollection() {

        List<DcaeLocation> allDcaeLocations = locationService.getAllDcaeLocations();

        assertTrue(allDcaeLocations.isEmpty());
    }

    @Test
    public void addDcaeLocation_shouldAddLocationToMap() {
        DcaeLocation locationA = createDcaeLocation(LOCATION_A);

        DcaeLocation addedLocation = locationService.addDcaeLocation(locationA);

        assertEquals(locationA, locationService.getDcaeLocation(LOCATION_A));
        assertSame(locationA, addedLocation);
    }

    @Test
    public void addDcaeLocation_shouldSetStatusAndLastModDate() {
        DcaeLocation locationA = createDcaeLocation(LOCATION_A);
        Date creationDate = new Date(10);
        locationA.setLastMod(creationDate);

        DcaeLocation addedLocation = locationService.addDcaeLocation(locationA);

        assertTrue(addedLocation.getLastMod().after(creationDate));
        assertEquals(DmaapObject.DmaapObject_Status.VALID, addedLocation.getStatus());
    }

    @Test
    public void updateDcaeLocation_shouldUpdateLocationAndLastModDate() {
        DcaeLocation location = createDcaeLocation(LOCATION_A);
        Date creationDate = new Date(10);
        location.setLastMod(creationDate);
        locationService.addDcaeLocation(location);

        DcaeLocation updatedLocation = locationService.updateDcaeLocation(location);

        assertTrue(updatedLocation.getLastMod().after(creationDate));
        assertSame(location, updatedLocation);
    }

    @Test
    public void updateDcaeLocation_shouldShouldReturnNullWhenLocationNameIsEmpty() {
        DcaeLocation location = createDcaeLocation("");

        DcaeLocation updatedLocation = locationService.updateDcaeLocation(location);

        assertNull(updatedLocation);
        assertTrue(locationService.getAllDcaeLocations().isEmpty());
    }

    @Test
    public void removeDcaeLocation_shouldRemoveLocationFromService() {
        locationService.addDcaeLocation(createDcaeLocation(LOCATION_A));

        locationService.removeDcaeLocation(LOCATION_A);

        assertTrue(locationService.getAllDcaeLocations().isEmpty());
    }

    @Test
    public void getCentralLocation_shouldGetFirstCentralLocation() {
        locationService.addDcaeLocation(createDcaeLocation(LOCATION_A, "layerA"));
        locationService.addDcaeLocation(createDcaeLocation(LOCATION_B, "centralLayer"));

        assertEquals(LOCATION_B, locationService.getCentralLocation());
    }

    @Test
    public void getCentralLocation_shouldReturnDefaultCentralLocationNameWhenThereIsNoCentralLocation() {
        locationService.addDcaeLocation(createDcaeLocation(LOCATION_A, "layerA"));

        assertEquals("aCentralLocation", locationService.getCentralLocation());
    }

    @Test
    public void isEdgeLocation_shouldReturnTrueForNotCentralLocation() {
        locationService.addDcaeLocation(createDcaeLocation(LOCATION_A, "layerA"));
        locationService.addDcaeLocation(createDcaeLocation(LOCATION_B, "centralLayer"));

        assertTrue(locationService.isEdgeLocation(LOCATION_A));
        assertFalse(locationService.isEdgeLocation(LOCATION_B));
    }

    @Test
    public void isEdgeLocation_shouldReturnFalseWhenLocationDoesNotExist() {
        locationService.addDcaeLocation(createDcaeLocation(LOCATION_A, "layerA"));

        assertFalse(locationService.isEdgeLocation("not_existing_location"));
    }

    private DcaeLocation createDcaeLocation(String locationName) {
        return createDcaeLocation(locationName, "dcaeLayer");
    }

    private DcaeLocation createDcaeLocation(String locationName, String dcaeLayer) {
        return new DcaeLocation("clli", dcaeLayer, locationName, "openStackAvailabilityZone", "subnet");
    }


}
