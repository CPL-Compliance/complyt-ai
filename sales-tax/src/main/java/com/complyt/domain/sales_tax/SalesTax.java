package com.complyt.domain.sales_tax;

import lombok.*;

import java.math.BigDecimal;

@With
public record SalesTax(BigDecimal amount, SalesTaxRates salesTaxRates) {
}
