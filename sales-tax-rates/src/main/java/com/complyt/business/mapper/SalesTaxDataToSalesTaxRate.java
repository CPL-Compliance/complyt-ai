package com.complyt.business.mapper;

import com.complyt.domain.SalesTaxData;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.mappers.sales_tax.SalesTaxDataToSalesTaxRateMapper;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@AllArgsConstructor
public class SalesTaxDataToSalesTaxRate {

    @NonNull
    SalesTaxDataToSalesTaxRateMapper salesTaxDataToSalesTaxRateMapper;

    public Mono<SalesTaxRates> map(@NonNull SalesTaxData salesTaxData) {
        SalesTaxRates salesTaxRates = salesTaxDataToSalesTaxRateMapper.map(salesTaxData);

        if (salesTaxData.isUnincorporated()) {
            return ContextLogger.observeCtx("Unincorporated Address - Setting City and City District Rates to 0 ", log::debug)
                    .then(Mono.just(salesTaxRates.withCityRate(0).withCityDistrictRate(0)));
        }
        return Mono.just(salesTaxRates);
    }
}