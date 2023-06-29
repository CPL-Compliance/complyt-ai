package com.complyt.business.sales_tax.sales_tax_web_clients;

import com.complyt.annotations.Generated;
import com.complyt.domain.Address;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.fast_tax.TaxInfoItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@Generated
public class StubComplytSalesTaxRatesClientWrapper implements SalesTaxWebClientWrapper<ComplytSalesTaxRates> {

    @Override
    public Mono<ComplytSalesTaxRates> findByAddress(String state, String country, String county, String city, String street, String zip, boolean isPartial) {
        return null;
    }

    @Override
    public Mono<ComplytSalesTaxRates> findByAddress(@NonNull Address address) {
        return Mono.fromCallable(() -> {
            String json = "{\"address\": {\"city\": \"Acampo\",\"state\": \"CA\",\"zip\": \"95220\",\"county\": \"San Joaquin\",\"country\": \"US\",\"street\": \"7498 N Remington Ave\"},\"salesTaxRates\": {\"cityRate\": \"0\",\"countyRate\": \"0.0125\",\"combinedDistrictRate\": \"0.005\",\"stateRate\": \"0.06\",\"taxRate\": \"0.0775\",\"ratesMetaData\": {\"cityDistrictRate\": \"0\",\"countyDistrictRate\": \"0.005\"}}}";
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, ComplytSalesTaxRates.class);
        });
    }
}