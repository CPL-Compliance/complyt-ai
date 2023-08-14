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
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SalesTaxDataToSalesTaxRate {

    @NonNull
    SalesTaxDataToSalesTaxRateMapper salesTaxDataToSalesTaxRateMapper;

    public Mono<SalesTaxRates> map(@NonNull SalesTaxData salesTaxData) {
        SalesTaxRates salesTaxRates = salesTaxDataToSalesTaxRateMapper.map(salesTaxData);

        if (salesTaxData.isUnincorporated()) {
            return ContextLogger.observeCtx("Unincorporated Address - Setting City and City District Rates to 0 ", log::debug)
                    .then(Mono.just(handleUnincorporatedAddress(salesTaxRates)));
        }

        return Mono.just(salesTaxRates);
    }

    private SalesTaxRates handleUnincorporatedAddress(SalesTaxRates salesTaxRates) {
        if (salesTaxRates.ratesMetaData() == null) {
            double modifiedTaxRate = salesTaxRates.taxRate() - salesTaxRates.cityRate();

            return salesTaxRates
                    .withTaxRate(modifiedTaxRate)
                    .withCityRate(0);
        }
        double modifiedTaxRate = salesTaxRates.taxRate() - salesTaxRates.cityRate() - salesTaxRates.ratesMetaData().cityDistrictRate();
        RatesMetaData modifiedRatesMetaData = salesTaxRates.ratesMetaData().withCityDistrictRate(0);

        return salesTaxRates
                .withTaxRate(modifiedTaxRate)
                .withCityRate(0)
                .withRatesMetaData(modifiedRatesMetaData);
    }

}