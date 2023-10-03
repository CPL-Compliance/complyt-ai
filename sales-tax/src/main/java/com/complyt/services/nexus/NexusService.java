package com.complyt.services.nexus;

import com.complyt.business.nexus.ApplicationDateCreator;
import com.complyt.business.nexus.checker.NexusChecker;
import com.complyt.business.nexus.data_extractor.NexusCalculator;
import com.complyt.domain.ClientTracking;
import com.complyt.domain.decorator.SalesTaxTrackingWithNexusInfo;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.utils.query.DateRangeStrategy;
import com.complyt.utils.query.NexusTransactionsSearchQueryBuilder;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
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

    public boolean isNexusTrackingCalculationRequired(@NonNull Transaction transaction, SalesTaxTracking salesTaxTracking) {
        return transaction.getTransactionType() == TransactionType.INVOICE && salesTaxTracking.isEnforcesSalesTax();
    }

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
    }


    public Mono<Query> getTransactionsQueryByNexusCalculation(NexusStateRule nexusStateRule, ClientTracking clientTracking) {
        return Mono.just(nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(clientTracking.getNexus(), nexusStateRule, LocalDateTime.now()));
    }

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
}