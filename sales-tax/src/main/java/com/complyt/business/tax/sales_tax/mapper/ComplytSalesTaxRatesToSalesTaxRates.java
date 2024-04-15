package com.complyt.business.tax.sales_tax.mapper;

import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.mappers.ComplytSalesTaxRatesToSalesTaxRatesMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@AllArgsConstructor
public class ComplytSalesTaxRatesToSalesTaxRates {

    @NonNull
    ComplytSalesTaxRatesToSalesTaxRatesMapper complytSalesTaxRatesToSalesTaxRatesMapper;

    public Mono<SalesTaxRates> map(@NonNull ComplytSalesTaxRates complytSalesTaxRates) {
        SalesTaxRates salesTaxRates = complytSalesTaxRatesToSalesTaxRatesMapper.map(complytSalesTaxRates);
        return Mono.just(salesTaxRates);
    }
}
