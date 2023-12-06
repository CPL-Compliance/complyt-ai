package com.complyt.domain;

import lombok.With;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@With
public record ComplytSalesTaxRates(@Id String id, Address address, Address requestAddress,
                                   SalesTaxRates salesTaxRates,
                                   LocalDateTime createdDate, LocalDateTime expireAt) {
}
