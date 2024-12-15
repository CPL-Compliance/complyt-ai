package com.complyt.domain.internal_rates;

import lombok.With;

@With
public record InternalAddress(String state,
                              String county,
                              String city,
                              boolean isUnincorporated,
                              String zip,
                              int lowerPlusFourDigits,
                              int upperPlusFourDigits) {
}
