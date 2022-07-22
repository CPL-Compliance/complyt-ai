package com.complyt.services.nexus;

import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.repositories.SalesTaxTrackingRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@AllArgsConstructor
@Slf4j
@Service
public class SalesTaxTrackingServiceImpl implements SalesTaxTrackingService {

    @NonNull
    private SalesTaxTrackingRepository salesTaxTrackingRepository;

    @Override
    public Mono<SalesTaxTracking> findById(@NonNull String id) {
        return salesTaxTrackingRepository.findById(id);
    }

    @Override
    public Mono<SalesTaxTracking> save(@NonNull SalesTaxTracking salesTaxTracking) {
        return salesTaxTrackingRepository.save(salesTaxTracking);
    }

    @Override
    public Mono<SalesTaxTracking> findByState(@NonNull String state) {
        return salesTaxTrackingRepository.findByState(state);
    }

    @Override
    public Flux<SalesTaxTracking> findAll() {
        return salesTaxTrackingRepository.findAll();
    }

    @Override
    public Mono<SalesTaxTracking> saveWithEconomicQualified(@NonNull SalesTaxTracking salesTaxTracking) {
        EconomicNexusTracker newTracker = new EconomicNexusTracker(true, new Date());
        SalesTaxTracking modifiedTracking = salesTaxTracking.withEconomicNexusTracker(newTracker);

        log.debug("Saving modified sales tax tracking :  " + modifiedTracking);
        return save(modifiedTracking).log();
    }
}
