package com.complyt.facades;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.services.ClientTrackingService;
import com.complyt.services.TransactionService;
import com.complyt.services.nexus.NexusService;
import com.complyt.services.nexus.NexusStateRuleService;
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
    @Qualifier("nexusStateRuleServiceImpl")
    @NonNull
    private NexusStateRuleService nexusStateRuleService;
    @Qualifier("clientTrackingServiceImpl")
    @NonNull
    private ClientTrackingService clientTrackingService;
    @NonNull
    private NexusService nexusService;

    @NonNull
    private TransactionService transactionService;

    public Mono<SalesTaxTracking> findByState(@NonNull String state) {
        return salesTaxTrackingService.findByState(state);
    }

    public Mono<SalesTaxTracking> findByComplytId(@NonNull UUID complytId) {
        return salesTaxTrackingService.findByComplytId(complytId);
    }

    public Mono<SalesTaxTracking> update(@NonNull SalesTaxTracking salesTaxTracking, @NonNull SalesTaxTracking originalSalesTaxTracking, @NonNull String state) {
        return salesTaxTrackingService.checkComplytIdOfModifiedEqualsToOriginal(salesTaxTracking, originalSalesTaxTracking)
                .flatMap(checkedSalesTaxTracking ->
                        nexusStateRuleService.findByState(salesTaxTracking.getState().getName())
                                .map(checkedSalesTaxTracking::withNexusStateRule)
                                .flatMap(salesTaxTrackingWithStateInfo -> clientTrackingService.getClientTracking()
                                        .map(salesTaxTrackingWithStateInfo::withClientTracking))
                                .flatMap(salesTaxTrackingWithStateAndClientInfo ->
                                        salesTaxTrackingService.update(salesTaxTrackingWithStateAndClientInfo, state)));
    }

    public Mono<SalesTaxTracking> save(@NonNull SalesTaxTracking salesTaxTracking) {
        return salesTaxTrackingService.checkSalesTaxTrackingNotHavingComplytId(salesTaxTracking)
                .flatMap(checkedSalesTaxTracking -> nexusStateRuleService.findByState(salesTaxTracking.getState().getName())
                        .map(checkedSalesTaxTracking::withNexusStateRule)
                        .flatMap(salesTaxTrackingWithStateInfo -> clientTrackingService.getClientTracking()
                                .map(salesTaxTrackingWithStateInfo::withClientTracking)
                                .flatMap(salesTaxTrackingService::injectDataToNewSalesTaxTracking))
                        .flatMap(salesTaxTrackingService::save));
    }

    public Flux<SalesTaxTracking> findAll() {
        return salesTaxTrackingService.findAll();
    }

    public Mono<SalesTaxTracking> refreshNexusSummary(@NonNull String state) {
        return salesTaxTrackingService.findByState(state)
                .flatMap(salesTaxTracking -> nexusService.getTransactionsQueryByNexusCalculation(salesTaxTracking.getNexusStateRule(), salesTaxTracking.getClientTracking())
                        .flatMapMany(transactionService::getTransactionsByQuery).collectList()
                        .flatMap(transactions -> nexusService.refreshNexusSummary(salesTaxTracking, transactions))
                        .flatMap(refreshedSalesTaxTracking -> salesTaxTrackingService.update(refreshedSalesTaxTracking, state)));
    }

}
