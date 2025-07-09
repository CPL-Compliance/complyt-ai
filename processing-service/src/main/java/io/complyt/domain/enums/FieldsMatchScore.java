package io.complyt.domain.enums;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FieldsMatchScore(
        FieldMatchType countryMatch,
        FieldMatchType stateMatch,
        FieldMatchType regionMatch,
        FieldMatchType cityMatch,
        FieldMatchType streetMatch,
        FieldMatchType zipMatch
) {
}