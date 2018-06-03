package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Address;
import de.tu_berlin.dima.niteout.routing.model.Location;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.LinkedHashMap;

/**
 * A wrapper for the Mapzen Search API.
 * NOTE: This wrapper only implements the a subset of the functionality offered by the API
 * @author Andres Ardila
 * see also https://mapzen.com/products/search/
 * 
 */
class MapzenSearchApiWrapper extends MapzenApi implements GeocodingAPI {

    private final String endpoint = "v1/search/structured";

    protected MapzenSearchApiWrapper(String apiKey) {
        super("search", apiKey);
    }

    @Override
    public Location getLocation(Address address) throws RoutingAPIException {
        LinkedHashMap<String, String> queryString = new LinkedHashMap<>();
        queryString.put("address", address.getStreet() + " " + address.getHouseNumber());
        queryString.put("locality", address.getCity());
        queryString.put("postalcode", address.getPostalCode());
        queryString.put("country", "DE");
        queryString.put("size", "1");
        queryString.put("api_key", super.apiKey);

        JsonObject responseJson = super.getResponse(endpoint, queryString);
        JsonArray features = responseJson.getJsonArray("features");

        Location location = null;

        if (features.size() > 0) {
            JsonObject feature = (JsonObject) features.get(0);
            JsonArray coordinates = feature.getJsonObject("geometry").getJsonArray("coordinates");
            location = new Location(
                    coordinates.getJsonNumber(1).doubleValue(),
                    coordinates.getJsonNumber(0).doubleValue());
        }

        return location;
    }
}
