package com.complyt.v1.models.matched_address.enums;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FieldsMatchScore(
        FieldMatchType countryMatch,
        FieldMatchType stateMatch,
        FieldMatchType cityMatch,
        FieldMatchType streetMatch,
        FieldMatchType zipMatch
) {
}
