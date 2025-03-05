package com.complyt.domain.internal_rates;


import com.complyt.domain.TaxRates;
import com.complyt.domain.enums.SalesTaxSources;
import com.complyt.domain.properties.ComplytIdProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@With
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@ToString
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InternalSalesTaxRates extends TaxRates implements ComplytIdProperty {
    UUID complytId;
    @Id
    String id;
    InternalAddress address;
    InternalRates salesTaxRates;
    InternalEffectiveDates effectiveDates;
    InternalSalesTaxRatesMetaData internalSalesTaxRatesMetaData;
    LocalDateTime createdDate;
    LocalDateTime expiredDate;
}
