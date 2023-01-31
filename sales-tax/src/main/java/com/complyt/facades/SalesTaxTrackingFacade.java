package com.complyt.facades;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.services.nexus.SalesTaxTrackingService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@AllArgsConstructor
public class SalesTaxTrackingFacade {

    @NonNull
    @Qualifier("salesTaxTrackingServiceImpl")
    private SalesTaxTrackingService salesTaxTrackingService;

    public Mono<SalesTaxTracking> findByState(@NonNull String state) {
        return salesTaxTrackingService.findByState(state);
    }

    public Mono<SalesTaxTracking> findByComplytId(@NonNull UUID complytId) {
        return salesTaxTrackingService.findByComplytId(complytId);
    }

    public Mono<SalesTaxTracking> update(@NonNull SalesTaxTracking salesTaxTracking, @NonNull SalesTaxTracking originalSalesTaxTracking, @NonNull String state) {
        return salesTaxTrackingService.checkComplytIdOfModifiedEqualsToOriginal(salesTaxTracking, originalSalesTaxTracking)
                .flatMap(checkedSalesTaxTracking -> salesTaxTrackingService.update(checkedSalesTaxTracking, state));
    }

    public Mono<SalesTaxTracking> save(@NonNull SalesTaxTracking salesTaxTracking) {
        return salesTaxTrackingService.checkSalesTaxTrackingNotHavingComplytId(salesTaxTracking)
                .flatMap(salesTaxTrackingService::injectDataToNewSalesTaxTracking)
                .flatMap(salesTaxTrackingService::save);
    }

    public Flux<SalesTaxTracking> findAll() {
        return salesTaxTrackingService.findAll();
    }

}
