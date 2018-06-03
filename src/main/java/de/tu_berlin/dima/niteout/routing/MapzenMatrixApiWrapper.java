package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.DistanceUnits;
import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.TimeMatrixEntry;
import de.tu_berlin.dima.niteout.routing.model.mapzen.CostingModel;
import de.tu_berlin.dima.niteout.routing.model.mapzen.MatrixType;
import de.tu_berlin.dima.niteout.routing.model.mapzen.Units;

import javax.json.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


/**
 * A wrapper for the Mapzen Matrix API
 * @author Andres Ardila
 */
class MapzenMatrixApiWrapper extends MapzenApi {

    private final Units MapzenDistanceUnits = Units.KM;
    private final DistanceUnits MatrixDistanceUnits = DistanceUnits.KILOMETERS;

    public MapzenMatrixApiWrapper(String apiKey) throws RoutingAPIException {

        super("matrix", apiKey);

        if (apiKey == null || apiKey.trim().length() == 0)
            throw new RoutingAPIException(RoutingAPIException.ErrorCode.API_CREDENTIALS_INVALID,
                    "The api key for mapzen was either empty or not set or could not accessed.");
    }

    /**
     * Gets a one-to-many time matrix between the starting location and all destinations
     * @param start the starting location
     * @param destinations the list of destinations
     * @return the time matrix
     */
    public List<TimeMatrixEntry> getWalkingMatrix(Location start, Location[] destinations) throws RoutingAPIException {

        JsonBuilder jsonBuilder = new JsonBuilder();
        jsonBuilder.addLocation(start);
        jsonBuilder.addLocations(destinations);
        JsonObject requestJsonObject = jsonBuilder.build(this.MapzenDistanceUnits);

        JsonObject response = super.getResponse(MatrixType.ONE_TO_MANY.getApiString(), requestJsonObject);

        JsonArray outerArray = response.getJsonArray(MatrixType.ONE_TO_MANY.getApiString());
        JsonArray innerArray = outerArray.getJsonArray(0);

        ArrayList<TimeMatrixEntry> out = new ArrayList<>(destinations.length);

        for (JsonValue value : innerArray) {
            JsonObject jsonObject = (JsonObject)value;
            int index = jsonObject.getInt("to_index");
            if (index == 0) { continue; } // skip 'from_index' : 0 'to_index' : 0 since it's the departure/start point

            TimeMatrixEntry entry = new TimeMatrixEntry(
                    0,
                    index - 1,
                    jsonObject.getInt("time"),
                    jsonObject.getJsonNumber("distance").doubleValue(),
                    MatrixDistanceUnits
            );
            out.add(entry);
        }

        return out;
    }

    public List<TimeMatrixEntry> getWalkingMatrix(
            Location[] startLocations, Location destinationLocation) throws RoutingAPIException {

        JsonBuilder jsonBuilder = new JsonBuilder();
        jsonBuilder.addLocations(startLocations);
        jsonBuilder.addLocation(destinationLocation);
        JsonObject requestJsonObject = jsonBuilder.build(this.MapzenDistanceUnits);

        JsonObject response = this.getResponse(MatrixType.MANY_TO_ONE.getApiString(), requestJsonObject);

        ArrayList<TimeMatrixEntry> out = new ArrayList<>(startLocations.length);

        for (JsonValue jsonValue : response.getJsonArray(MatrixType.MANY_TO_ONE.getApiString())) {
            JsonArray innerArray = (JsonArray)jsonValue;
            JsonObject jsonObject = innerArray.getJsonObject(0);
            int fromIndex = jsonObject.getInt("from_index");
            if (fromIndex >= startLocations.length) continue; //skip last combination ("from destination to destination")

            TimeMatrixEntry entry;
            try {
                entry = new TimeMatrixEntry(
                        fromIndex,
                        0,
                        jsonObject.getInt("time"),
                        jsonObject.getJsonNumber("distance").doubleValue(),
                        MatrixDistanceUnits);
            } catch (ClassCastException e) {
                throw new RoutingAPIException(RoutingAPIException.ErrorCode.PROCESS_RESPONSE_ERROR_JSON,
                        "could not cast JsonValue to JsonNumber when trying to get time and distance from json \n"
                        + jsonObject.toString());
            }
            out.add(entry);
        }
        return out;

    }

