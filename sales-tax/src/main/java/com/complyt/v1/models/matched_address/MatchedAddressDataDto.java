package com.complyt.v1.models.matched_address;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.transaction.MandatoryAddressDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.With;

@With
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MatchedAddressDataDto(
        @Valid MandatoryAddressDto address,
        @Valid ScoringDto scoring
) {
}

