package com.complyt.domain;

import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Value
@With
public class AddressWithSalesTaxRates {
    @Id
    String id;
    Address address;
    SalesTaxRates salesTaxRates;
    LocalDateTime createdDate;
    LocalDateTime expireAt;
}
