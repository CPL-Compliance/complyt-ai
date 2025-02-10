package com.complyt.v1.models.matched_address.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MatchLevelTypeTest {

    @Test
    void fromScore_returnsExcellent_forBoundaryValues() {
        assertEquals(MatchLevelType.EXCELLENT, MatchLevelType.fromScore(0.9));
        assertEquals(MatchLevelType.EXCELLENT, MatchLevelType.fromScore(1.0));
    }

    @Test
    void fromScore_returnsVeryGood_forBoundaryValues() {
        assertEquals(MatchLevelType.VERY_GOOD, MatchLevelType.fromScore(0.8));
        assertEquals(MatchLevelType.VERY_GOOD, MatchLevelType.fromScore(0.89));
    }

    @Test
    void fromScore_returnsGood_forBoundaryValues() {
        assertEquals(MatchLevelType.GOOD, MatchLevelType.fromScore(0.7));
        assertEquals(MatchLevelType.GOOD, MatchLevelType.fromScore(0.79));
    }

    @Test
    void fromScore_returnsFair_forBoundaryValues() {
        assertEquals(MatchLevelType.FAIR, MatchLevelType.fromScore(0.6));
        assertEquals(MatchLevelType.FAIR, MatchLevelType.fromScore(0.69));
    }

    @Test
    void fromScore_returnsPoor_forBoundaryValues() {
        assertEquals(MatchLevelType.POOR, MatchLevelType.fromScore(0.1));
        assertEquals(MatchLevelType.POOR, MatchLevelType.fromScore(0.59));
    }

    @Test
    void fromScore_returnsVeryPoor_forBoundaryValues() {
        assertEquals(MatchLevelType.VERY_POOR, MatchLevelType.fromScore(0.0));
        assertEquals(MatchLevelType.VERY_POOR, MatchLevelType.fromScore(0.09));
    }

    @Test
    void fromScore_returnsVeryPoor_forNegativeValues() {
        assertEquals(MatchLevelType.VERY_POOR, MatchLevelType.fromScore(-0.1));
    }

    @Test
    void fromScore_returnsVeryPoor_forAboveMaxValue() {
        assertEquals(MatchLevelType.VERY_POOR, MatchLevelType.fromScore(1.1));
    }

    @Test
    void getLabel_returnsCorrectLabels() {
        assertEquals("Excellent", MatchLevelType.EXCELLENT.getLabel());
        assertEquals("Very Good", MatchLevelType.VERY_GOOD.getLabel());
        assertEquals("Good", MatchLevelType.GOOD.getLabel());
        assertEquals("Fair", MatchLevelType.FAIR.getLabel());
        assertEquals("Poor", MatchLevelType.POOR.getLabel());
        assertEquals("Very Poor", MatchLevelType.VERY_POOR.getLabel());
    }

    @Test
    void scoreAtExactUpperBound_returnsExpectedCategory() {
        assertEquals(MatchLevelType.EXCELLENT, MatchLevelType.fromScore(1.0));
        assertEquals(MatchLevelType.VERY_GOOD, MatchLevelType.fromScore(0.8));
        assertEquals(MatchLevelType.GOOD, MatchLevelType.fromScore(0.7));
        assertEquals(MatchLevelType.FAIR, MatchLevelType.fromScore(0.6));
        assertEquals(MatchLevelType.POOR, MatchLevelType.fromScore(0.1));
        assertEquals(MatchLevelType.VERY_POOR, MatchLevelType.fromScore(0));
    }
}