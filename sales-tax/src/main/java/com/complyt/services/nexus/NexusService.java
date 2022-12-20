package com.complyt.services.nexus;

import com.complyt.business.nexus.checker.NexusChecker;
import com.complyt.business.nexus.data_extractor.NexusCalculator;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionType;
import com.complyt.domain.decorator.SalesTaxTrackingWithNexusInfo;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.services.ClientTrackingService;
import com.complyt.services.TransactionService;
import com.complyt.utils.query.NexusTransactionsSearchQueryBuilder;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class NexusService {

    @Qualifier("salesTaxTrackingServiceImpl")
    @NonNull
    private SalesTaxTrackingService salesTaxTrackingService;

    @Qualifier("nexusStateRuleServiceImpl")
    @NonNull
    private NexusStateRuleService nexusStateRuleService;

    @Qualifier("transactionServiceImpl")
    @NonNull
    private TransactionService transactionService;

    @Qualifier("clientTrackingServiceImpl")
    @NonNull
    private ClientTrackingService clientTrackingService;

    @NonNull
    private NexusCalculator nexusCalculator;

    @NonNull
    private NexusChecker nexusChecker;

    @NonNull
    private NexusTransactionsSearchQueryBuilder nexusTransactionsSearchQueryBuilder;

    public Mono<SalesTaxTracking> findTrackingByState(String state) {
        return salesTaxTrackingService.findByState(state);
    }

    public Mono<SalesTaxTracking> findTrackingByState(Transaction transaction) {
        return salesTaxTrackingService.findByState(transaction.getShippingAddress().getState());
    }

    public Mono<NexusStateRule> findRuleByState(String state) {
        return nexusStateRuleService.findByState(state);
    }

    public Mono<SalesTaxTrackingWithNexusInfo> hasNexus(@NonNull Transaction transaction) {
        return findTrackingByState(transaction)
                .map(salesTaxTracking -> {
                    boolean hasNexus = nexusChecker.hasNexus(salesTaxTracking);
                    return new SalesTaxTrackingWithNexusInfo(salesTaxTracking, hasNexus);
                });
    }

    public boolean isNexusTrackingCalculationRequired(@NonNull Transaction transaction) {
        return transaction.getTransactionType() == TransactionType.INVOICE;
    }

    public Mono<SalesTaxTracking> calculateNexusTracking(@NonNull Transaction transaction) {
        String state = transaction.getShippingAddress().getState();
        LocalDateTime referenceDate = transaction.getExternalTimestamps().getCreatedDate();

        return clientTrackingService.getNexusInfo()
                .flatMap(nexusInfo -> findRuleByState(state)
                        .flatMap(stateRule -> {
                            Query nexusTransactionsSearchQuery = nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(nexusInfo, stateRule, referenceDate);
                            return transactionService.getTransactionsByQuery(nexusTransactionsSearchQuery)
                                    .collectList().flatMap(transactions -> aggregateNexusInfo(transactions, stateRule, referenceDate, state));
                        }));
    }

    public Mono<SalesTaxTracking> aggregateNexusInfo(List<Transaction> transactions, NexusStateRule stateRule, LocalDateTime referenceDate, String state) {
        return nexusCalculator.calculate(transactions, stateRule)
                .map(nexusCalculationSummary -> nexusChecker.passedThreshold(nexusCalculationSummary, stateRule))
                .flatMap(passedThreshold -> findTrackingByState(state)
                        .flatMap(salesTaxTracking -> passedThreshold ?
                                salesTaxTrackingService.saveWithEconomicQualified(salesTaxTracking, stateRule, referenceDate) :
                                Mono.just(salesTaxTracking)
                        ));
    }

}