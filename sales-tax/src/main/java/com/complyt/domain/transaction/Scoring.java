package com.complyt.domain.transaction;


import com.complyt.v1.models.matched_address.enums.FieldsMatchScore;
import com.complyt.v1.models.matched_address.enums.MatchLevelType;
import lombok.With;

@With
public record Scoring(
        MatchLevelType matchLevel,
        double score,
        FieldsMatchScore fieldScore
) {
}
