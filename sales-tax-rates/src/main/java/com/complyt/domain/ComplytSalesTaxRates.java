package com.complyt.domain;

import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Value
@With
public class ComplytSalesTaxRates {
    @Id
    String id;
    Address address;
    SalesTaxRates salesTaxRates;
    LocalDateTime createdDate;
    LocalDateTime expireAt;
}
