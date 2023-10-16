package com.complyt.services.nexus;

import com.complyt.business.nexus.ApplicationDateCreator;
import com.complyt.business.nexus.checker.NexusChecker;
import com.complyt.business.nexus.data_extractor.NexusCalculator;
<<<<<<< HEAD
<<<<<<< HEAD
import com.complyt.domain.ClientTracking;
=======
import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
>>>>>>> c9ecc4a6 (eyal/com-303-nexus-tracking-details-add-thresholds)
=======
import com.complyt.domain.ClientTracking;
>>>>>>> 1b610118 (merged main)
import com.complyt.domain.decorator.SalesTaxTrackingWithNexusInfo;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.SalesTaxTracking;
<<<<<<< HEAD
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.utils.factory.DateRange;
=======
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.services.ClientTrackingService;
import com.complyt.services.CustomerService;
import com.complyt.services.TransactionService;
>>>>>>> 1b610118 (merged main)
import com.complyt.utils.query.DateRangeStrategy;
import com.complyt.utils.query.NexusTransactionsSearchQueryBuilder;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class NexusService {

<<<<<<< HEAD
    @NonNull
    ApplicationDateCreator applicationDateCreator;
=======
    @NonNull
    ApplicationDateCreator applicationDateCreator;
    @Qualifier("salesTaxTrackingServiceImpl")
    @NonNull
    private SalesTaxTrackingService salesTaxTrackingService;
    @Qualifier("nexusStateRuleServiceImpl")
    @NonNull
    private NexusStateRuleService nexusStateRuleService;
    @Qualifier("customerServiceImpl")
    @NonNull
    private CustomerService customerService;
    @Qualifier("transactionServiceImpl")
    @NonNull
    private TransactionService transactionService;
    @Qualifier("clientTrackingServiceImpl")
    @NonNull
    private ClientTrackingService clientTrackingService;
>>>>>>> 1b610118 (merged main)
    @NonNull
    private NexusCalculator nexusCalculator;
    @NonNull
    private NexusChecker nexusChecker;
    @NonNull
    private NexusTransactionsSearchQueryBuilder nexusTransactionsSearchQueryBuilder;


    public Mono<SalesTaxTrackingWithNexusInfo> hasNexus(@NonNull SalesTaxTracking salesTaxTracking) {
        return Mono.fromCallable(() ->
                new SalesTaxTrackingWithNexusInfo(salesTaxTracking, nexusChecker.hasNexus(salesTaxTracking)));
    }

    public boolean isNexusTrackingCalculationRequired(@NonNull Transaction transaction, SalesTaxTracking salesTaxTracking) {
        return List.of(TransactionType.INVOICE, TransactionType.REFUND).contains(transaction.getTransactionType()) &&
               salesTaxTracking.isEnforcesSalesTax() &&
               salesTaxTracking.getNexusStateRule().customerTypes().contains(transaction.getCustomer().getCustomerType());
    }

<<<<<<< HEAD
    public Mono<SalesTaxTracking> economicNexusQualified(@NonNull SalesTaxTracking salesTaxTracking, @NonNull LocalDateTime referenceDate) {
        EconomicNexusTracker newTracker = new EconomicNexusTracker(true, referenceDate);
        LocalDateTime appliedDate = applicationDateCreator.create(salesTaxTracking.getNexusStateRule().timeFrame(), referenceDate);

<<<<<<< HEAD
        SalesTaxTracking modifiedTracking = salesTaxTracking
                .withEconomicNexusTracker(newTracker)
                .withAppliedDate(appliedDate);

        return Mono.just(modifiedTracking);
=======
        return findRuleByState(state)
                .flatMap(stateRule -> extractTransactionsForCalculation(referenceDate, stateRule)
                        .flatMap(transactions -> aggregateNexusInfo(transactions, stateRule, referenceDate, state)))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));
