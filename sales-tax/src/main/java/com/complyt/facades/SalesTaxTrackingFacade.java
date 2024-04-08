package com.complyt.facades;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.services.CustomerService;
import com.complyt.services.TransactionService;
import com.complyt.services.nexus.NexusService;
import com.complyt.services.nexus.SalesTaxTrackingService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class SalesTaxTrackingFacade {

    @NonNull
    @Qualifier("salesTaxTrackingServiceImpl")
    private SalesTaxTrackingService salesTaxTrackingService;
    @NonNull
    private CustomerService customerService;
    @NonNull
    private NexusService nexusService;

    @NonNull
    private TransactionService transactionService;

    public Mono<SalesTaxTracking> findByCountryAndState(@NonNull String country, String state) {
        return salesTaxTrackingService.findByCountryAndState(country, state)
                .flatMap(recalculateCurrentNexusSummaryIfNeeded());
    }

    public Mono<SalesTaxTracking> findByComplytId(@NonNull UUID complytId) {
        return salesTaxTrackingService.findByComplytId(complytId)
                .flatMap(recalculateCurrentNexusSummaryIfNeeded());
    }

    public Mono<SalesTaxTracking> update(@NonNull SalesTaxTracking salesTaxTracking, @NonNull SalesTaxTracking originalSalesTaxTracking) {
        return salesTaxTrackingService.checkComplytIdOfModifiedEqualsToOriginal(salesTaxTracking, originalSalesTaxTracking)
                .flatMap(salesTaxTrackingService::addClientAndStateDetails)
                .flatMap(salesTaxTrackingService::updateRegisteredDateIfIsRegisteredModified)
                .flatMap(salesTaxTrackingWithClientAndStateDetails -> salesTaxTrackingService.insertSummariesFromOriginal(salesTaxTrackingWithClientAndStateDetails, originalSalesTaxTracking))
                .flatMap(salesTaxTrackingService::update)
                .flatMap(recalculateCurrentNexusSummaryIfNeeded());
    }

    public Mono<SalesTaxTracking> save(@NonNull SalesTaxTracking salesTaxTracking) {
        return salesTaxTrackingService.checkSalesTaxTrackingNotHavingComplytId(salesTaxTracking)
                .flatMap(salesTaxTrackingService::injectDataToNewSalesTaxTracking)
                .flatMap(salesTaxTrackingService::save)
                .flatMap(recalculateCurrentNexusSummaryIfNeeded());
    }

    public Flux<SalesTaxTracking> findAll(int page, int size) {
        return salesTaxTrackingService.findAll(page, size)
                .flatMapSequential(recalculateCurrentNexusSummaryIfNeeded());
    }

    private Function<SalesTaxTracking, Mono<SalesTaxTracking>> recalculateCurrentNexusSummaryIfNeeded() {
        return salesTaxTracking -> salesTaxTracking.getNexusStateRule() != null
                ? nexusService.recalculationOfNexusSummaryIfRequired(salesTaxTracking,
                nexusService.getNexusSummaryDate(salesTaxTracking, LocalDateTime.now())
                        .flatMap(dateRangeSummary -> nexusService.calculateNexusSummaryFromTransactionSummaries(salesTaxTracking, dateRangeSummary)))
                : Mono.just(salesTaxTracking);
    }

    public Mono<SalesTaxTracking> refreshNexusSummary(@NonNull String country, String state, @NonNull LocalDate refreshDate) {
        return salesTaxTrackingService.findByCountryAndState(country, state)
                .flatMap(extractedSalesTaxTracking -> nexusService.hasNexus(extractedSalesTaxTracking)
                        .flatMap(salesTaxTrackingWithNexusInfo -> !salesTaxTrackingWithNexusInfo.isHasNexus()
                                ? salesTaxTrackingService.addClientAndStateDetails(salesTaxTrackingWithNexusInfo.getSalesTaxTracking())
                                .flatMap(salesTaxTracking -> nexusService.getTransactionsQueryByNexusCalculation(salesTaxTracking.getNexusStateRule(), salesTaxTracking.getClientTracking(), refreshDate)
                                        .flatMapMany(transactionService::getTransactionsByQuery)
                                        .flatMap(
                                                transaction -> customerService.findByComplytId(transaction.getCustomerId())
                                                .map(transaction::withCustomer))
                                        .collectList()
                                        .flatMap(transactions -> nexusService.refreshNexusSummary(salesTaxTracking, transactions, refreshDate))
                                        .flatMap(salesTaxTrackingService::save))
                                : Mono.just(extractedSalesTaxTracking)));
    }

}
