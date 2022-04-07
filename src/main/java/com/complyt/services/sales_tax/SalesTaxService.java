package com.complyt.services.sales_tax;

import com.complyt.domain.SalesTaxData;
import reactor.core.publisher.Mono;

public interface SalesTaxService {
    Mono<SalesTaxData> findByAddress(String zip, String address, String city, String state);
}
