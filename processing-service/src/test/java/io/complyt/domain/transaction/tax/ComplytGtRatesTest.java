package io.complyt.domain.transaction.tax;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ComplytGtRatesTest {

    @Test
    void testRecordFieldsAndWithMethods() {
        GtAddress address = new GtAddress("country", "region");
        GtRates rates = new GtRates(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.02), BigDecimal.valueOf(0.07));

        ComplytGtRates gtRates = new ComplytGtRates(address, rates);

        assertEquals(address, gtRates.gtAddress());
        assertEquals(rates, gtRates.gtRates());

        // Test `with` methods
        GtAddress newAddress = new GtAddress("Canada", "Quebec");
        ComplytGtRates updated = gtRates.withGtAddress(newAddress);

        assertEquals(newAddress, updated.gtAddress());
        assertEquals(rates, updated.gtRates());
        assertNotEquals(gtRates, updated);
    }

    @Test
    void testEqualityAndHashCode() {
        GtAddress address = new GtAddress("country", "region");
        GtRates rates = new GtRates(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.02), BigDecimal.valueOf(0.07));

        ComplytGtRates gtRates1 = new ComplytGtRates(address, rates);
        ComplytGtRates gtRates2 = new ComplytGtRates(address, rates);

        assertEquals(gtRates1, gtRates2);
        assertEquals(gtRates1.hashCode(), gtRates2.hashCode());
    }

    @Test
    void testToString() {
        GtAddress address = new GtAddress("country", "region");
        GtRates rates = new GtRates(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.02), BigDecimal.valueOf(0.07));

        ComplytGtRates gtRates = new ComplytGtRates(address, rates);
        String str = gtRates.toString();

        assertTrue(str.contains("GtAddress"));
        assertTrue(str.contains("GtRates"));
    }
}
