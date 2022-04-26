package com.complyt.business.sales_tax;

import com.complyt.domain.Address;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface SalesTaxWebClientWrapper {
    Mono<SalesTaxData> findByAddress(String zip, String address, String city, String state);

    Mono<SalesTaxData> findByAddress(@NonNull final Address address);
}
