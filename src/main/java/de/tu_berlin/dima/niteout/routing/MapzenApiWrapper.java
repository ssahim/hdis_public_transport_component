package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.RouteSummary;
import de.tu_berlin.dima.niteout.routing.model.TimeMatrixEntry;

import java.time.LocalDateTime;
import java.util.List;

/**
 * An implementation of the {@link WalkingDirectionsAPI} as a facade to other Mapzen APIs (like Mobility and Matrix)
 * @author Andres Ardila
 */
class MapzenApiWrapper implements WalkingDirectionsAPI {

    private final String apiKey;

    public MapzenApiWrapper(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public int getWalkingTripTime(Location start, Location destination) throws RoutingAPIException {
        return getWalkingTripTime(start, destination, null);
    }

    @Override
    public int getWalkingTripTime(Location start, Location destination,
                                  LocalDateTime departureTime) throws RoutingAPIException {
        MapzenMobilityApiWrapper mobilityWrapper = new MapzenMobilityApiWrapper(apiKey);
        return departureTime == null ?
                mobilityWrapper.getWalkingTripTime(start, destination) :
                mobilityWrapper.getWalkingTripTime(start, destination, departureTime);
    }

    @Override
    public RouteSummary getWalkingRouteSummary(Location start, Location destination) throws RoutingAPIException {
        return getWalkingRouteSummary(start, destination, null);
    }

    @Override
    public RouteSummary getWalkingRouteSummary(Location start, Location destination,
                                               LocalDateTime departureTime) throws RoutingAPIException {
        MapzenMobilityApiWrapper mobilityWrapper = new MapzenMobilityApiWrapper(apiKey);
        return departureTime == null ?
                mobilityWrapper.getWalkingRouteSummary(start, destination) :
                mobilityWrapper.getWalkingRouteSummary(start, destination, departureTime);
    }

    @Override
    public List<TimeMatrixEntry> getWalkingMatrix(Location[] startLocations,
                                                  Location[] destinationLocations) throws RoutingAPIException {

        MapzenMatrixApiWrapper matrixWrapper = new MapzenMatrixApiWrapper(this.apiKey);
        return matrixWrapper.getWalkingMatrix(startLocations, destinationLocations);
    }
}
