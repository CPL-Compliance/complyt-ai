package io.complyt.v1.models;

import jakarta.validation.Valid;
import lombok.With;

@With
public record CachedAddressDataDto(
        @Valid AddressDto address,
        @Valid ScoringDto scoring
) {
}
