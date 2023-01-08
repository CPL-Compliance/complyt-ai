package com.complyt.facades;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.services.nexus.SalesTaxTrackingService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class SalesTaxTrackingFacade {

    @NonNull
    @Qualifier("salesTaxTrackingServiceImpl")
    private SalesTaxTrackingService salesTaxTrackingService;

    public Mono<SalesTaxTracking> findByState(@NonNull String state) {
        return salesTaxTrackingService.findByState(state);
    }

    public Mono<SalesTaxTracking> update(@NonNull SalesTaxTracking salesTaxTracking, @NonNull String state) {
        return salesTaxTrackingService.update(salesTaxTracking, state);
    }

    public Mono<SalesTaxTracking> save(@NonNull SalesTaxTracking salesTaxTracking) {
        return salesTaxTrackingService.save(salesTaxTracking);
    }

    public Flux<SalesTaxTracking> findAll() {
        return salesTaxTrackingService.findAll();
    }

}
