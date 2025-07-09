package io.complyt.domain.transaction;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CityCountyWrapperTest {

    @Test
    void testFieldAccessors() {
        CityCountyWrapper wrapper = new CityCountyWrapper("San Francisco", "San Francisco County");

        assertEquals("San Francisco", wrapper.city());
        assertEquals("San Francisco County", wrapper.county());
    }

    @Test
    void testWithMethods() {
        CityCountyWrapper original = new CityCountyWrapper("Los Angeles", "LA County");

        CityCountyWrapper updatedCity = original.withCity("San Diego");
        assertEquals("San Diego", updatedCity.city());
        assertEquals("LA County", updatedCity.county());

        CityCountyWrapper updatedCounty = original.withCounty("Orange County");
        assertEquals("Los Angeles", updatedCounty.city());
        assertEquals("Orange County", updatedCounty.county());
    }

    @Test
    void testEqualsAndHashCode() {
        CityCountyWrapper a = new CityCountyWrapper("CityA", "CountyA");
        CityCountyWrapper b = new CityCountyWrapper("CityA", "CountyA");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testToString() {
        CityCountyWrapper wrapper = new CityCountyWrapper("Seattle", "King County");
        String result = wrapper.toString();

        assertTrue(result.contains("Seattle"));
        assertTrue(result.contains("King County"));
    }
}
