package com.complyt.services.nexus;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.services.CrudService;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface SalesTaxTrackingService extends CrudService<SalesTaxTracking,String> {

    Mono<SalesTaxTracking> save(@NonNull SalesTaxTracking salesTaxTracking);

    Mono<SalesTaxTracking> findByState(@NonNull String state);

    Flux<SalesTaxTracking> findAll();

    Mono<SalesTaxTracking> saveWithEconomicQualified(@NonNull SalesTaxTracking salesTaxTracking);
}
