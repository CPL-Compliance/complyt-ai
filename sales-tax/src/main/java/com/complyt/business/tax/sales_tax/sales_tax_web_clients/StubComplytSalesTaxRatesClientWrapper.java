package com.complyt.business.tax.sales_tax.sales_tax_web_clients;

import com.complyt.annotations.Generated;
import com.complyt.business.tax.SalesTaxRatesWebClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.sales_tax.RatesMetaData;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.transaction.Address;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode
@Generated
public class StubComplytSalesTaxRatesClientWrapper implements SalesTaxRatesWebClientWrapper<ComplytSalesTaxRates> {

    @Override
    public Mono<ComplytSalesTaxRates> findByAddress(String state, String country, String county, String city, String street, String zip, String region, boolean isPartial, LocalDateTime transactionDate) {
        Address address = new Address(
                "Juneau", "USA", "San Joaquin", "Alaska", "2285 Trout St", "99801", "", false
        );

        SalesTaxRates salesTaxRates = new SalesTaxRates(
                new BigDecimal("0.06"), // state
                new BigDecimal("0.0125"), // county
                new BigDecimal("0"), // city
                new BigDecimal("0.005"), // combinedDistrictRate
                new RatesMetaData(
                        new BigDecimal("0"), // cityDistrictRate
                        new BigDecimal("0.005"), // countyDistrictRate,
                        BigDecimal.ZERO
                ),
                null, // mtaRate
                null, // spdRate
                null, // otherRate
                new BigDecimal("0.0775") // taxRate
        );
        ComplytSalesTaxRates complytSalesTaxRates = new ComplytSalesTaxRates(null, address, salesTaxRates);
        return Mono.just(complytSalesTaxRates);
    }

    @Override
    public Mono<ComplytSalesTaxRates> findByAddress(@NonNull Address address, LocalDateTime transactionDate) {
        return findByAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.region(), address.isPartial(), transactionDate);
    }

}