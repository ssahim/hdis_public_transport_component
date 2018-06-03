package de.tu_berlin.dima.niteout.routing;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.utils.URIBuilder;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;

/**
 * A base class for Mapzen API consumers/wrappers
 * @author Andres Ardila
 */
abstract class MapzenApi {

    protected final String apiKey;
    protected final String service;
    private final String urlFormat = "https://%s.mapzen.com/%s";

    protected MapzenApi(String service, String apiKey) {
        assert !service.isEmpty();
        assert !apiKey.isEmpty();

        this.service = service;
        this.apiKey = apiKey;
    }

    protected JsonObject getResponse(String endpoint, JsonObject jsonObject) throws RoutingAPIException {
        final String url;
        final JsonObject json;
        try {
            url = getUrl(endpoint, jsonObject);
            json =  getResponse(url);
        } catch (URISyntaxException e) {
            throw new RoutingAPIException(RoutingAPIException.ErrorCode.INVALID_URI_SYNTAX, e);
        }
        if (json.containsKey("status_code") && json.getJsonNumber("status_code").intValue() != 200) {
            throw RoutingAPIException.buildFromStatusCode(json.getJsonNumber("status_code").intValue(), json.toString());
        }
        return json;
    }

    protected JsonObject getResponse(String endpoint, LinkedHashMap<String, String> queryString) throws RoutingAPIException {
        final String url;
        try {
            url = getUrl(endpoint, queryString);
            return getResponse(url);
        } catch (URISyntaxException e) {
            throw new RoutingAPIException(RoutingAPIException.ErrorCode.INVALID_URI_SYNTAX, e);
        }
    }

    private JsonObject getResponse(String url) throws RoutingAPIException {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response;
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            throw new RoutingAPIException(RoutingAPIException.ErrorCode.HTTP, e);
        }
        JsonReader jsonReader = Json.createReader(response.body().charStream());
        JsonObject out = jsonReader.readObject();

        return out;
    }

    protected String getUrl(String endpoint, JsonObject jsonObject) throws URISyntaxException {
        assert jsonObject != null;

        LinkedHashMap<String, String> queryString = new LinkedHashMap<>();
        queryString.put("json", jsonObject.toString());
        queryString.put("api_key", this.apiKey);
        return getUrl(endpoint, queryString);
    }

    protected String getUrl(String endpoint, LinkedHashMap<String, String> queryString) throws URISyntaxException {
        String baseUrl = String.format(urlFormat, service, endpoint);
        URIBuilder builder = new URIBuilder(baseUrl);
        for (String key : queryString.keySet()) {
            builder.addParameter(key, queryString.get(key));
        }
        URI uri = builder.build();
        return uri.toString();
    }
}
