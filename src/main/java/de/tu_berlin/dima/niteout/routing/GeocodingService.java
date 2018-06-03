package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Address;
import de.tu_berlin.dima.niteout.routing.model.Location;


/**
 * Created by aardila on 1/29/2017.
 */
public class GeocodingService implements GeocodingAPI {

    GeocodingAPI provider = new MapzenSearchApiWrapper(System.getProperty("API_KEY_MAPZEN"));

    @Override
    public Location getLocation(Address address) throws RoutingAPIException {
        return provider.getLocation(address);
    }
}
