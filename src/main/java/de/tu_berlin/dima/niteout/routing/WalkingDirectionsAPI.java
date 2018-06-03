package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.RouteSummary;
import de.tu_berlin.dima.niteout.routing.model.TimeMatrixEntry;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by aardila on 1/29/2017.
 */
public interface WalkingDirectionsAPI {

    int getWalkingTripTime(Location startLocation,
                           Location destinationLocation) throws RoutingAPIException;

    int getWalkingTripTime(Location startLocation,
                           Location destinationLocation,
                           LocalDateTime startTime) throws RoutingAPIException;

    RouteSummary getWalkingRouteSummary(Location start, Location destination) throws RoutingAPIException;

    RouteSummary getWalkingRouteSummary(Location start, Location destination,
                                        LocalDateTime departureTime) throws RoutingAPIException;

    List<TimeMatrixEntry> getWalkingMatrix(Location[] startLocations,
                                           Location[] destinationLocations) throws RoutingAPIException;
}
