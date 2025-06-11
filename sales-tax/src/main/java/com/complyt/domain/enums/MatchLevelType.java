package com.complyt.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MatchLevelType {
    EXCELLENT(0.9, 1.0, "Excellent"),
    VERY_GOOD(0.8, 0.9, "Very Good"),
    GOOD(0.7, 0.8, "Good"),
    FAIR(0.6, 0.7, "Fair"),
    POOR(0.1, 0.6, "Poor"),
    VERY_POOR(0.0, 0.1, "Very Poor");

    private final double min; // Minimum value of the range (inclusive)
    private final double max; // Maximum value of the range (exclusive)
    private final String label; // Client-friendly label

    public static MatchLevelType fromScore(double score) {
        for (MatchLevelType category : values()) {
            if (score >= category.min && score <= category.max) {
                return category;
            }
        }
        return MatchLevelType.VERY_POOR;
    }
}
