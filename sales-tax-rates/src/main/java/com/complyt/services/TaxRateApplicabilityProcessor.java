package com.complyt.services;

import com.complyt.domain.internal_rates.InternalEffectiveDates;
import com.complyt.domain.internal_rates.InternalRates;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Slf4j
public class TaxRateApplicabilityProcessor {

    private static final LocalDateTime BASE_DATE = LocalDateTime.of(2000, 1,1,0,0);

    public InternalRates processRates(InternalSalesTaxRates internalSalesTaxRates, LocalDateTime transactionDate) {
        InternalRates rates = internalSalesTaxRates.getSalesTaxRates();
        InternalEffectiveDates effectiveDates = internalSalesTaxRates.getEffectiveDates();

        // Ensure transactionDate is not before BASE_DATE
        transactionDate = adjustTransactionDate(transactionDate);

        // If transactionDate is after maxEffectiveDate, return the original rates object
        if (effectiveDates.getMaxEffectiveDate() != null && transactionDate.isAfter(effectiveDates.getMaxEffectiveDate())) {
            log.info("Transaction date is after the maximum effective date; returning original rates.");
            return rates;
        }

        // Calculate individual applicable rates and the total tax rate
        BigDecimal stateRate = getApplicableRate(rates.getStateRate(), effectiveDates.getState(), transactionDate);
        BigDecimal countyRate = getApplicableRate(rates.getCountyRate(), effectiveDates.getCounty(), transactionDate);
        BigDecimal cityRate = getApplicableRate(rates.getCityRate(), effectiveDates.getCity(), transactionDate);
        BigDecimal mtaRate = getApplicableRate(rates.getMtaRate(), effectiveDates.getMta(), transactionDate);
        BigDecimal spdRate = getApplicableRate(rates.getSpdRate(), effectiveDates.getSpd(), transactionDate);
        BigDecimal other1Rate = getApplicableRate(rates.getOther1Rate(), effectiveDates.getOther1(), transactionDate);
        BigDecimal other2Rate = getApplicableRate(rates.getOther2Rate(), effectiveDates.getOther2(), transactionDate);
        BigDecimal other3Rate = getApplicableRate(rates.getOther3Rate(), effectiveDates.getOther3(), transactionDate);
        BigDecimal other4Rate = getApplicableRate(rates.getOther4Rate(), effectiveDates.getOther4(), transactionDate);
        BigDecimal totalTaxRate = calculateTotalTaxRate(stateRate, countyRate, cityRate, mtaRate, spdRate, other1Rate, other2Rate, other3Rate, other4Rate);
        InternalRates applicableRates = new InternalRates(stateRate, countyRate, cityRate, mtaRate, spdRate, other1Rate, other2Rate, other3Rate, other4Rate, totalTaxRate);
        log.info("Tax rates updated by effective date: {}", applicableRates);

        return applicableRates;
    }

    private boolean isRateApplicable(LocalDateTime effectiveDate, LocalDateTime transactionDate) {
        return effectiveDate != null && !transactionDate.isBefore(effectiveDate);
    }

    private BigDecimal getApplicableRate(BigDecimal rate, LocalDateTime effectiveDate, LocalDateTime transactionDate) {
        return isRateApplicable(effectiveDate, transactionDate) ? rate : BigDecimal.ZERO;
    }

    private BigDecimal calculateTotalTaxRate(BigDecimal... rates) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal rate : rates) {
            total = total.add(rate);
        }
        return total.stripTrailingZeros();
    }

    private LocalDateTime adjustTransactionDate(LocalDateTime transactionDate) {
        return transactionDate.isBefore(BASE_DATE) ? BASE_DATE : transactionDate;
    }
}
