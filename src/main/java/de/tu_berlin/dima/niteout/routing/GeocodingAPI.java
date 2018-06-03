package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Address;
import de.tu_berlin.dima.niteout.routing.model.Location;

import java.io.IOException;

/**
 * Created by aardila on 1/29/2017.
 */
public interface GeocodingAPI {

    Location getLocation(Address address) throws RoutingAPIException;
}
