package de.tu_berlin.dima.niteout.routing.model;

/**
 * The location we will use in the NiteOut-PublicTransport module
 */
public class Location {
    private double latitude;
    private double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * @return the latitude of the location
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Set the latitude of the location to a new value
     * @param latitude new value
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude of the location
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Set the longitude of the location to a new value
     * @param longitude new value
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
