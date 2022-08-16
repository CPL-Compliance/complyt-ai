package com.complyt.services.nexus;

import com.complyt.business.nexus.checker.NexusChecker;
import com.complyt.business.nexus.data_extractor.NexusCalculator;
import com.complyt.business.query.NexusTransactionsSearchQueryBuilder;
import com.complyt.domain.Transaction;
import com.complyt.domain.decorator.SalesTaxTrackingDecorator;
import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.services.ClientTrackingService;
import com.complyt.services.TransactionService;
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

    public Mono<SalesTaxTrackingDecorator> hasNexus(@NonNull Transaction transaction) {
        return findTrackingByState(transaction)
                .map(salesTaxTracking -> {
                    boolean hasNexus = nexusChecker.hasNexus(salesTaxTracking);
                    return new SalesTaxTrackingDecorator(salesTaxTracking, hasNexus);
                });
    }

    public Mono<SalesTaxTracking> calculateNexusTracking(@NonNull Transaction transaction) {
        String state = transaction.getShippingAddress().getState();
        LocalDateTime referenceDate = transaction.getExternalTimeStamps().getCreatedDate();

        return clientTrackingService.getNexusInfo()
                .flatMap(nexusInfo -> findRuleByState(state)
                        .flatMap(stateRule -> {
                            Query nexusTransactionsSearchQuery = nexusTransactionsSearchQueryBuilder.buildNexusTransactionsSearch(nexusInfo, stateRule, referenceDate);
                            return transactionService.getTransactionsByQuery(nexusTransactionsSearchQuery)
                                    .collectList().flatMap(transactions -> aggregateNexusInfo(transactions, stateRule, referenceDate));
                        }));
    }

    public Mono<SalesTaxTracking> aggregateNexusInfo(List<Transaction> transactions, NexusStateRule stateRule, LocalDateTime referenceDate) {
        NexusCalculationSummary summary = nexusCalculator.calculate(transactions, stateRule);
        boolean passedThreshold = nexusChecker.passedThreshold(summary, stateRule);

        return findTrackingByState(stateRule.getState().getAbbreviation())
                .flatMap(salesTaxTracking -> passedThreshold ?
                        salesTaxTrackingService.saveWithEconomicQualified(salesTaxTracking, stateRule, referenceDate) :
                        Mono.just(salesTaxTracking)
                );
    }
}