package de.tu_berlin.dima.niteout.routing.model;

import java.util.List;

public class Route {

    private List<Segment> segments;
    private int duration;

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
