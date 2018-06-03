package de.tu_berlin.dima.niteout.routing;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;

import de.tu_berlin.dima.niteout.routing.model.RouteSummary;
import de.tu_berlin.dima.niteout.routing.model.TransportMode;

/**
 * Test class for {@link RoutingService}.
 */
public class RoutingServiceTest {

    @Test
    public void testGetWalkingTripTime() {
        final RoutingService fixture = new RoutingService();
        int tripTime = 0;
        try {
            tripTime = fixture.getTripTime(TransportMode.WALKING,
                    LocationDirectory.TU_BERLIN, LocationDirectory.SIEGESSAEULE,
                    LocalDateTime.now());
        } catch (RoutingAPIException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        Assert.assertTrue(tripTime > 0);
    }

    @Test
    public void testGetWalkingRouteSummary() {

        final RoutingService fixture = new RoutingService();
        final LocalDateTime now = LocalDateTime.now();

        try {
            RouteSummary routeSummary = fixture.getRouteSummary(
                    TransportMode.WALKING,
                    LocationDirectory.TU_BERLIN, LocationDirectory.POTSDAMER_PLATZ,
                    now);
            Assert.assertNotNull(routeSummary);
            Assert.assertNotEquals(0, routeSummary.getTotalDuration());
            Assert.assertNotEquals(0, routeSummary.getTotalDistance());
            //check that the departure time is within 1 minute of what we specified
            Assert.assertTrue(MINUTES.between(routeSummary.getDepartureTime(), now) <= 1);
            Assert.assertNotNull(routeSummary.getArrivalTime());
            Assert.assertTrue(routeSummary.getArrivalTime().isAfter(now));
            //check that the departure time plus the duration is about the same as the arrival time
            Assert.assertTrue(
            		MINUTES.between(now.plusSeconds(routeSummary.getTotalDuration()),routeSummary.getArrivalTime()) <= 1);
        } catch (RoutingAPIException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}