    public List<TimeMatrixEntry> getWalkingMatrix(Location[] startLocations, Location[] destinationLocations) throws
            RoutingAPIException {

        JsonArrayBuilder sourcesBuilder = Json.createArrayBuilder();
        JsonArrayBuilder targetsBuilder = Json.createArrayBuilder();
        for (Location source : startLocations) {
            sourcesBuilder.add(serializeLocation(source));
        }
        for (Location target : destinationLocations) {
            targetsBuilder.add(serializeLocation(target));
        }
        JsonObject requestJsonObject = Json.createObjectBuilder()
                .add("sources", sourcesBuilder.build())
                .add("targets", targetsBuilder.build())
                .add("costing", "pedestrian")
                .add("units", this.MapzenDistanceUnits.getApiString())
                .build();

        JsonObject response = null;
        response = this.getResponse(MatrixType.SOURCES_TO_TARGETS.getApiString(), requestJsonObject);

        JsonArray outerArray = response.getJsonArray(MatrixType.SOURCES_TO_TARGETS.getApiString());
        ArrayList<TimeMatrixEntry> out = new ArrayList<>();

        for (JsonValue innerJsonValue : outerArray) {
            JsonArray innerArray = (JsonArray)innerJsonValue;
            for (JsonValue value : innerArray) {
                JsonObject jsonObject = (JsonObject)value;
                TimeMatrixEntry entry = new TimeMatrixEntry(
                        jsonObject.getInt("from_index"),
                        jsonObject.getInt("to_index"),
                        jsonObject.getInt("time"),
                        jsonObject.getJsonNumber("distance").doubleValue(),
                        MatrixDistanceUnits
                );
                out.add(entry);
            }
        }

        return out;
    }

    private JsonObject serializeLocation(Location location) {
        return Json.createObjectBuilder()
                .add("lat", location.getLatitude())
                .add("lon", location.getLongitude())
                .build();
    }

    public List<TimeMatrixEntry> getWalkingMatrix(Location[] locations) throws RoutingAPIException {
        JsonBuilder jsonBuilder = new JsonBuilder();
        jsonBuilder.addLocations(locations);
        JsonObject requestJsonObject = jsonBuilder.build(this.MapzenDistanceUnits);

        JsonObject response = null;
        response = this.getResponse(MatrixType.MANY_TO_MANY.getApiString(), requestJsonObject);


        ArrayList<TimeMatrixEntry> out = new ArrayList<>();

        for (JsonValue innerValue : response.getJsonArray(MatrixType.MANY_TO_MANY.getApiString())) {
            for (JsonValue element : ((JsonArray)innerValue)) {
                JsonObject jsonObject = (JsonObject)element;
                TimeMatrixEntry entry = new TimeMatrixEntry(
                        jsonObject.getInt("from_index"),
                        jsonObject.getInt("to_index"),
                        jsonObject.getInt("time"),
                        jsonObject.getJsonNumber("distance").doubleValue(),
                        MatrixDistanceUnits
                );
                out.add(entry);
            }
        }

        return out;
    }

    private class JsonBuilder {

        javax.json.JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        public JsonArrayBuilder addLocation(Location location) {
            return arrayBuilder.add(getJsonObject(location));
        }

        public JsonArrayBuilder addLocations(Location[] locations) {
            for (Location location : locations) {
                addLocation(location);
            }

            return arrayBuilder;
        }

        private JsonObject getJsonObject(Location location) {
            return Json.createObjectBuilder()
                    .add("lat", location.getLatitude())
                    .add("lon", location.getLongitude())
                    .build();
        }

        public JsonObject build(Units units) {
            builder.add("locations", arrayBuilder.build())
                    .add("costing", CostingModel.PEDESTRIAN.getApiString())
                    .add("units", units.getApiString());

            return builder.build();
        }
    }
}
