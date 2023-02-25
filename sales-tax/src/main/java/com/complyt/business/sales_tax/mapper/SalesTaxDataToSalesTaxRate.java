package com.complyt.business.sales_tax.mapper;

import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
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

    public Mono<SalesTaxRate> map(@NonNull SalesTaxData salesTaxData) {
        SalesTaxRate salesTaxRate = salesTaxDataToSalesTaxRateMapper.map(salesTaxData);

        if (salesTaxData.isUnincorporated()) {
            return ContextLogger.observeCtx("Unincorporated Address - Setting City and City District Rates to 0 ", log::debug)
                    .then(Mono.just(salesTaxRate.withCityRate(0).withCityDistrictRate(0)));
        }
        return Mono.just(salesTaxRate);
    }
}
