package com.complyt.domain;

import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;

@Value
@With
public class AddressWithSalesTaxRates {
    Address address;
    SalesTaxRates salesTaxRates;
    LocalDateTime expireAt;
}
