package com.complyt.domain.matched_address;

import com.complyt.domain.Address;
import com.complyt.domain.common_rates.Scoring;
import lombok.With;

@With
public record MatchedAddressData(Address address, Scoring scoring) {
}
