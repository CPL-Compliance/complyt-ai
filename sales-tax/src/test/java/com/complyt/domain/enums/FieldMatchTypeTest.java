package com.complyt.domain.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FieldMatchTypeTest {

    @Test
    public void testFromScore_Exact() {
        assertEquals(FieldMatchType.EXACT, FieldMatchType.fromScore(1.0));
    }

    @Test
    public void testFromScore_Good() {
        assertEquals(FieldMatchType.GOOD, FieldMatchType.fromScore(0.8));
        assertEquals(FieldMatchType.GOOD, FieldMatchType.fromScore(0.7));
    }

    @Test
    public void testFromScore_Partial() {
        assertEquals(FieldMatchType.PARTIAL, FieldMatchType.fromScore(0.6));
        assertEquals(FieldMatchType.PARTIAL, FieldMatchType.fromScore(0.4));
    }

    @Test
    public void testFromScore_NoMatch() {
        assertEquals(FieldMatchType.NO_MATCH, FieldMatchType.fromScore(0.3));
        assertEquals(FieldMatchType.NO_MATCH, FieldMatchType.fromScore(0.0));
    }

    @Test
    public void testDescriptions() {
        assertEquals("Exact Match", FieldMatchType.EXACT.getDescription());
        assertEquals("Good Match", FieldMatchType.GOOD.getDescription());
        assertEquals("Partial Match", FieldMatchType.PARTIAL.getDescription());
        assertEquals("Did Not Match", FieldMatchType.NO_MATCH.getDescription());
    }
}
