package io.complyt.domain;

import io.complyt.domain.enums.MatchLevelType;
import lombok.With;

@With
public record CachedAddressData(Address address, Scoring scoring) implements AddressData {
}