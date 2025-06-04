package io.complyt.domain.transaction;


import io.complyt.domain.matched_address.enums.FieldsMatchScore;
import io.complyt.domain.matched_address.enums.MatchLevelType;
import lombok.With;

@With
public record Scoring(
        MatchLevelType matchLevel,
        double score,
        FieldsMatchScore fieldScore
) {
}
