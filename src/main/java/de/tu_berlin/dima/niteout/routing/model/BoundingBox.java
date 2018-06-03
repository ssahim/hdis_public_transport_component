package de.tu_berlin.dima.niteout.routing.model;

/**
 * Created by aardila on 1/22/2017.
 */
public class BoundingBox {
    public final double MinX;
    public final double MinY;
    public final double MaxX;
    public final double MaxY;

    public BoundingBox(double minX, double minY, double maxX, double maxY) {
        this.MinX = minX;
        this.MinY = minY;
        this.MaxX = maxX;
        this.MaxY = maxY;
    }

    public boolean contains(Location location) {
        return
                (location.getLatitude() >= MinY && location.getLatitude() <= MaxY)
                &&
                (location.getLongitude() >= MinX && location.getLongitude() <= MaxX);
    }
}
