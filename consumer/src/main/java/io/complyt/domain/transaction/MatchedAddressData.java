package io.complyt.domain.transaction;

import lombok.With;

@With
public record MatchedAddressData(
        MandatoryAddress address,
        Scoring scoring
) {}
