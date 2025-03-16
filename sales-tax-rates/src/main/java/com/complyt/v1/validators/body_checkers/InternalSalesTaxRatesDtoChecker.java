package com.complyt.v1.validators.body_checkers;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalEffectiveDatesDto;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalRatesDto;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalSalesTaxRatesDto;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.actuator.HasFeatures;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@Slf4j
public class InternalSalesTaxRatesDtoChecker implements DtoBodyChecker<InternalSalesTaxRatesDto> {

    @Override
    public Flux<String> check(@NonNull InternalSalesTaxRatesDto salesTaxRatesDto) {
        return Flux.concat(
                checkTaxRateTotal(salesTaxRatesDto.salesTaxRates()),
                checkMaxEffectiveDate(salesTaxRatesDto.effectiveDates())
        );
    }

    /**
     * Ensures that the total taxRate is the sum of all applicable rates and is not null.
     */
    private Mono<String> checkTaxRateTotal(InternalRatesDto rates) {
        if (rates == null) {
            return Mono.just("salesTaxRates " + DtoErrorMessages.NOT_NULL_ERROR);
        }

        BigDecimal calculatedTotal = sumValidRates(rates).stripTrailingZeros();
        if (rates.getTaxRate() == null || !rates.getTaxRate().equals(calculatedTotal)) {
            log.info("Invalid taxRate calculation. Expected: {}, Provided: {}", calculatedTotal, rates.getTaxRate());
            return Mono.just("rates.taxRate " + DtoErrorMessages.INVALID_SUM_ERROR);
        }

        return Mono.empty();
    }

    /**
     * Ensures that `maxEffectiveDate` is the latest date among all provided effective dates.
     */
    private Mono<String> checkMaxEffectiveDate(InternalEffectiveDatesDto effectiveDates) {
        if (effectiveDates == null || effectiveDates.getMaxEffectiveDate() == null) {
            return Mono.just("EffectiveDates " + DtoErrorMessages.NOT_NULL_ERROR);
        }

        Optional<LocalDate> maxDate = getMaxEffectiveDate(effectiveDates);

        if (maxDate.isPresent() && !maxDate.get().equals(LocalDate.parse(effectiveDates.getMaxEffectiveDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))) {
            log.info("Invalid maxEffectiveDate. Expected: {}, Provided: {}", maxDate.orElse(null), effectiveDates.getMaxEffectiveDate());
            return Mono.just(DtoErrorMessages.INVALID_DATE_ERROR);
        }

        return Mono.empty();
    }

    /**
     * Computes the sum of all applicable rates.
     */
    private BigDecimal sumValidRates(InternalRatesDto rates) {
        return Stream.of(
                        rates.getStateRate(), rates.getCountyRate(), rates.getCityRate(),
                        rates.getMtaRate(), rates.getSpdRate(), rates.getOther1Rate(),
                        rates.getOther2Rate(), rates.getOther3Rate(), rates.getOther4Rate()
                )
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Finds the maximum date among all effective dates.
     */
    private Optional<LocalDate> getMaxEffectiveDate(InternalEffectiveDatesDto effectiveDates) {
        return Stream.of(
                        effectiveDates.getState(), effectiveDates.getCounty(), effectiveDates.getCity(),
                        effectiveDates.getMta(), effectiveDates.getSpd(), effectiveDates.getOther1(),
                        effectiveDates.getOther2(), effectiveDates.getOther3(), effectiveDates.getOther4()
                )
                .filter(Objects::nonNull)
                .map(dateStr -> {
                    try {
                        return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    } catch (DateTimeParseException e) {
                        log.warn("Invalid date format: {}", dateStr);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder());
    }
}
