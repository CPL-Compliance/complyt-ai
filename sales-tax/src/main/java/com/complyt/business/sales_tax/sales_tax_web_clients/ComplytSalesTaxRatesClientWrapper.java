package com.complyt.business.sales_tax.sales_tax_web_clients;

import com.complyt.domain.Address;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.proxies.SalesTaxRatesServiceProxy;
import com.complyt.v1.mappers.ComplytSalesTaxRatesMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ComplytSalesTaxRatesClientWrapper implements SalesTaxWebClientWrapper<ComplytSalesTaxRates> {

    SalesTaxRatesServiceProxy salesTaxRatesServiceProxy;

    public Mono<ComplytSalesTaxRates> findByAddress(String state, String country, String county, String city, String street, String zip) {
        return salesTaxRatesServiceProxy.findByAddress(state, country, county, city, street, zip)
                .map(ComplytSalesTaxRatesMapper.INSTANCE::complytSalesTaxRatesDtoToComplytSalesTaxRates);
    }

    public Mono<ComplytSalesTaxRates> findByAddress(Address address) {
        return findByAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip());
    }

}