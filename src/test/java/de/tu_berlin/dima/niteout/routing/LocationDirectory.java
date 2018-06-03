package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Location;
import de.tu_berlin.dima.niteout.routing.model.BoundingBox;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A collection of locations around Berlin. Data were collected using https://mapzen.com/products/search/
 */
public final class LocationDirectory {

    private LocationDirectory() { }

    public static final BoundingBox BERLIN_BOUNDING_BOX =
            new BoundingBox(13.0904186037, 52.3685305255, 13.739978, 52.654269);

    public static final Location TU_BERLIN = new Location(52.51221, 13.32697);
    public static final Location HAUPTBAHNHOF = new Location(52.524742, 13.369563);
    public static final Location BRANDENBURGER_TOR = new Location(52.516289,13.377729);
    public static final Location POTSDAMER_PLATZ = new Location(52.509498,13.376598);
    public static final Location SIEGESSAEULE = new Location(52.51458, 13.35015);
    public static final Location ALEXANDERPLATZ = new Location(52.520699, 13.410964);

    /**
     * Gets a random location in Berlin.
     * Because this uses a rectangular bounding box, it is possible that the location returned is not technically within
     * the official Bundesland Berlin boundaries (esp. for values close to the edges)
     * @return
     */
    public static Location getRandomLocationInBerlin() {
        return new Location(
            ThreadLocalRandom.current().nextDouble(BERLIN_BOUNDING_BOX.MinY, BERLIN_BOUNDING_BOX.MaxY),
            ThreadLocalRandom.current().nextDouble(BERLIN_BOUNDING_BOX.MinX, BERLIN_BOUNDING_BOX.MaxX)
        );
    }

    public static Location getRandomLocation(BoundingBox boundingBox) {
        return new Location(
                ThreadLocalRandom.current().nextDouble(boundingBox.MinY, boundingBox.MaxY),
                ThreadLocalRandom.current().nextDouble(boundingBox.MinX, boundingBox.MaxX)
        );
    }
}
