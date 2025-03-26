package io.complyt.domain.enums;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;

@With
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FieldsMatchScore(
        FieldMatchType countryMatch,
        FieldMatchType stateMatch,
        FieldMatchType cityMatch,
        FieldMatchType streetMatch,
        FieldMatchType zipMatch,
        FieldMatchType regionMatch
) {
}
