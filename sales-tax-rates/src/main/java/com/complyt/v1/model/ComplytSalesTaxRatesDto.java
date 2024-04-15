package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@With
@Schema(name = "ComplytSalesTaxRates")
public record ComplytSalesTaxRatesDto (AddressDto address, SalesTaxRatesDto salesTaxRates) {
}

