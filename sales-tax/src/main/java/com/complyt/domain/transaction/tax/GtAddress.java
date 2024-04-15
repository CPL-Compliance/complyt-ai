package com.complyt.domain.transaction.tax;

import com.complyt.domain.transaction.BaseAddress;
import lombok.With;

@With
public record GtAddress(String country, String region) implements BaseAddress {
}
