package io.complyt.domain.transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MandatoryAddressTest {

    @Test
    void testFieldAccessors() {
        MandatoryAddress address = new MandatoryAddress(
                "New York", "USA", "New York County", "NY", "5th Avenue", "East",
                "10001", false
        );

        assertEquals("New York", address.city());
        assertEquals("USA", address.country());
        assertEquals("New York County", address.county());
        assertEquals("NY", address.state());
        assertEquals("5th Avenue", address.street());
        assertEquals("East", address.region());
        assertEquals("10001", address.zip());
        assertFalse(address.isPartial());
    }

    @Test
    void testWithMethod() {
        MandatoryAddress original = new MandatoryAddress(
                "Tel Aviv", "Israel", "Tel Aviv County", "TA", "Rothschild", "Center", "12345", true
        );

        MandatoryAddress modified = original.withCity("Jerusalem");

        assertEquals("Jerusalem", modified.city());
        assertEquals("Israel", modified.country());
        assertEquals(original.zip(), modified.zip());
        assertNotEquals(original.city(), modified.city());
    }

    @Test
    void testEqualsAndHashCode() {
        MandatoryAddress a = new MandatoryAddress("Paris", "France", "Paris County", "IDF", "Champs-Élysées", "West", "75008", false);
        MandatoryAddress b = new MandatoryAddress("Paris", "France", "Paris County", "IDF", "Champs-Élysées", "West", "75008", false);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testJsonSerializationExcludesNulls() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

        MandatoryAddress address = new MandatoryAddress("Berlin", null, null, "BE", "Unter den Linden", null, "10117", null);

        String json = mapper.writeValueAsString(address);

        assertTrue(json.contains("Berlin"));
        assertTrue(json.contains("BE"));
        assertTrue(json.contains("Unter den Linden"));
        assertTrue(json.contains("10117"));
        assertFalse(json.contains("null"));
    }
}
