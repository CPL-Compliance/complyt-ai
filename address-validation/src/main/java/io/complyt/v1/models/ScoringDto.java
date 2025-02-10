package io.complyt.v1.models;

import io.complyt.domain.enums.MatchLevelType;
import io.complyt.domain.enums.FieldsMatchScore;
import lombok.With;

@With
public record ScoringDto(
        MatchLevelType matchLevel,
        double score,
        FieldsMatchScore fieldScore
) {
}
