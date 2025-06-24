package com.complyt.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchLevelTypeTest {

    @Test
    void testFromScore_Excellent() {
        assertEquals(MatchLevelType.EXCELLENT, MatchLevelType.fromScore(0.95));
        assertEquals(MatchLevelType.EXCELLENT, MatchLevelType.fromScore(1.0));
    }

    @Test
    void testFromScore_VeryGood() {
        assertEquals(MatchLevelType.VERY_GOOD, MatchLevelType.fromScore(0.85));
    }

    @Test
    void testFromScore_Good() {
        assertEquals(MatchLevelType.GOOD, MatchLevelType.fromScore(0.75));
    }

    @Test
    void testFromScore_Fair() {
        assertEquals(MatchLevelType.FAIR, MatchLevelType.fromScore(0.65));
    }

    @Test
    void testFromScore_Poor() {
        assertEquals(MatchLevelType.POOR, MatchLevelType.fromScore(0.5));
    }

    @Test
    void testFromScore_VeryPoor() {
        assertEquals(MatchLevelType.VERY_POOR, MatchLevelType.fromScore(0.05));
        assertEquals(MatchLevelType.VERY_POOR, MatchLevelType.fromScore(-0.1));
    }

    @Test
    void testScoreOnBoundaries() {
        assertEquals(MatchLevelType.EXCELLENT, MatchLevelType.fromScore(0.9));
        assertEquals(MatchLevelType.VERY_GOOD, MatchLevelType.fromScore(0.8));
        assertEquals(MatchLevelType.GOOD, MatchLevelType.fromScore(0.7));
        assertEquals(MatchLevelType.FAIR, MatchLevelType.fromScore(0.6));
        assertEquals(MatchLevelType.POOR, MatchLevelType.fromScore(0.1));
        assertEquals(MatchLevelType.VERY_POOR, MatchLevelType.fromScore(0.0));
    }

    @Test
    void testExactBoundaries() {
        assertEquals(MatchLevelType.VERY_POOR, MatchLevelType.fromScore(0.0));
        assertEquals(MatchLevelType.POOR, MatchLevelType.fromScore(0.1));
        assertEquals(MatchLevelType.POOR, MatchLevelType.fromScore(0.599));
        assertEquals(MatchLevelType.FAIR, MatchLevelType.fromScore(0.6)); // potential conflict
        assertEquals(MatchLevelType.GOOD, MatchLevelType.fromScore(0.7)); // potential conflict
        assertEquals(MatchLevelType.VERY_GOOD, MatchLevelType.fromScore(0.8)); // potential conflict
        assertEquals(MatchLevelType.EXCELLENT, MatchLevelType.fromScore(0.9)); // conflict with VERY_GOOD
        assertEquals(MatchLevelType.EXCELLENT, MatchLevelType.fromScore(1.0));
    }

    @Test
    void testOverlappingCondition() {
        // This test is expected to fail if the original implementation includes `score <= category.max`
        MatchLevelType result = MatchLevelType.fromScore(0.9);
        assertEquals(MatchLevelType.EXCELLENT, result); // May fail if VERY_GOOD captures 0.9 first
    }

}
