package com.complyt.domain.internal_rates;

import com.complyt.domain.enums.SalesTaxSources;
import com.complyt.domain.properties.ComplytIdProperty;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@With
@Value
@AllArgsConstructor
public class InternalRates {
    BigDecimal stateRate;
    BigDecimal countyRate;
    BigDecimal cityRate;
    BigDecimal mtaRate;
    BigDecimal spdRate;
    BigDecimal other1Rate;
    BigDecimal other2Rate;
    BigDecimal other3Rate;
    BigDecimal other4Rate;
    BigDecimal taxRate;
}