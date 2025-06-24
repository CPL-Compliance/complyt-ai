package com.complyt.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FieldsMatchScoreTest {

    @Test
    public void testAllFieldsPopulated() {
        FieldsMatchScore score = new FieldsMatchScore(
                FieldMatchType.EXACT,
                FieldMatchType.GOOD,
                FieldMatchType.PARTIAL,
                FieldMatchType.NO_MATCH,
                FieldMatchType.EXACT
        );

        assertEquals(FieldMatchType.EXACT, score.countryMatch());
        assertEquals(FieldMatchType.GOOD, score.stateMatch());
        assertEquals(FieldMatchType.PARTIAL, score.cityMatch());
        assertEquals(FieldMatchType.NO_MATCH, score.streetMatch());
        assertEquals(FieldMatchType.EXACT, score.zipMatch());
    }

    @Test
    public void testSomeNullFields() {
        FieldsMatchScore score = new FieldsMatchScore(
                null,
                FieldMatchType.GOOD,
                null,
                FieldMatchType.NO_MATCH,
                null
        );

        assertNull(score.countryMatch());
        assertEquals(FieldMatchType.GOOD, score.stateMatch());
        assertNull(score.cityMatch());
        assertEquals(FieldMatchType.NO_MATCH, score.streetMatch());
        assertNull(score.zipMatch());
    }
}
