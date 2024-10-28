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
import java.util.Map;
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

    public Mono<SalesTaxTracking> findByCountryAndState(@NonNull String country, String state, String subsidiary) {
        return salesTaxTrackingService.findByCountryStateAndSubsidiary(country, state, subsidiary)
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
                .flatMap(salesTaxTrackingService::updateAppliedDateIfIsPhysicalNexusEstablished)
                .flatMap(salesTaxTrackingWithClientAndStateDetails -> salesTaxTrackingService.insertSummariesFromOriginal(salesTaxTrackingWithClientAndStateDetails, originalSalesTaxTracking))
                .flatMap(salesTaxTrackingService::update)
                .flatMap(recalculateCurrentNexusSummaryIfNeeded());
    }

    public Mono<SalesTaxTracking> save(@NonNull SalesTaxTracking salesTaxTracking) {
        return salesTaxTrackingService.checkSalesTaxTrackingNotHavingComplytId(salesTaxTracking)
                .flatMap(salesTaxTrackingService::injectDataToNewSalesTaxTracking)
                .flatMap(salesTaxTrackingService::updateAppliedDateIfIsPhysicalNexusEstablished)
                .flatMap(salesTaxTrackingService::save)
                .flatMap(recalculateCurrentNexusSummaryIfNeeded());
    }

    public Flux<SalesTaxTracking> findAll(int page, int size, Map<String, String> filterMap, String sortOrder, String sortBy) {
        return salesTaxTrackingService.findAll(page, size, filterMap, sortOrder, sortBy)
                .flatMapSequential(recalculateCurrentNexusSummaryIfNeeded());
    }

    private Function<SalesTaxTracking, Mono<SalesTaxTracking>> recalculateCurrentNexusSummaryIfNeeded() {
        return salesTaxTracking -> salesTaxTracking.getNexusStateRule() != null
                ? nexusService.recalculationOfNexusSummaryIfRequired(salesTaxTracking,
                nexusService.getNexusSummaryDate(salesTaxTracking, LocalDateTime.now())
                        .flatMap(dateRangeSummary -> nexusService.calculateNexusSummaryFromTransactionSummaries(salesTaxTracking, dateRangeSummary)))
                : Mono.just(salesTaxTracking);
    }

    /**
     * <a href="https://coda.io/d/Refresh-Flow_dx9KY9BHPh8/Refresh-Flow_suYGv#_luR43">Coda documentation</a>
     */
    public Mono<SalesTaxTracking> refreshNexusSummary(@NonNull String country, String state, LocalDate refreshDate, String subsidiary) {
        return salesTaxTrackingService.findByCountryStateAndSubsidiary(country, state, subsidiary)
                .flatMap(extractedSalesTaxTracking -> nexusService.salesTaxTrackingWithNexusIndication(extractedSalesTaxTracking)
                        .flatMap(salesTaxTrackingWithNexusInfo -> salesTaxTrackingService.addClientAndStateDetails(salesTaxTrackingWithNexusInfo.getSalesTaxTracking())
                                .flatMap(salesTaxTracking -> nexusService.getTransactionsQueryByNexusCalculation(salesTaxTracking.getNexusStateRule(), salesTaxTracking.getClientTracking(), refreshDate, salesTaxTracking.getSubsidiary())
                                        .flatMapMany(transactionService::getTransactionsByQuery)
                                        .flatMapSequential(transaction -> customerService.findByComplytId(transaction.getCustomerId())
                                                .map(transaction::setCustomer))
                                        .collectList()
                                        .flatMap(transactions -> nexusService.refreshNexusSummary(salesTaxTracking, transactions, refreshDate))
                                        .flatMap(salesTaxTrackingService::save))));
    }

}