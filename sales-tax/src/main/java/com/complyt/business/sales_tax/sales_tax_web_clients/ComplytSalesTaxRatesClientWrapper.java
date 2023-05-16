package com.complyt.business.sales_tax.sales_tax_web_clients;

import com.complyt.domain.Address;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.proxies.SalesTaxRatesServiceProxy;
import com.complyt.v1.mappers.ComplytSalesTaxRatesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ComplytSalesTaxRatesClientWrapper implements SalesTaxWebClientWrapper<ComplytSalesTaxRates> {

    @Autowired
    SalesTaxRatesServiceProxy salesTaxRatesServiceProxy;

    public Mono<ComplytSalesTaxRates> findByAddress(String state, String country, String county, String city, String street, String zip) {
        return salesTaxRatesServiceProxy.findByAddress(state, country, county, city, street, zip)
                .map(ComplytSalesTaxRatesMapper.INSTANCE::complytSalesTaxRatesDtoToComplytSalesTaxRates);
    }

    public Mono<ComplytSalesTaxRates> findByAddress(Address address) {
        return findByAddress(address.getState(), address.getCountry(), address.getCounty(), address.getCity(), address.getStreet(), address.getZip());
    }

}
