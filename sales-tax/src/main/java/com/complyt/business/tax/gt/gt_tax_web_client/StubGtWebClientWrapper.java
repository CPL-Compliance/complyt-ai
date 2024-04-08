package com.complyt.business.tax.gt.gt_tax_web_client;

import com.complyt.annotations.Generated;
import com.complyt.business.tax.SalesTaxRatesWebClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.tax.ComplytGtRates;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@Generated
public class StubGtWebClientWrapper implements SalesTaxRatesWebClientWrapper<ComplytGtRates> {

    @Override
    public Mono<ComplytGtRates> findByAddress(String state, String country, String county, String city, String street, String zip, String region, boolean isPartial) {
        return Mono.fromCallable(() -> {
            String json = "{\"gtAddress\": {\"country\": \"Canada\",\"region\": \"Quebec\"},\"gtRates\": {\"taxRate\": \"0.14975\",\"countryRate\": \"0.05\",\"regionRate\": \"0.0975\"}}";
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, ComplytGtRates.class);
        });
    }

    @Override
    public Mono<ComplytGtRates> findByAddress(@NonNull Address address) {
        return Mono.fromCallable(() -> {
            String json = "{\"gtAddress\": {\"country\": \"Canada\",\"region\": \"Quebec\"},\"gtRates\": {\"taxRate\": \"0.14975\",\"countryRate\": \"0.05\",\"regionRate\": \"0.0975\"}}";
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, ComplytGtRates.class);
        });
    }
}
