package de.tu_berlin.dima.niteout.routing;

import com.google.common.util.concurrent.RateLimiter;
import de.tu_berlin.dima.niteout.routing.model.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonParsingException;
import java.io.IOException;
import java.io.Reader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static java.util.stream.Collectors.toList;

/*
 *
 *
 * API Wrapper that uses the HERE Api
 * www.here.com
 * Example API call:
 *
 * https://route.cit.api.here.com/routing/7.2/calculateroute.json
 *   ?app_id={YOUR_APP_ID}
 *   &app_code={YOUR_APP_CODE}
 *   &waypoint0=geo!52.530,13.326
 *   &waypoint1=geo!52.513,13.407
 *   &departure=now
 *   &mode=fastest;publicTransport
 *   &combineChange=true
 */

/**
 * The Wrapper for the here.com API which wraps the requesting and network logic and just returns simple objects of our
 * model that our Service can work with, to keep the dependencies of this API only inside this class.
 */
class HereWrapper implements PublicTransportWrapper {

    private final static String URL_MAIN = "https://route.cit.api.here.com/routing/7.2/calculateroute.json";
    private final static String URL_APP_ID = "app_id=%s";
    private final static String URL_APP_CODE = "app_code=%s";
    private final static String URL_START = "waypoint0=geo!%s,%s";
    private final static String URL_DESTINATION = "waypoint1=geo!%s,%s";
    private final static String URL_DEPARTURE = "departure=%s";
    private final static String URL_MODE = "mode=fastest;publicTransport";
    private final static String URL_COMBINE_CHANGE = "combineChange=true";
    private final static double MAX_API_RPS = 1;


    public final static DateTimeFormatter ISO_LOCAL_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final String apiId;
    private final String apiCode;

    private final RateLimiter rateLimiter = RateLimiter.create(MAX_API_RPS); // TODO fine-tune RPS value
    private final OkHttpClient httpClient = new OkHttpClient();

    public HereWrapper(String apiId, String apiCode) throws RoutingAPIException {
        if (apiId == null || apiId.trim().isEmpty() || apiCode == null || apiCode.trim().isEmpty()) {
            throw new RoutingAPIException(RoutingAPIException.ErrorCode.API_CREDENTIALS_INVALID,
                    "The api code or api id for here.com were either empty or not set or could not accessed.");
        }
        this.apiId = apiId;
        this.apiCode = apiCode;
    }

    @Override
    public int getPublicTransportTripTime(Location start, Location destination, LocalDateTime departure) throws RoutingAPIException {
        return getMatrixEntryForRouteArguments(0, 0, start, destination, departure).getTime();
    }

    public List<TimeMatrixEntry> getMultiModalMatrix(Location[] startLocations, Location[] destinationLocations,
                                                     LocalDateTime departureTime) throws RoutingAPIException {

        // Parallelize all start locations, map each of them to all destination locations and get a MatrixEntry for
        // each combination. Then collect them again to a single list and return it.

        IntStream startIndices = IntStream.range(0, startLocations.length);
        List<TimeMatrixEntry> matrix = null;

        try {
            matrix = startIndices.parallel()
                    .mapToObj(i -> IntStream.range(0, destinationLocations.length)
                            .mapToObj(j -> {
                                try {
                                    return getMatrixEntryForRouteArguments(i, j, startLocations[i], destinationLocations[j], departureTime);
                                } catch (RoutingAPIException e) {
                                    // do ugly exception wrapping
                                    throw new RuntimeException(e);
                                }
                            })
                            .collect(toList()))
                    .flatMap(l -> l.stream())
                    .collect(toList());
        } catch (RuntimeException e) {
            // unwrap ugly wrapped exception if it is RoutingApiException
            if (e.getCause() != null && e.getCause() instanceof RoutingAPIException) {
                throw (RoutingAPIException) e.getCause();
            }
            throw e;
        }
        return matrix;
    }

    private TimeMatrixEntry getMatrixEntryForRouteArguments(int fromIndex, int toIndex, Location start, Location destination,
                                                            LocalDateTime departure) throws RoutingAPIException {

        Reader response = getHTTPResponse(start, destination, departure);
        assert response != null;
        JsonObject json;

        try {
            json = Json.createReader(response).readObject();
        } catch (JsonParsingException e) {
            throw new RoutingAPIException(RoutingAPIException.ErrorCode.PROCESS_RESPONSE_ERROR_JSON, "here.com api " +
                    "response could not be converted to json, probably it returned an error instead of json", e);
        }

        assert json != null;
        return getTimeMatrixEntryFromJsonRoute(fromIndex, toIndex, json);
    }

    private TimeMatrixEntry getTimeMatrixEntryFromJsonRoute(int fromIndex, int toIndex, JsonObject json) throws RoutingAPIException {

        // error handling


        JsonObject jsonRouteSummary;

        try {
            // only get first RouteSummary as it will not return alternatives due to missing 'alternative' request parameter
            jsonRouteSummary = json
                    .getJsonObject("response")
                    .getJsonArray("route")
                    .getJsonObject(0)
                    .getJsonObject("summary");
        } catch (NullPointerException e) {
            throw new RoutingAPIException(RoutingAPIException.ErrorCode.PROCESS_RESPONSE_ERROR_JSON, "here.com api " +
                    "response does not contain any routes: " + json.toString(), e);
        }

        int distance = jsonRouteSummary.getInt("distance");
        int time = jsonRouteSummary.getInt("baseTime");

        return new TimeMatrixEntry(fromIndex, toIndex, time, distance, DistanceUnits.KILOMETERS);
    }

