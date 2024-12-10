package com.complyt.services.nexus;

import com.complyt.business.nexus.ApplicationDateCreator;
import com.complyt.business.nexus.checker.NexusChecker;
import com.complyt.business.nexus.data_extractor.NexusCalculator;
import com.complyt.business.timestamps_injection.provider.NexusAppliedDateProvider;
import com.complyt.domain.ClientTracking;
import com.complyt.domain.decorator.SalesTaxTrackingWithNexusInfo;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.utils.factory.DateRange;
import com.complyt.utils.query.DateRangeStrategy;
import com.complyt.utils.query.NexusTransactionsSearchQueryBuilder;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class NexusService {

    @NonNull
    ApplicationDateCreator applicationDateCreator;
    @NonNull
    private NexusCalculator nexusCalculator;
    @NonNull
    private NexusChecker nexusChecker;
    @NonNull
    private NexusTransactionsSearchQueryBuilder nexusTransactionsSearchQueryBuilder;
    @NonNull
    private NexusAppliedDateProvider nexusAppliedDateProvider;


    public Mono<SalesTaxTrackingWithNexusInfo> salesTaxTrackingWithNexusIndication(@NonNull SalesTaxTracking salesTaxTracking) {
        return Mono.fromCallable(() ->
                new SalesTaxTrackingWithNexusInfo(salesTaxTracking, nexusChecker.hasNexus(salesTaxTracking)));
    }

    public boolean isNexusTrackingCalculationRequired(@NonNull Transaction transaction, @NonNull SalesTaxTracking salesTaxTracking) {
        return List.of(TransactionType.INVOICE, TransactionType.REFUND, TransactionType.TAXABLE_REFUND).contains(transaction.getTransactionType()) &&
                salesTaxTracking.getNexusStateRule().customerTypes().contains(transaction.getCustomer().getCustomerType());
    }

    public Mono<SalesTaxTracking> economicNexusQualified(@NonNull SalesTaxTracking salesTaxTracking, @NonNull LocalDateTime referenceDate) {
        EconomicNexusTracker newTracker = new EconomicNexusTracker(true, referenceDate);
        LocalDateTime updatedAppliedDate = applicationDateCreator.create(salesTaxTracking.getNexusStateRule().timeFrame(), referenceDate);
        LocalDateTime appliedDate = nexusAppliedDateProvider.getAppliedDate(salesTaxTracking, updatedAppliedDate);

        SalesTaxTracking modifiedTracking = salesTaxTracking
                .setEconomicNexusTracker(newTracker)
                .setAppliedDate(appliedDate);

        return ContextLogger.observeCtx("update economicNexusTracker " + newTracker, log::info)
                .then(Mono.just(modifiedTracking));
    }

    public Mono<SalesTaxTracking> refreshNexusSummary(@NonNull SalesTaxTracking salesTaxTracking, @NonNull List<Transaction> transactions, LocalDate refreshDate) {
        return prepareSalesTaxTrackingForRefresh(salesTaxTracking, refreshDate)
                .flatMap(salesTaxTrackingReadyForRefresh -> processTransactionsForRefresh(salesTaxTrackingReadyForRefresh, transactions));
    }

    private Mono<SalesTaxTracking> prepareSalesTaxTrackingForRefresh(SalesTaxTracking salesTaxTracking, LocalDate refreshDate) {
        return Mono.justOrEmpty(refreshDate)
                .flatMap(date -> {
                    LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(23, 59, 59));
                    return getNexusSummaryDate(salesTaxTracking, dateTime)
                            .flatMap(dateRange -> nexusCalculator.initNexusCalculationSummaryByDateRange(salesTaxTracking, dateRange));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    salesTaxTracking.setNexusCalculationSummaries(new HashMap<>())
                    .setTransactionNexusSummaries(new HashMap<>())
                    .setEconomicNexusTracker(EconomicNexusTracker.build());
                    return Mono.just(salesTaxTracking);
                }));
    }

    private Mono<SalesTaxTracking> processTransactionsForRefresh(SalesTaxTracking salesTaxTracking, List<Transaction> transactions) {
        return Flux.fromIterable(transactions)
                .flatMapSequential(transaction -> upsertTransactionToNexusTrackingForRefresh(transaction, salesTaxTracking)
                        .filterWhen(salesTaxTrackingWithNexusTracking -> Mono.just(nexusChecker.hasEconomicNexus(salesTaxTrackingWithNexusTracking))))
                .next()
                .defaultIfEmpty(salesTaxTracking);
    }

    private Mono<SalesTaxTracking> upsertTransactionToNexusTrackingForRefresh(Transaction transaction, SalesTaxTracking salesTaxTracking) {
        return getNexusSummaryDate(salesTaxTracking, transaction.getExternalTimestamps().getCreatedDate())
                .flatMap(summaryDateRange -> isNexusTrackingCalculationRequired(transaction, salesTaxTracking) ?
                        (nexusCalculator.addTransactionToNexusSummary(transaction, salesTaxTracking, summaryDateRange)
                                .flatMap(salesTaxTrackingAfterInsertion -> checkAndHandleThreshold(salesTaxTrackingAfterInsertion, transaction, summaryDateRange)))
                        : Mono.just(salesTaxTracking));
    }

    private Mono<SalesTaxTracking> getSalesTaxTrackingReadyForRecalculation(SalesTaxTracking salesTaxTracking) {
        return Mono.just(salesTaxTracking
                .setTransactionNexusSummaries(salesTaxTracking.getTransactionNexusSummaries() == null
                        ? new HashMap<>() : salesTaxTracking.getTransactionNexusSummaries())
                .setNexusCalculationSummaries(salesTaxTracking.getNexusCalculationSummaries() == null
                        ? new HashMap<>() : salesTaxTracking.getNexusCalculationSummaries()));
    }

    public Mono<SalesTaxTracking> calculateNexusSummaryFromTransactionSummaries(@NonNull SalesTaxTracking salesTaxTracking, @NonNull DateRange summaryDateRange) {
        return getSalesTaxTrackingReadyForRecalculation(salesTaxTracking)
                .flatMap(salesTaxTrackingReadyForCalculation -> nexusCalculator.calculateNexusSummaryFromTransactionSummaries(salesTaxTrackingReadyForCalculation, summaryDateRange));
    }

    public Mono<SalesTaxTracking> recalculationOfNexusSummaryIfRequired(@NonNull SalesTaxTracking salesTaxTracking, @NonNull Mono<SalesTaxTracking> calculationMono) {
        return salesTaxTrackingWithNexusIndication(salesTaxTracking)
                .flatMap(salesTaxTrackingWithNexusInfo -> !salesTaxTrackingWithNexusInfo.isHasNexus() &&
                        salesTaxTracking.getNexusStateRule().timeFrame().equals(TimeFrame.PREVIOUS_TWELVE_MONTHS)
                        ? calculationMono
                        : Mono.just(salesTaxTracking));
    }

    public Mono<Query> getTransactionsQueryByNexusCalculation(@NonNull NexusStateRule nexusStateRule, @NonNull ClientTracking clientTracking, LocalDate referenceDate, String subsidiary) {
        return Mono.just(nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(
                clientTracking.getNexus(), nexusStateRule, referenceDate, subsidiary));
    }

    public Mono<SalesTaxTracking> upsertToNexusTracking(@NonNull Transaction updatedTransaction, @NonNull SalesTaxTracking salesTaxTracking) {
        return getNexusSummaryDate(salesTaxTracking, updatedTransaction.getExternalTimestamps().getCreatedDate())
                .flatMap(summaryDateRange -> getSalesTaxTrackingReadyForRecalculation(salesTaxTracking)
                        .flatMap(salesTaxTrackingReadyForCalculation -> recalculationOfNexusSummaryIfRequired(salesTaxTrackingReadyForCalculation, calculateNexusSummaryFromTransactionSummaries(salesTaxTrackingReadyForCalculation, summaryDateRange))
                                .flatMap(salesTaxTrackingWithNexusSummary -> nexusCalculator.subtractTransactionFromNexusSummary(updatedTransaction.getComplytId(), salesTaxTrackingWithNexusSummary, summaryDateRange)
                                        .flatMap(salesTaxTrackingAfterSubtraction ->
                                                isNexusTrackingCalculationRequired(updatedTransaction, salesTaxTrackingAfterSubtraction)
                                                        ? nexusCalculator.addTransactionToNexusSummary(updatedTransaction, salesTaxTrackingAfterSubtraction, summaryDateRange)
                                                        : Mono.just(salesTaxTrackingAfterSubtraction))
                                        .flatMap(salesTaxTrackingAfterUpsertion -> checkAndHandleThreshold(salesTaxTrackingAfterUpsertion, updatedTransaction, summaryDateRange)))));
    }

    public Mono<DateRange> getNexusSummaryDate(@NonNull SalesTaxTracking salesTaxTracking, @NonNull LocalDateTime referenceDate) {
        return Mono.just(new DateRangeStrategy(salesTaxTracking.getNexusStateRule().timeFrame(),
                salesTaxTracking.getClientTracking().getNexus().getTaxableDate(),
                referenceDate).getDateRange());
    }

    public Mono<SalesTaxTracking> removeFromNexusTracking(@NonNull Transaction cancelledTransaction, @NonNull SalesTaxTracking salesTaxTracking) {
        return getNexusSummaryDate(salesTaxTracking, cancelledTransaction.getExternalTimestamps().getCreatedDate())
                .flatMap(summaryDateRange -> getSalesTaxTrackingReadyForRecalculation(salesTaxTracking)
                        .flatMap(salesTaxTrackingReadyForCalculation -> recalculationOfNexusSummaryIfRequired(salesTaxTrackingReadyForCalculation, calculateNexusSummaryFromTransactionSummaries(salesTaxTrackingReadyForCalculation, summaryDateRange))
                                .flatMap(salesTaxTrackingWithNexusSummary -> nexusCalculator.subtractTransactionFromNexusSummary(cancelledTransaction.getComplytId(), salesTaxTrackingWithNexusSummary, summaryDateRange)
                                        .flatMap(salesTaxTrackingAfterUpsertion -> checkAndHandleThreshold(salesTaxTrackingAfterUpsertion, cancelledTransaction, summaryDateRange)))));
    }

    private Mono<SalesTaxTracking> checkAndHandleThreshold(SalesTaxTracking salesTaxTracking, Transaction transaction, DateRange dateRange) {
        return Mono.just(nexusChecker.passedThreshold(salesTaxTracking, dateRange))
                .flatMap(passedThreshold -> passedThreshold ? economicNexusQualified(salesTaxTracking, transaction.getExternalTimestamps().getCreatedDate())
                        : Mono.just(salesTaxTracking));
    }
}