>>>>>>> c9ecc4a6 (eyal/com-303-nexus-tracking-details-add-thresholds)
    }

    public Mono<SalesTaxTracking> refreshNexusSummary(@NonNull SalesTaxTracking salesTaxTracking, List<Transaction> transactionList, LocalDate refreshDate) {
        return getNexusSummaryDate(salesTaxTracking, LocalDateTime.of(refreshDate, LocalTime.of(23, 59, 59)))
                .flatMap(summaryDateRange -> nexusCalculator.calculateNexusSummary(transactionList, salesTaxTracking, summaryDateRange));
    }

<<<<<<< HEAD
    public Mono<SalesTaxTracking> calculateNexusSummaryFromTransactionSummaries(SalesTaxTracking salesTaxTracking, DateRange summaryDateRange) {
        Mono<SalesTaxTracking> salesTaxTrackingMono = nexusCalculator.calculateNexusSummaryFromTransactionSummaries(salesTaxTracking, summaryDateRange);
        return salesTaxTrackingMono;
    }

    public Mono<SalesTaxTracking> recalculationOfNexusSummaryIfRequired(SalesTaxTracking salesTaxTracking, Mono<SalesTaxTracking> calculationMono) {
        return hasNexus(salesTaxTracking)
                .flatMap(salesTaxTrackingWithNexusInfo -> !salesTaxTrackingWithNexusInfo.isHasNexus() &&
                                                          salesTaxTracking.getNexusStateRule().timeFrame().equals(TimeFrame.PREVIOUS_TWELVE_MONTHS)
                        ? calculationMono
                        : Mono.just(salesTaxTracking));
    }


    public Mono<Query> getTransactionsQueryByNexusCalculation(NexusStateRule nexusStateRule, ClientTracking clientTracking, LocalDate referenceDate) {
        return Mono.just(nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(
                clientTracking.getNexus(), nexusStateRule, LocalDateTime.of(referenceDate, LocalTime.of(23, 59, 59))));
    }

    public Mono<SalesTaxTracking> upsertToNexusTracking(@NonNull Transaction updatedTransaction, @NonNull SalesTaxTracking salesTaxTracking) {
        return getNexusSummaryDate(salesTaxTracking, updatedTransaction.getExternalTimestamps().getCreatedDate())
                .flatMap(summaryDateRange -> recalculationOfNexusSummaryIfRequired(salesTaxTracking, calculateNexusSummaryFromTransactionSummaries(salesTaxTracking, summaryDateRange))
                        .flatMap(salesTaxTrackingWithNexusSummary -> nexusCalculator.subtractTransactionFromNexusSummary(updatedTransaction.getComplytId(), salesTaxTrackingWithNexusSummary, summaryDateRange)
                                .flatMap(salesTaxTrackingAfterSubtraction -> nexusCalculator.addTransactionToNexusSummary(updatedTransaction, salesTaxTrackingAfterSubtraction, summaryDateRange))
                                .flatMap(salesTaxTrackingAfterUpsertion -> Mono.just(nexusChecker.passedThreshold(salesTaxTrackingAfterUpsertion.getNexusCalculationSummaries().get(summaryDateRange.getEnd().toLocalDate()), salesTaxTrackingAfterUpsertion.getNexusStateRule()))
                                        .flatMap(passedThreshold -> passedThreshold
                                                ? economicNexusQualified(salesTaxTrackingAfterUpsertion, updatedTransaction.getExternalTimestamps().getCreatedDate())
                                                : Mono.just(salesTaxTrackingAfterUpsertion)))));
    }

    public Mono<DateRange> getNexusSummaryDate(SalesTaxTracking salesTaxTracking, LocalDateTime referenceDate) {
        return Mono.just(new DateRangeStrategy(salesTaxTracking.getNexusStateRule().timeFrame(),
                salesTaxTracking.getClientTracking().getNexus().getTaxableDate(),
                referenceDate).getDateRange());
    }
