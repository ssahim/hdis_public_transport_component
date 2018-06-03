package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.Route;
import de.tu_berlin.dima.niteout.routing.model.RouteSummary;
import de.tu_berlin.dima.niteout.routing.model.TimeMatrixEntry;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicTransportWrapper {

    /**
     * Get the amount of time in seconds to travel between two locations with, or walking if public
     * transport is not suitable.
     * @param start the location where the trip starts
     * @param destination the location where the trip terminates
     * @param departure the date and time at which the trip starts
     * @return the total number of seconds required for the trip
     */
    int getPublicTransportTripTime(Location start, Location destination, LocalDateTime departure) throws
            RoutingAPIException;

    /**
     * Gets the summary details about a route between two locations with public transport, or walking if public
     * transport is not suitable.
     * @param start the location where the trip starts
     * @param destination the location where the trip terminates
     * @param departure the date and time at which the trip starts
     * @return a RouteSummary with the details about the route
     */
    RouteSummary getPublicTransportRouteSummary(Location start, Location destination, LocalDateTime departure)
            throws RoutingAPIException;

    /**
     * Gets a matrix with trip information between start and destination locations using public transport,
     * or walking if public transport is not suitable.
     * @param startLocations the locations where the trips start
     * @param destinationLocations the locations where the trips terminate
     * @param departureTime the date and time at which the trips start
     * @return a matrix with trip information between start and destination locations
     */
    List<TimeMatrixEntry> getMultiModalMatrix(Location[] startLocations, Location[] destinationLocations,
                                              LocalDateTime departureTime) throws RoutingAPIException;
}
