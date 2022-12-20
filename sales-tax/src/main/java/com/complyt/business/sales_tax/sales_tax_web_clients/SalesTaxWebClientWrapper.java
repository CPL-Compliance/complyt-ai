package com.complyt.business.sales_tax.sales_tax_web_clients;

import com.complyt.domain.Address;
import com.complyt.domain.sales_tax.SalesTaxData;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface SalesTaxWebClientWrapper {
    Mono<SalesTaxData> findByAddress(String zip, String address, String city, String state);

    Mono<SalesTaxData> findByAddress(@NonNull final Address address);
}