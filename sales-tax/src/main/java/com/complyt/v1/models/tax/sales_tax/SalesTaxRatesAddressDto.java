package com.complyt.v1.models.tax.sales_tax;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SalesTaxRatesAddressDto")
public record SalesTaxRatesAddressDto(
        String country,
        String state,
        String county,
        String city,
        Boolean isUnincorporated,
        Boolean hasPlusFourZipCode,
        String zip,
        Integer lowerPlusFourDigits,
        Integer upperPlusFourDigits,
        String street,
        Boolean isPartial) {
}
