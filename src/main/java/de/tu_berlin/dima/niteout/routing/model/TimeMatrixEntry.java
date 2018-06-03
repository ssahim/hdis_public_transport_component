package de.tu_berlin.dima.niteout.routing.model;

/**
 * Created by aardila on 1/23/2017.
 */
public class TimeMatrixEntry {
    private int fromIndex;
    private int toIndex;
    private int time;
    private double distance;
    private DistanceUnits units;

    public TimeMatrixEntry(int fromIndex, int toIndex, int time, double distance, DistanceUnits units) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.time = time;
        this.distance = distance;
        this.units = units;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public void setFromIndex(int fromIndex) {
        this.fromIndex = fromIndex;
    }

    public int getToIndex() {
        return toIndex;
    }

    public void setToIndex(int toIndex) {
        this.toIndex = toIndex;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public DistanceUnits getUnits() {
        return units;
    }

    public void setUnits(DistanceUnits units) {
        this.units = units;
    }
}
