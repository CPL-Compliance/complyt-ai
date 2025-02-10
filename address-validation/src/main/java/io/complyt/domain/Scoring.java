package io.complyt.domain;

import io.complyt.domain.enums.MatchLevelType;
import io.complyt.domain.enums.FieldsMatchScore;
import lombok.With;

@With
public record Scoring(
        MatchLevelType matchLevel,
        double score,
        FieldsMatchScore fieldScore
) {
}