    @Override
    public RouteSummary getPublicTransportRouteSummary(Location start, Location destination, LocalDateTime departure) throws RoutingAPIException {
        Reader responseReader = getHTTPResponse(start, destination, departure);
        JsonObject json = null;

        try {
            json = Json.createReader(responseReader).readObject();
        } catch (JsonParsingException e) {
            throw new RoutingAPIException(RoutingAPIException.ErrorCode.PROCESS_RESPONSE_ERROR_JSON, "here.com api " +
                    "response could not be converted to json", e);
        }

        assert json != null;
        return getRouteSummaryFromJsonResponse(json);

    }

    private RouteSummary getRouteSummaryFromJsonResponse(JsonObject json) throws RoutingAPIException {

        JsonObject route = null;
        try {
            route = json
                    .getJsonObject("response")
                    .getJsonArray("route")
                    .getJsonObject(0);
        } catch (NullPointerException e) {
            throw new RoutingAPIException(RoutingAPIException.ErrorCode.PROCESS_RESPONSE_ERROR_JSON, "here.com api " +
                    "response does not contain any routes: " + json.toString(), e);
        }

        assert route != null;

        // travel times for modes: route/leg[]/maneuver{_type,traveltime}
        JsonArray legs = route.getJsonArray("leg");

        int publicTransportTravelTime = 0,
                walkingTravelTime = 0;

        for (int i = 0; i < legs.size(); i++) {
            JsonArray maneuvers = legs.getJsonObject(i).getJsonArray("maneuver");
            for (int j = 0; j < maneuvers.size(); j++) {
                JsonObject maneuver = maneuvers.getJsonObject(j);
                String type = maneuver.getJsonString("_type").getString();
                int travelTime = maneuver.getInt("travelTime");
                switch (type) {
                    case "PrivateTransportManeuverType":
                        walkingTravelTime += travelTime;
                        break;
                    case "PublicTransportManeuverType":
                        publicTransportTravelTime += travelTime;
                        break;
                    default:
                        throw new RoutingAPIException(RoutingAPIException.ErrorCode.DATA_SOURCE_RESPONSE_INVALID,
                                "can not handle transport type ["
                                        + type + "] in response by here.com: \n" + route);
                }
            }
        }

        HashMap<TransportMode, Integer> modeOfTransportTravelTimes = new HashMap<>();
        modeOfTransportTravelTimes.put(TransportMode.PUBLIC_TRANSPORT, publicTransportTravelTime);
        modeOfTransportTravelTimes.put(TransportMode.WALKING, walkingTravelTime);

        // total duration: route/summary/travelTime
        int duration = route.getJsonObject("summary").getInt("travelTime");

        // departure: route/summary/departure
        String departureAsISOString = route.getJsonObject("summary").getString("departure");
        LocalDateTime departure = LocalDateTime.parse(departureAsISOString, ISO_OFFSET_DATE_TIME);

        // arrival: departure + travelTime
        LocalDateTime arrival = departure.plus(Duration.ofSeconds(duration));

        // number of changes: route/publicTransportLine -1
        int numberOfChanges = route.getJsonArray("publicTransportLine").size() - 1;

        // distance: route/summary/distance
        int distance = route.getJsonObject("summary").getInt("distance");

        RouteSummary routeSummary = new RouteSummary();
        routeSummary.setArrivalTime(arrival);
        routeSummary.setDepartureTime(departure);
        routeSummary.setNumberOfChanges(numberOfChanges);
        routeSummary.setTotalDuration(duration);
        routeSummary.setModeOfTransportTravelTimes(modeOfTransportTravelTimes);
        routeSummary.setTotalDistance(distance);
        return routeSummary;

    }

    private Reader getHTTPResponse(Location start, Location destination, LocalDateTime departure) throws RoutingAPIException {
        String url = buildURL(start, destination, departure.withNano(0));

        System.out.println("call URL " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        //Acquire a ticket from the rate limiter
        rateLimiter.acquire();
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            throw new RoutingAPIException(RoutingAPIException.ErrorCode.HTTP, e);
        }

        return response.body().charStream();
    }

    private String buildURL(Location start, Location destination, LocalDateTime departure) {
        return URL_MAIN +
                formatFirstParameter(URL_APP_ID, apiId) +
                formatParameter(URL_APP_CODE, apiCode) +
                formatParameter(URL_START, start.getLatitude(), start.getLongitude()) +
                formatParameter(URL_DESTINATION, destination.getLatitude(), destination.getLongitude()) +
                formatParameter(URL_DEPARTURE, departure.format(ISO_LOCAL_DATE_TIME)) +
                formatParameter(URL_MODE) +
                formatParameter(URL_COMBINE_CHANGE);
    }

    private static String formatParameter(String parameterTemplate, Object... args) {
        return formatParameter(false, parameterTemplate, args);
    }

    private static String formatFirstParameter(String parameterTemplate, Object... args) {
        return formatParameter(true, parameterTemplate, args);
    }

    private static String formatParameter(boolean firstParameter, String parameterTemplate, Object... args) {
        return (firstParameter ? "?" : "&") + String.format(parameterTemplate, args);
    }
}
