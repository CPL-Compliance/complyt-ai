package com.complyt.facades;

import com.complyt.domain.nexus.SalesTaxTracking;
<<<<<<< HEAD
<<<<<<< HEAD
import com.complyt.services.CustomerService;
import com.complyt.services.TransactionService;
=======
>>>>>>> c9ecc4a6 (eyal/com-303-nexus-tracking-details-add-thresholds)
=======
import com.complyt.services.TransactionService;
>>>>>>> 1b610118 (merged main)
import com.complyt.services.nexus.NexusService;
import com.complyt.services.nexus.SalesTaxTrackingService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

<<<<<<< HEAD
<<<<<<< HEAD
import java.time.LocalDate;
=======
>>>>>>> c9ecc4a6 (eyal/com-303-nexus-tracking-details-add-thresholds)
import java.time.LocalDateTime;
=======
>>>>>>> 1b610118 (merged main)
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

    @NonNull
    private NexusService nexusService;

    @NonNull
    private TransactionService transactionService;

    public Mono<SalesTaxTracking> findByState(@NonNull String state) {
        return salesTaxTrackingService.findByState(state)
                .flatMap(recalculateCurrentNexusSummaryIfNeeded());
    }

    public Mono<SalesTaxTracking> findByComplytId(@NonNull UUID complytId) {
        return salesTaxTrackingService.findByComplytId(complytId)
                .flatMap(recalculateCurrentNexusSummaryIfNeeded());
    }

    public Mono<SalesTaxTracking> update(@NonNull SalesTaxTracking salesTaxTracking, @NonNull SalesTaxTracking originalSalesTaxTracking) {
        return salesTaxTrackingService.checkComplytIdOfModifiedEqualsToOriginal(salesTaxTracking, originalSalesTaxTracking)
                .flatMap(salesTaxTrackingService::addClientAndStateDetails)
                .flatMap(checkedSalesTaxTracking -> salesTaxTrackingService.insertSummariesFromOriginal(checkedSalesTaxTracking, originalSalesTaxTracking))
                .flatMap(salesTaxTrackingService::update)
                .flatMap(recalculateCurrentNexusSummaryIfNeeded());
    }

    public Mono<SalesTaxTracking> save(@NonNull SalesTaxTracking salesTaxTracking) {
        return salesTaxTrackingService.checkSalesTaxTrackingNotHavingComplytId(salesTaxTracking)
                .flatMap(salesTaxTrackingService::addClientAndStateDetails)
                .flatMap(salesTaxTrackingService::injectDataToNewSalesTaxTracking)
                .flatMap(salesTaxTrackingService::save)
                .flatMap(recalculateCurrentNexusSummaryIfNeeded());
    }

    public Flux<SalesTaxTracking> findAll() {
        return salesTaxTrackingService.findAll()
                .flatMap(recalculateCurrentNexusSummaryIfNeeded());
    }

    private Function<SalesTaxTracking, Mono<SalesTaxTracking>> recalculateCurrentNexusSummaryIfNeeded() {
        return salesTaxTracking -> salesTaxTracking.getNexusStateRule() != null
                ? nexusService.recalculationOfNexusSummaryIfRequired(salesTaxTracking,
                nexusService.getNexusSummaryDate(salesTaxTracking, LocalDateTime.now())
                        .flatMap(dateRangeSummary -> nexusService.calculateNexusSummaryFromTransactionSummaries(salesTaxTracking, dateRangeSummary)))
                : Mono.just(salesTaxTracking) ;
    }

    public Mono<SalesTaxTracking> refreshNexusSummary(@NonNull String state, LocalDate refreshDate) {
            return salesTaxTrackingService.findByState(state)
                    .flatMap(salesTaxTrackingService::addClientAndStateDetails)
                    .flatMap(salesTaxTracking -> nexusService.getTransactionsQueryByNexusCalculation(salesTaxTracking.getNexusStateRule(), salesTaxTracking.getClientTracking(), refreshDate)
                            .flatMapMany(transactionService::getTransactionsByQuery)
                            .flatMap(transaction -> customerService.findByComplytId(transaction.getCustomerId())
                                    .map(transaction::withCustomer))
                            .collectList()
                        .flatMap(transactions -> nexusService.refreshNexusSummary(salesTaxTracking, transactions, refreshDate))
                        .flatMap(salesTaxTrackingService::update));
    }

    public Mono<SalesTaxTracking> refreshNexusSummary(@NonNull String state) {
        return salesTaxTrackingService.findByState(state)
                .flatMap(salesTaxTracking -> nexusService.getTransactionsQueryByStateRule(salesTaxTracking.getNexusStateRule(), salesTaxTracking.getClientTracking())
                        .flatMapMany(transactionService::getTransactionsByQuery).collectList()
                        .flatMap(transactions -> nexusService.refreshNexusSummary(salesTaxTracking, transactions))
                        .flatMap(refreshedSalesTaxTracking -> salesTaxTrackingService.update(refreshedSalesTaxTracking, state)));
    }

}
