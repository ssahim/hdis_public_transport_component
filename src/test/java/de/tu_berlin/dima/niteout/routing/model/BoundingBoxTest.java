package de.tu_berlin.dima.niteout.routing.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by aardila on 1/29/2017.
 */
public class BoundingBoxTest {
    @Test
    public void contains() throws Exception {
        final BoundingBox fixture = new BoundingBox(-1, -1, 1, 1);
        final Location containedLocation = new Location(0, 0);
        final Location outsideLocation = new Location(5, -5);
        final boolean contains = fixture.contains(containedLocation);
        final boolean notContains = fixture.contains(outsideLocation);
        Assert.assertTrue(contains);
        Assert.assertFalse(notContains);
    }

}