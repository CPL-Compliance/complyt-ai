package com.complyt.domain.sales_tax;

import lombok.*;

@With
public record SalesTax(float amount, SalesTaxRates salesTaxRates) {
}
