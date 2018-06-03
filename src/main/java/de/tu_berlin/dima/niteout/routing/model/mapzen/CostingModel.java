package de.tu_berlin.dima.niteout.routing.model.mapzen;


public enum CostingModel {

    /**
     * Standard costing for driving routes by car, motorcycle, truck, and so on that obeys automobile driving rules, such as access and turn restrictions.
     * Auto provides a short time path (though not guaranteed to be shortest time) and uses intersection costing to minimize turns and maneuvers or road name changes.
     * Routes also tend to favor highways and higher classification roads, such as motorways and trunks.
     */
    AUTO ("auto"),

    /**
     * Alternate costing for driving that provides a short path (though not guaranteed to be shortest distance) that obeys driving rules for access and turn restrictions.
     */
    AUTO_SHORTER ("auto_shorter"),

    /**
     * Standard costing for travel by bicycle, with a slight preference for using cycleways or roads with bicycle lanes.
     * Bicycle routes follow regular roads when needed, but avoid roads without bicycle access.
     */
    BICYCLE ("bicycle"),

    /**
     * Standard costing for bus routes. Bus costing inherits the auto costing behaviors, but checks for bus access on the roads.
     */
    BUS ("bus"),

    /**
     * Standard costing for high-occupancy vehicle (HOV) routes. HOV costing inherits the auto costing behaviors, but checks for HOV lane access on the roads and favors those roads.
     */
    HOV ("hov"),

    /**
     * Currently supports pedestrian and transit. In the future, multimodal will support a combination of all of the above.
     */
    MULTIMODAL ("multimodal"),

    /**
     * Standard walking route that excludes roads without pedestrian access.
     * In general, pedestrian routes are shortest distance with the following exceptions: walkways and footpaths are slightly favored, while steps or stairs and alleys are slightly avoided.
     */
    PEDESTRIAN ("pedestrian");

    
    private final String ApiString;

    private CostingModel(String apiString) { this.ApiString = apiString; }
    public String getApiString() { return ApiString; }
}
