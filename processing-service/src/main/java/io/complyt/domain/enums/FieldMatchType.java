package io.complyt.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FieldMatchType {
    EXACT("Exact Match"),
    PARTIAL("Partial Match"),
    GOOD("Good Match"),
    NO_MATCH("Did Not Match");

    private final String description;

    // Utility method to derive status from a score
    public static FieldMatchType fromScore(double score) {
        if (score == 1.0) return EXACT;
        if (score >= 0.7) return GOOD;
        if (score >= 0.4) return PARTIAL;
        return NO_MATCH;
    }
}
