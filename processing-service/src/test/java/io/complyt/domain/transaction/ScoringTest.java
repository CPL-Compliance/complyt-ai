package io.complyt.domain.transaction;

import io.complyt.domain.enums.FieldMatchType;
import io.complyt.domain.enums.FieldsMatchScore;
import io.complyt.domain.enums.MatchLevelType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoringTest {

    @Test
    void testConstructorAndGetters() {
        FieldsMatchScore fieldScore = new FieldsMatchScore(
                FieldMatchType.EXACT, FieldMatchType.GOOD, FieldMatchType.GOOD,
                FieldMatchType.PARTIAL, FieldMatchType.NO_MATCH, FieldMatchType.EXACT);

        Scoring scoring = new Scoring(MatchLevelType.GOOD, 0.75, fieldScore);

        assertEquals(MatchLevelType.GOOD, scoring.matchLevel());
        assertEquals(0.75, scoring.score());
        assertEquals(fieldScore, scoring.fieldScore());
    }

    @Test
    void testWithMethodCreatesModifiedCopy() {
        FieldsMatchScore originalFieldScore = new FieldsMatchScore(
                FieldMatchType.GOOD, FieldMatchType.GOOD, FieldMatchType.GOOD,
                FieldMatchType.GOOD, FieldMatchType.GOOD, FieldMatchType.GOOD);

        Scoring original = new Scoring(MatchLevelType.FAIR, 0.65, originalFieldScore);
        Scoring modified = original.withScore(0.85).withMatchLevel(MatchLevelType.EXCELLENT);

        assertNotEquals(original, modified);
        assertEquals(0.85, modified.score());
        assertEquals(MatchLevelType.EXCELLENT, modified.matchLevel());
    }

    @Test
    void testEqualsAndHashCode() {
        FieldsMatchScore fieldScore = new FieldsMatchScore(
                FieldMatchType.PARTIAL, FieldMatchType.GOOD, FieldMatchType.EXACT,
                FieldMatchType.PARTIAL, FieldMatchType.GOOD, FieldMatchType.NO_MATCH);

        Scoring scoring1 = new Scoring(MatchLevelType.VERY_GOOD, 0.85, fieldScore);
        Scoring scoring2 = new Scoring(MatchLevelType.VERY_GOOD, 0.85, fieldScore);

        assertEquals(scoring1, scoring2);
        assertEquals(scoring1.hashCode(), scoring2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        FieldsMatchScore fieldScore = new FieldsMatchScore(
                FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT,
                FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT);

        Scoring scoring = new Scoring(MatchLevelType.EXCELLENT, 0.95, fieldScore);
        String output = scoring.toString();

        assertTrue(output.contains("EXCELLENT"));
        assertTrue(output.contains("0.95"));
        assertTrue(output.contains("fieldScore"));
    }
}
