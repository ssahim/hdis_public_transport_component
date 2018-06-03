package de.tu_berlin.dima.niteout.routing.model.mapzen;

/**
 * Created by aardila on 1/23/2017.
 */
public enum Units {
    KM ("km"),
    MI ("mi");

    private final String ApiString;

    private Units(String apiString) { this.ApiString = apiString; }
    public String getApiString() { return ApiString; }
}
