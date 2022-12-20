package com.complyt.services.nexus;

import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.services.crud.CrudService;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


public interface SalesTaxTrackingService extends CrudService<SalesTaxTracking, String> {

    Mono<SalesTaxTracking> save(@NonNull SalesTaxTracking salesTaxTracking);

    Mono<SalesTaxTracking> findByState(@NonNull String state);

    Flux<SalesTaxTracking> findAll();

    Mono<SalesTaxTracking> saveWithEconomicQualified(@NonNull SalesTaxTracking salesTaxTracking, @NonNull NexusStateRule stateRule, @NonNull LocalDateTime referenceDate);

}
