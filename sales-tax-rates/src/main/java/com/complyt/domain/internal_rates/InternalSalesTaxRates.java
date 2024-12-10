package com.complyt.domain.internal_rates;


import com.complyt.domain.TaxRates;
import com.complyt.domain.enums.SalesTaxSources;
import com.complyt.domain.properties.ComplytIdProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@With
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
public class InternalSalesTaxRates extends TaxRates implements ComplytIdProperty {
    UUID complytId;
    @Id
    String id;
    InternalAddress address;
    InternalRates salesTaxRates;
    InternalEffectiveDates effectiveDates;
    InternalSalesTaxRatesMetaData internalSalesTaxRatesMetaData;
    LocalDateTime createdDate;
}
