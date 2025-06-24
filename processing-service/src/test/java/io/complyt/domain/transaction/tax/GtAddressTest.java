package io.complyt.domain.transaction.tax;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GtAddressTest {

    @Test
    void testFieldAccessors() {
        GtAddress address = new GtAddress("USA", "CA");

        assertEquals("USA", address.country());
        assertEquals("CA", address.region());
    }

    @Test
    void testWithMethods() {
        GtAddress original = new GtAddress("USA", "CA");

        GtAddress updatedCountry = original.withCountry("CAN");
        assertEquals("CAN", updatedCountry.country());
        assertEquals("CA", updatedCountry.region());

        GtAddress updatedRegion = original.withRegion("NY");
        assertEquals("USA", updatedRegion.country());
        assertEquals("NY", updatedRegion.region());
    }

    @Test
    void testEqualsAndHashCode() {
        GtAddress a1 = new GtAddress("USA", "CA");
        GtAddress a2 = new GtAddress("USA", "CA");

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    void testToString() {
        GtAddress address = new GtAddress("USA", "CA");
        String str = address.toString();

        assertTrue(str.contains("USA"));
        assertTrue(str.contains("CA"));
    }
}
