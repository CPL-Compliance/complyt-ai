package com.complyt.domain.sales_tax;

import lombok.*;

@With
public record SalesTax(double amount, SalesTaxRates salesTaxRates) {
}
