package com.complyt.services.nexus;

import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.services.crud.CrudService;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;


public interface SalesTaxTrackingService extends CrudService<SalesTaxTracking, String> {

    Mono<SalesTaxTracking> save(@NonNull SalesTaxTracking salesTaxTracking);

    Mono<SalesTaxTracking> upsertWithoutNexusSummaryIfNeeded(@NonNull SalesTaxTracking salesTaxTracking);

    Mono<SalesTaxTracking> addClientAndStateDetails(@NonNull SalesTaxTracking salesTaxTracking);

    Mono<SalesTaxTracking> findByCountryStateAndSubsidiary(@NonNull String country, String state, String subsidiaryId);

    Mono<SalesTaxTracking> findByComplytId(@NonNull UUID complytId);

    Flux<SalesTaxTracking> findAll(int page, int size);

    Mono<SalesTaxTracking> saveWithEconomicQualified(@NonNull SalesTaxTracking salesTaxTracking, @NonNull NexusStateRule stateRule, @NonNull LocalDateTime referenceDate);

    Mono<SalesTaxTracking> injectDataToNewSalesTaxTracking(@NonNull SalesTaxTracking SalesTaxTracking);

    Mono<SalesTaxTracking> checkComplytIdOfModifiedEqualsToOriginal(@NonNull final SalesTaxTracking modifiedSalesTaxTracking, @NonNull final SalesTaxTracking originalSalesTaxTracking);

    Mono<SalesTaxTracking> update(@NonNull SalesTaxTracking salesTaxTracking);

    Mono<SalesTaxTracking> checkSalesTaxTrackingNotHavingComplytId(@NonNull final SalesTaxTracking newSalesTaxTracking);

    Mono<SalesTaxTracking> insertSummariesFromOriginal(@NonNull final SalesTaxTracking checkedSalesTaxTracking, @NonNull final SalesTaxTracking originalSalesTaxTracking);

    Mono<SalesTaxTracking> updateEconomicNexus(SalesTaxTracking salesTaxTracking);

    Mono<SalesTaxTracking> handleSalesTaxTrackingAfterTransactionCalculated(@NonNull SalesTaxTracking salesTaxTracking);

    Mono<SalesTaxTracking> updateRegisteredDateIfIsRegisteredModified(@NonNull SalesTaxTracking salesTaxTracking);

    Mono<SalesTaxTracking> injectRegisteredDateToSalesTaxTracking(@NonNull SalesTaxTracking salesTaxTracking);
}