=======
    public Mono<List<Transaction>> extractTransactionsForCalculation(LocalDateTime referenceDate, NexusStateRule nexusStateRule) {
        return clientTrackingService.getNexusInfo()
                .flatMap(nexusInfo -> {
                    Query nexusTransactionsSearchQuery = nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(nexusInfo, nexusStateRule, referenceDate);
                    return transactionService.getTransactionsByQuery(nexusTransactionsSearchQuery)
                            .flatMap(singleTransaction -> customerService.findByComplytId(singleTransaction.getCustomerId())
                                    .map(singleTransaction::withCustomer)).collectList();
                });
=======
    public Mono<SalesTaxTracking> addToNexusTracking(@NonNull Transaction transaction, @NonNull SalesTaxTracking salesTaxTracking) {
        return getNexusSummaryDate(salesTaxTracking, transaction.getExternalTimestamps().getCreatedDate()).flatMap(summaryDate ->
                nexusCalculator.addTransactionToNexusSummary(transaction, salesTaxTracking, summaryDate)
                        .map(modifiedSalesTaxTracking -> nexusChecker.passedThreshold(modifiedSalesTaxTracking.getNexusCalculationSummaries().get(summaryDate), salesTaxTracking.getNexusStateRule()))
                        .flatMap(passedThreshold -> passedThreshold ?
                                saveWithEconomicQualified(salesTaxTracking, transaction.getExternalTimestamps().getCreatedDate()) :
                                Mono.just(salesTaxTracking)));
    }

    public Mono<SalesTaxTracking> saveWithEconomicQualified(@NonNull SalesTaxTracking salesTaxTracking, @NonNull LocalDateTime referenceDate) {
        EconomicNexusTracker newTracker = new EconomicNexusTracker(true, referenceDate);
        LocalDateTime appliedDate = applicationDateCreator.create(salesTaxTracking.getNexusStateRule().timeFrame(), referenceDate);

        SalesTaxTracking modifiedTracking = salesTaxTracking
                .withEconomicNexusTracker(newTracker)
                .withAppliedDate(appliedDate);

        return Mono.just(modifiedTracking);
    }

    public Mono<SalesTaxTracking> refreshNexusSummary(@NonNull SalesTaxTracking salesTaxTracking, List<Transaction> transactionList) {
        return nexusCalculator.calculateNexusSummary(transactionList, salesTaxTracking, LocalDateTime.now());
>>>>>>> 1b610118 (merged main)
    }


    public Mono<Query> getTransactionsQueryByStateRule(NexusStateRule nexusStateRule, ClientTracking clientTracking) {
        return Mono.just(nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(clientTracking.getNexus(), nexusStateRule, LocalDateTime.now()));
    }

<<<<<<< HEAD
>>>>>>> c9ecc4a6 (eyal/com-303-nexus-tracking-details-add-thresholds)
=======
    public Mono<SalesTaxTracking> updateToNexusTracking(Transaction updatedTransaction, SalesTaxTracking salesTaxTracking) {
        return getNexusSummaryDate(salesTaxTracking, updatedTransaction.getExternalTimestamps().getCreatedDate())
                .flatMap(summaryDate -> nexusCalculator.subtractTransactionFromNexusSummary(updatedTransaction.getComplytId(), salesTaxTracking, summaryDate)
                        .flatMap(nexusCalculationSummary -> nexusCalculator.addTransactionToNexusSummary(updatedTransaction, salesTaxTracking, summaryDate))
                        .map(modifiedSalesTaxTracking -> nexusChecker.passedThreshold(modifiedSalesTaxTracking.getNexusCalculationSummaries().get(summaryDate), salesTaxTracking.getNexusStateRule()))
                        .flatMap(passedThreshold -> passedThreshold ?
                                saveWithEconomicQualified(salesTaxTracking, updatedTransaction.getExternalTimestamps().getCreatedDate()) :
                                Mono.just(salesTaxTracking)));
    }

    public Mono<LocalDateTime> getNexusSummaryDate(SalesTaxTracking salesTaxTracking, LocalDateTime referenceDate) {
        return Mono.just(new DateRangeStrategy(salesTaxTracking.getNexusStateRule().timeFrame(),
                salesTaxTracking.getClientTracking().getNexus().getTaxableDate(),
                referenceDate).getDateRange().getStart());
    }
>>>>>>> 1b610118 (merged main)
}