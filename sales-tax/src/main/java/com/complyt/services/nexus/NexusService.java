package com.complyt.services.nexus;

import com.complyt.business.nexus.ApplicationDateCreator;
import com.complyt.business.nexus.checker.NexusChecker;
import com.complyt.business.nexus.data_extractor.NexusCalculator;
import com.complyt.domain.ClientTracking;
import com.complyt.domain.decorator.SalesTaxTrackingWithNexusInfo;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.utils.factory.DateRange;
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


    public Mono<SalesTaxTrackingWithNexusInfo> hasNexus(@NonNull SalesTaxTracking salesTaxTracking) {
        return Mono.fromCallable(() ->
                new SalesTaxTrackingWithNexusInfo(salesTaxTracking, nexusChecker.hasNexus(salesTaxTracking)));
    }

    public boolean isNexusTrackingCalculationRequired(@NonNull Transaction transaction, @NonNull SalesTaxTracking salesTaxTracking) {
        return List.of(TransactionType.INVOICE, TransactionType.REFUND).contains(transaction.getTransactionType()) &&
               salesTaxTracking.isEnforcesSalesTax() &&
               salesTaxTracking.getNexusStateRule().customerTypes().contains(transaction.getCustomer().getCustomerType());
    }

    public Mono<SalesTaxTracking> economicNexusQualified(@NonNull SalesTaxTracking salesTaxTracking, @NonNull LocalDateTime referenceDate) {
        EconomicNexusTracker newTracker = new EconomicNexusTracker(true, referenceDate);
        LocalDateTime appliedDate = applicationDateCreator.create(salesTaxTracking.getNexusStateRule().timeFrame(), referenceDate);

        SalesTaxTracking modifiedTracking = salesTaxTracking
                .withEconomicNexusTracker(newTracker)
                .withAppliedDate(appliedDate);

        return Mono.just(modifiedTracking);
    }

    public Mono<SalesTaxTracking> refreshNexusSummary(@NonNull SalesTaxTracking salesTaxTracking, @NonNull List<Transaction> transactionList, @NonNull LocalDate refreshDate) {
        return getSalesTaxTrackingReadyForRecalculation(salesTaxTracking)
                .flatMap(salesTaxTrackingReadyForRefresh -> getNexusSummaryDate(salesTaxTracking, LocalDateTime.of(refreshDate, LocalTime.of(23, 59, 59)))
                        .flatMap(summaryDateRange -> nexusCalculator.calculateNexusSummary(transactionList, salesTaxTrackingReadyForRefresh, summaryDateRange)));
    }

    private Mono<SalesTaxTracking> getSalesTaxTrackingReadyForRecalculation(SalesTaxTracking salesTaxTracking) {
        return Mono.just(salesTaxTracking
                .withTransactionNexusSummaries(salesTaxTracking.getTransactionNexusSummaries() == null
                        ? new HashMap<>() : salesTaxTracking.getTransactionNexusSummaries())
                .withNexusCalculationSummaries(salesTaxTracking.getNexusCalculationSummaries() == null
                        ? new HashMap<>() : salesTaxTracking.getNexusCalculationSummaries()));
    }

    public Mono<SalesTaxTracking> calculateNexusSummaryFromTransactionSummaries(@NonNull SalesTaxTracking salesTaxTracking, @NonNull DateRange summaryDateRange) {
        return getSalesTaxTrackingReadyForRecalculation(salesTaxTracking)
                .flatMap(salesTaxTrackingReadyForCalculation -> nexusCalculator.calculateNexusSummaryFromTransactionSummaries(salesTaxTrackingReadyForCalculation, summaryDateRange));
    }

    public Mono<SalesTaxTracking> recalculationOfNexusSummaryIfRequired(@NonNull SalesTaxTracking salesTaxTracking, @NonNull Mono<SalesTaxTracking> calculationMono) {
        return hasNexus(salesTaxTracking)
                .flatMap(salesTaxTrackingWithNexusInfo -> !salesTaxTrackingWithNexusInfo.isHasNexus() &&
                                                          salesTaxTracking.getNexusStateRule().timeFrame().equals(TimeFrame.PREVIOUS_TWELVE_MONTHS)
                        ? calculationMono
                        : Mono.just(salesTaxTracking));
    }


    public Mono<Query> getTransactionsQueryByNexusCalculation(@NonNull NexusStateRule nexusStateRule, @NonNull ClientTracking clientTracking, @NonNull LocalDate referenceDate) {
        return Mono.just(nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(
                clientTracking.getNexus(), nexusStateRule, LocalDateTime.of(referenceDate, LocalTime.of(23, 59, 59))));
    }

    public Mono<SalesTaxTracking> upsertToNexusTracking(@NonNull Transaction updatedTransaction, @NonNull SalesTaxTracking salesTaxTracking) {
        return getNexusSummaryDate(salesTaxTracking, updatedTransaction.getExternalTimestamps().getCreatedDate())
                .flatMap(summaryDateRange -> getSalesTaxTrackingReadyForRecalculation(salesTaxTracking)
                        .flatMap(salesTaxTrackingReadyForCalculation -> recalculationOfNexusSummaryIfRequired(salesTaxTrackingReadyForCalculation, calculateNexusSummaryFromTransactionSummaries(salesTaxTrackingReadyForCalculation, summaryDateRange))
                                .flatMap(salesTaxTrackingWithNexusSummary -> nexusCalculator.subtractTransactionFromNexusSummary(updatedTransaction.getComplytId(), salesTaxTrackingWithNexusSummary, summaryDateRange)
                                        .flatMap(salesTaxTrackingAfterSubtraction -> nexusCalculator.addTransactionToNexusSummary(updatedTransaction, salesTaxTrackingAfterSubtraction, summaryDateRange))
                                        .flatMap(salesTaxTrackingAfterUpsertion -> Mono.just(nexusChecker.passedThreshold(salesTaxTrackingAfterUpsertion.getNexusCalculationSummaries().get(summaryDateRange.getEnd().toLocalDate()), salesTaxTrackingAfterUpsertion.getNexusStateRule()))
                                                .flatMap(passedThreshold -> passedThreshold
                                                        ? economicNexusQualified(salesTaxTrackingAfterUpsertion, updatedTransaction.getExternalTimestamps().getCreatedDate())
                                                        : Mono.just(salesTaxTrackingAfterUpsertion))))));
    }

    public Mono<DateRange> getNexusSummaryDate(@NonNull SalesTaxTracking salesTaxTracking, @NonNull LocalDateTime referenceDate) {
        return Mono.just(new DateRangeStrategy(salesTaxTracking.getNexusStateRule().timeFrame(),
                salesTaxTracking.getClientTracking().getNexus().getTaxableDate(),
                referenceDate).getDateRange());
    }
}