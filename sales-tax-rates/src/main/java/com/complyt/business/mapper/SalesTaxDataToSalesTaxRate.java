package com.complyt.business.mapper;

import com.complyt.domain.RatesMetaData;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.mappers.SalesTaxDataToSalesTaxRateMapper;
import com.complyt.utils.observability.ContextLogger;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SalesTaxDataToSalesTaxRate {

    @NonNull
    SalesTaxDataToSalesTaxRateMapper getBestMatchDataToSalesTaxRateMapper;

    public Mono<SalesTaxRates> map(@NonNull SalesTaxData salesTaxData) {
        SalesTaxRates salesTaxRates = getBestMatchDataToSalesTaxRateMapper.map(salesTaxData);

        if (salesTaxData.isUnincorporated()) {
            return ContextLogger.observeCtx("Unincorporated Address - Setting City and City District Rates to 0 ", log::debug)
                    .then(Mono.just(handleUnincorporatedAddress(salesTaxRates)));
        }

        return Mono.just(salesTaxRates);
    }

    private SalesTaxRates handleUnincorporatedAddress(SalesTaxRates salesTaxRates) {
        if (salesTaxRates.ratesMetaData() == null) {
            BigDecimal modifiedTaxRate = salesTaxRates.taxRate().subtract(salesTaxRates.cityRate());

            return salesTaxRates
                    .withTaxRate(modifiedTaxRate)
                    .withCityRate(BigDecimal.ZERO);
        }
        BigDecimal modifiedTaxRate = salesTaxRates.taxRate().subtract(salesTaxRates.cityRate()).subtract(salesTaxRates.ratesMetaData().cityDistrictRate());
        RatesMetaData modifiedRatesMetaData = salesTaxRates.ratesMetaData().withCityDistrictRate(BigDecimal.ZERO);

        return salesTaxRates
                .withTaxRate(modifiedTaxRate)
                .withCityRate(BigDecimal.ZERO)
                .withRatesMetaData(modifiedRatesMetaData);
    }

}