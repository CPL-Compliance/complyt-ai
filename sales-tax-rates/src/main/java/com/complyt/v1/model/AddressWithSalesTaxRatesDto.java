package com.complyt.v1.model;

import com.complyt.domain.Address;
import com.complyt.domain.SalesTaxRates;
import lombok.Value;
import lombok.With;

@With
public record AddressWithSalesTaxRatesDto(Address address, SalesTaxRates salesTaxRates) {
}
