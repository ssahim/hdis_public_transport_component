package de.tu_berlin.dima.niteout.routing.model.mapzen;

/**
 * Created by aardila on 1/22/2017.
 */
public enum MatrixType {

    ONE_TO_MANY         ("one_to_many"),
    MANY_TO_ONE         ("many_to_one"),
    MANY_TO_MANY        ("many_to_many"),
    SOURCES_TO_TARGETS  ("sources_to_targets");

    private final String ApiString;

    private MatrixType(String apiString) { this.ApiString = apiString; }
    public String getApiString() { return ApiString; }
}
