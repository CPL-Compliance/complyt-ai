package com.complyt.domain;

import com.complyt.domain.properties.ComplytIdProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@With
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class ComplytSalesTaxRates extends TaxRates implements ComplytIdProperty {
    UUID complytId;
    @Id
    String id;
    Address address;
    SalesTaxRates salesTaxRates;
    LocalDateTime createdDate;
    LocalDateTime expireAt;
}
