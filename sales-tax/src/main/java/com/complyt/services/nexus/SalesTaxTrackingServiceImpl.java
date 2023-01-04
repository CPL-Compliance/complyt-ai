package com.complyt.services.nexus;

import com.complyt.business.nexus.ApplicationDateCreator;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.repositories.SalesTaxTrackingRepository;
import com.complyt.v1.exceptions.ObjectNotFoundException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.function.Function;

@AllArgsConstructor
@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SalesTaxTrackingServiceImpl implements SalesTaxTrackingService {
    
    @NonNull
    SalesTaxTrackingRepository salesTaxTrackingRepository;
    @NonNull
    ApplicationDateCreator applicationDateCreator;

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
        return salesTaxTrackingRepository.findByState(state)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("No SalesTaxTracking with state " + state)));
    }

    @Override
    public Flux<SalesTaxTracking> findAll() {
        return salesTaxTrackingRepository.findAll();
    }

    @Override
    public Mono<SalesTaxTracking> saveWithEconomicQualified(@NonNull SalesTaxTracking salesTaxTracking, @NonNull NexusStateRule stateRule, @NonNull LocalDateTime referenceDate) {
        EconomicNexusTracker newTracker = new EconomicNexusTracker(true, referenceDate);
        LocalDateTime appliedDate = applicationDateCreator.create(stateRule.getTimeFrame(), referenceDate);

        SalesTaxTracking modifiedTracking = salesTaxTracking
                .withEconomicNexusTracker(newTracker)
                .withAppliedDate(appliedDate);

        log.debug("Saving modified sales tax tracking :  " + modifiedTracking);
        return save(modifiedTracking);
    }

    @Override
    public Mono<SalesTaxTracking> update(@NonNull SalesTaxTracking salesTaxTracking, @NonNull String state) {
        return salesTaxTrackingRepository.findByState(state)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("No SalesTaxTracking with state " + state)))
                .map(createFunctionUpdateSalesTaxTracking(salesTaxTracking))
                .flatMap(salesTaxTrackingRepository::save);
    }

    private Function<SalesTaxTracking, SalesTaxTracking> createFunctionUpdateSalesTaxTracking(SalesTaxTracking salesTaxTracking) {
        return salesTaxTrackingInfo -> salesTaxTrackingInfo
                .withAppliedDate(salesTaxTracking.getAppliedDate())
                .withApprovalDate(salesTaxTracking.getApprovalDate())
                .withEnforcesSalesTax(salesTaxTracking.isEnforcesSalesTax())
                .withEconomicNexusTracker(salesTaxTracking.getEconomicNexusTracker())
                .withPhysicalNexusTracker(salesTaxTracking.getPhysicalNexusTracker())
                .withApproved(salesTaxTracking.isApproved())
                .withState(salesTaxTracking.getState());
    }
}
