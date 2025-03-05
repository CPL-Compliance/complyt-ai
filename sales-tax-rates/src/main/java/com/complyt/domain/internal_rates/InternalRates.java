package com.complyt.domain.internal_rates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

import java.math.BigDecimal;

@With
@AllArgsConstructor
@Data
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