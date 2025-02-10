package com.complyt.domain.common_rates;

import com.complyt.domain.enums.FieldsMatchScore;
import com.complyt.domain.enums.MatchLevelType;
import lombok.With;

@With
public record Scoring(
        MatchLevelType matchLevel,
        double score,
        FieldsMatchScore fieldScore
) {
}
