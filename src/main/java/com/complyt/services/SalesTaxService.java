package com.complyt.services;

import com.complyt.domain.SalesTaxData;
import reactor.core.publisher.Mono;

public interface SalesTaxService {
    Mono<SalesTaxData> findByAddress(String zip, String address, String city, String state);
}
