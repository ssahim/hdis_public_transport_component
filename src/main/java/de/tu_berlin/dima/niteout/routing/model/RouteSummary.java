package de.tu_berlin.dima.niteout.routing.model;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Represents aggregate information about a Route
 * @author Andres Ardila
 */
public class RouteSummary {
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int totalDuration;
    private double totalDistance;
    private HashMap<TransportMode, Integer> modeOfTransportTravelTimes;
    private int numberOfChanges;


    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public double getTotalDistance() { return totalDistance; }

    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

    public HashMap<TransportMode, Integer> getModeOfTransportTravelTimes() {
        return modeOfTransportTravelTimes;
    }

    public void setModeOfTransportTravelTimes(HashMap<TransportMode, Integer> modeOfTransportTravelTimes) {
        this.modeOfTransportTravelTimes = modeOfTransportTravelTimes;
    }

    public int getNumberOfChanges() {
        return numberOfChanges;
    }

    public void setNumberOfChanges(int numberOfChanges) {
        this.numberOfChanges = numberOfChanges;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }
}
