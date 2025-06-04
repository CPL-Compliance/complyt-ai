package io.complyt.domain.transaction.tax;

import io.complyt.domain.transaction.BaseAddress;
import lombok.With;

@With
public record GtAddress(String country, String region) implements BaseAddress {
}
