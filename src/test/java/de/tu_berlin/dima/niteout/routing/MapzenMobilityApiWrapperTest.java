package de.tu_berlin.dima.niteout.routing;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for {@link MapzenMobilityApiWrapper}.
 */
public class MapzenMobilityApiWrapperTest {

    private final String apiKey = System.getProperty("API_KEY_MAPZEN");
    private MapzenMobilityApiWrapper fixture;

    @Before
    public void setUpMapzenWrapper() {
        try {
            fixture = new MapzenMobilityApiWrapper(this.apiKey);
        } catch (RoutingAPIException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getWalkingTripTime() {

        int tripDuration = 0;

        try {
            tripDuration = fixture.getWalkingTripTime(LocationDirectory.TU_BERLIN, LocationDirectory.HAUPTBAHNHOF);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        Assert.assertNotEquals(tripDuration, -1);
    }

    @Test
    public void getPublicTransportTripTime() {

        int tripDuration = 0;

        try {
            tripDuration = fixture.getPublicTransportTripTime(
                    LocationDirectory.TU_BERLIN,
                    LocationDirectory.HAUPTBAHNHOF,
                    LocalDateTime.now());
            Assert.fail("should not reach this point as no public transport is implemented");
        } catch (Exception e) {
            assertTrue(e instanceof RoutingAPIException);
            assertEquals(RoutingAPIException.ErrorCode.API_ERROR_BAD_REQUEST, ((RoutingAPIException) e).getCode());
        }

        Assert.assertNotEquals(tripDuration, -1);
    }
}
