package de.tu_berlin.dima.niteout.routing.model.mapzen;


public enum PedestrianCostingOption {

    /**
     * Walking speed in kilometers per hour. Defaults to 5.1 km/hr (3.1 miles/hour).
     */
    WALKING_SPEED ("walking_speed"),

    /**
     * A factor that modifies the cost when encountering roads or paths that do not allow vehicles and are set aside for pedestrian use.
     * Pedestrian routes generally attempt to favor using these walkways and sidewalks. The default walkway_factor is 0.9, indicating a slight preference.
     */
    WALKING_FACTOR ("walkway_factor"),

    /**
     * A factor that modifies (multiplies) the cost when alleys are encountered.
     * Pedestrian routes generally want to avoid alleys or narrow service roads between buildings. The default alley_factor is 2.0.
     */
    ALLEY_FACTOR ("alley_factor"),

    /**
     * A factor that modifies (multiplies) the cost when encountering a driveway, which is often a private, service road.
     * Pedestrian routes generally want to avoid driveways (private). The default driveway factor is 5.0.
     */
    DRIVEWAY_FACTOR ("driveway_factor"),

    /**
     * A penalty in seconds added to each transition onto a path with steps or stairs.
     * Higher values apply larger cost penalties to avoid paths that contain flights of steps.
     */
    STEP_PENALTY ("step_penalty");


    private final String ApiString;

    private PedestrianCostingOption(String apiString) { this.ApiString = apiString; }
    public String getApiString() { return ApiString; }
}
