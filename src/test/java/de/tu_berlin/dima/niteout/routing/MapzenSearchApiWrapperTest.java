package de.tu_berlin.dima.niteout.routing;

import de.tu_berlin.dima.niteout.routing.model.Address;
import de.tu_berlin.dima.niteout.routing.model.BoundingBox;
import de.tu_berlin.dima.niteout.routing.model.Location;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by aardila on 1/29/2017.
 */
public class MapzenSearchApiWrapperTest {
    private final BoundingBox TU_BERLIN_BBOX =
            new BoundingBox(13.3198, 52.5097, 13.3346, 52.5169);

    @Test
    public void getAddressLocation() throws Exception {
        Address address = new Address.AddressBuilder()
                .street("Straße des 17. Juni")
                .houseNumber("135")
                .city("Berlin")
                .postalCode(10623)
                .build();

        MapzenSearchApiWrapper api = new MapzenSearchApiWrapper(System.getProperty("API_KEY_MAPZEN"));
        Location location = api.getLocation(address);
        Assert.assertNotNull(location);
        Assert.assertTrue(TU_BERLIN_BBOX.contains(location));
    }
}