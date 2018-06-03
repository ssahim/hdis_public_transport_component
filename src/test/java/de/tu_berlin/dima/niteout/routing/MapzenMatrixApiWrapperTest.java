package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.TimeMatrixEntry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Test class for {@link MapzenMatrixApiWrapper}
 * @author Andres Ardila
 */
public class MapzenMatrixApiWrapperTest {


    private final String apiKey = System.getProperty("API_KEY_MAPZEN");
    private MapzenMatrixApiWrapper fixture;

    @Before
    public void setUpMapzenWrapper() {
        try {
            fixture = new MapzenMatrixApiWrapper(this.apiKey);
        } catch (RoutingAPIException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testOneToManyMatrix() {

        Location start = LocationDirectory.TU_BERLIN;
        Location[] destinations =
        {
            LocationDirectory.POTSDAMER_PLATZ,
            LocationDirectory.BRANDENBURGER_TOR,
            LocationDirectory.SIEGESSAEULE,
            LocationDirectory.getRandomLocationInBerlin()
        };

        try {
            List<TimeMatrixEntry> matrix = fixture.getWalkingMatrix(start, destinations);
            Assert.assertEquals(destinations.length, matrix.size());

        } catch (RoutingAPIException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testManyToOneMatrix() {

        Location[] startLocations =
                {
                        LocationDirectory.POTSDAMER_PLATZ,
                        LocationDirectory.BRANDENBURGER_TOR,
                        LocationDirectory.SIEGESSAEULE,
                        LocationDirectory.getRandomLocationInBerlin()
                };
        Location destination = LocationDirectory.TU_BERLIN;

        try {
            List<TimeMatrixEntry> matrix = fixture.getWalkingMatrix(startLocations, destination);
            Assert.assertEquals(startLocations.length, matrix.size());

        } catch (RoutingAPIException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testManyToManyMatrix() {

        Location[] locations =
                {
                        LocationDirectory.TU_BERLIN,
                        LocationDirectory.POTSDAMER_PLATZ,
                        LocationDirectory.BRANDENBURGER_TOR,
                        LocationDirectory.SIEGESSAEULE,
                        LocationDirectory.HAUPTBAHNHOF,
                        LocationDirectory.ALEXANDERPLATZ
                };

        try {
            List<TimeMatrixEntry> matrix = fixture.getWalkingMatrix(locations);
            Assert.assertEquals((int)Math.pow(locations.length, 2), matrix.size());

        } catch (RoutingAPIException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testSourcesToTargetsMatrix() {

        Location[] startLocations =
                {
                        LocationDirectory.TU_BERLIN,
                        LocationDirectory.HAUPTBAHNHOF,
                        LocationDirectory.ALEXANDERPLATZ
                };

        Location[] destinations =
                {
                        LocationDirectory.POTSDAMER_PLATZ,
                        LocationDirectory.BRANDENBURGER_TOR,
                        LocationDirectory.SIEGESSAEULE
                };

        try {
            List<TimeMatrixEntry> matrix = fixture.getWalkingMatrix(startLocations, destinations);
            Assert.assertEquals(startLocations.length * destinations.length, matrix.size());

        } catch (RoutingAPIException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
