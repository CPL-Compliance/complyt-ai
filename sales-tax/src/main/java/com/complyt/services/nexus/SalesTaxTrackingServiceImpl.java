package com.complyt.services.nexus;

import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.business.nexus.ApplicationDateCreator;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.repositories.ClientTrackingRepository;
import com.complyt.repositories.NexusStateRuleRepository;
import com.complyt.repositories.SalesTaxTrackingRepository;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

    @NonNull
    ComplytIdHandler<SalesTaxTracking> complytIdHandler;

    @NonNull
    ClientTrackingRepository clientTrackingRepository;

    @NonNull
    NexusStateRuleRepository nexusStateRuleRepository;

    @Override
    public Mono<SalesTaxTracking> findById(@NonNull String id) {
        return salesTaxTrackingRepository.findById(id);
    }

    @Override
    public Mono<SalesTaxTracking> save(@NonNull SalesTaxTracking salesTaxTracking) {
        return upsertWithoutNexusSummaryIfNeeded(salesTaxTracking);
    }

    @Override
    public Mono<SalesTaxTracking> upsertWithoutNexusSummaryIfNeeded(@NonNull SalesTaxTracking salesTaxTracking) {
        return Mono.just(salesTaxTracking.getNexusStateRule().timeFrame().equals(TimeFrame.PREVIOUS_TWELVE_MONTHS)
                        ? salesTaxTracking.withNexusCalculationSummaries(Map.of())
                        : salesTaxTracking)
                .flatMap(salesTaxTrackingRepository::save)
                .map(upsertedSalesTaxTracking -> upsertedSalesTaxTracking.withNexusCalculationSummaries(salesTaxTracking.getNexusCalculationSummaries()));
    }

    @Override
    public Mono<SalesTaxTracking> addClientAndStateDetails(@NonNull SalesTaxTracking salesTaxTracking) {
        return clientTrackingRepository.findClient()
                .map(salesTaxTracking::withClientTracking)
                .flatMap(salesTaxTrackingWithClient -> nexusStateRuleRepository.findMostRecentByState(salesTaxTracking.getState().getName())
                        .map(salesTaxTrackingWithClient::withNexusStateRule))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));
    }

    @Override
    public Mono<SalesTaxTracking> findByState(@NonNull String state) {
        return salesTaxTrackingRepository.findByState(state);
    }

    @Override
    public Mono<SalesTaxTracking> findByComplytId(@NonNull UUID complytId) {
        return salesTaxTrackingRepository.findByComplytId(complytId);
    }

    @Override
    public Flux<SalesTaxTracking> findAll(int page, int size) {
        return salesTaxTrackingRepository.findAll(page, size);
    }

    @Deprecated
    @Override
    public Mono<SalesTaxTracking> saveWithEconomicQualified(@NonNull SalesTaxTracking salesTaxTracking, @NonNull NexusStateRule stateRule, @NonNull LocalDateTime referenceDate) {
        EconomicNexusTracker newTracker = new EconomicNexusTracker(true, referenceDate);
        LocalDateTime appliedDate = applicationDateCreator.create(stateRule.timeFrame(), referenceDate);

        SalesTaxTracking modifiedTracking = salesTaxTracking
                .withEconomicNexusTracker(newTracker)
                .withAppliedDate(appliedDate);

        return ContextLogger.observeCtx("Saving modified sales tax tracking:  " + modifiedTracking, log::debug)
                .then(save(modifiedTracking));
    }

    @Override
    public Mono<SalesTaxTracking> update(@NonNull SalesTaxTracking salesTaxTracking) {
        return salesTaxTrackingRepository.findByState(salesTaxTracking.getState().getName())
                .switchIfEmpty(Mono.error(new NotFoundException("No salesTaxTracking with state " + salesTaxTracking.getState().getName())))
                .flatMap(createFunctionUpdateSalesTaxTracking(salesTaxTracking))
                .flatMap(this::upsertWithoutNexusSummaryIfNeeded);
    }

    private Function<SalesTaxTracking, Mono<SalesTaxTracking>> createFunctionUpdateSalesTaxTracking(SalesTaxTracking salesTaxTracking) {
        return salesTaxTrackingInfo ->
                addClientAndStateDetails(salesTaxTracking)
                        .map(salesTaxTrackingWithDetails -> new SalesTaxTracking(
                                salesTaxTrackingInfo.getComplytId(), salesTaxTrackingInfo.getId(), salesTaxTrackingWithDetails.getState(),
                                salesTaxTrackingInfo.getTenantId(), salesTaxTrackingWithDetails.getComment(),
                                salesTaxTrackingWithDetails.isEnforcesSalesTax(),
                                salesTaxTrackingWithDetails.getPhysicalNexusTracker(), salesTaxTrackingWithDetails.getEconomicNexusTracker(),
                                salesTaxTrackingWithDetails.getNexusStateRule(), salesTaxTrackingWithDetails.getClientTracking(),
                                salesTaxTrackingWithDetails.getNexusCalculationSummaries(), salesTaxTrackingWithDetails.getTransactionNexusSummaries(),
                                salesTaxTrackingWithDetails.getAppliedDate(), salesTaxTrackingWithDetails.isApproved(),
                                salesTaxTrackingWithDetails.getApprovalDate(),
                                salesTaxTrackingWithDetails.getFilingFrequency()));
    }

    @Override
    public Mono<SalesTaxTracking> checkSalesTaxTrackingNotHavingComplytId(@NonNull final SalesTaxTracking newSalesTaxTracking) {
        return complytIdHandler.checkNewDontHaveComplytId(newSalesTaxTracking);
    }

    @Override
    public Mono<SalesTaxTracking> insertSummariesFromOriginal(@NonNull final SalesTaxTracking checkedSalesTaxTracking, @NonNull final SalesTaxTracking originalSalesTaxTracking) {
        return Mono.just(checkedSalesTaxTracking
                .withTransactionNexusSummaries(originalSalesTaxTracking.getTransactionNexusSummaries() == null ? new HashMap<>() : originalSalesTaxTracking.getTransactionNexusSummaries())
                .withNexusCalculationSummaries(originalSalesTaxTracking.getNexusCalculationSummaries() == null ? new HashMap<>() : originalSalesTaxTracking.getNexusCalculationSummaries()));
    }

    @Override
    public Mono<SalesTaxTracking> updateEconomicNexus(SalesTaxTracking salesTaxTracking) {
        return salesTaxTrackingRepository.updateEconomicNexus(salesTaxTracking);
    }

    @Override
    public Mono<SalesTaxTracking> checkComplytIdOfModifiedEqualsToOriginal(@NonNull final SalesTaxTracking modifiedSalesTaxTracking, @NonNull final SalesTaxTracking originalSalesTaxTracking) {
        return complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(modifiedSalesTaxTracking, originalSalesTaxTracking);
    }

    @Override
    public Mono<SalesTaxTracking> injectDataToNewSalesTaxTracking(@NonNull SalesTaxTracking salesTaxTracking) {
        return addClientAndStateDetails(salesTaxTracking)
                .map(complytIdHandler::insertComplytIdToNew);
    }
}
