package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.ItemsNexusStateRuleQualificationChecker;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.TransactionNexusSummary;
import com.complyt.domain.transaction.Transaction;
import com.complyt.utils.factory.NexusAmountAggregatorFactory;
import lombok.NonNull;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class NexusTransactionSummaryCalculator {

//    @NonNull
//    private ItemsNexusStateRuleQualificationChecker itemsNexusStateRuleQualificationChecker;

    @NonNull
    private NexusAmountAggregatorFactory nexusAmountAggregatorFactory;

    public Mono<TransactionNexusSummary> extract(@NonNull Transaction transaction, @NonNull NexusStateRule nexusStateRule) {
        return Mono.just(nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transaction,nexusStateRule))
                .flatMap(TaxableCollectionAmountExtractor::extract)
                .map(amount -> new TransactionNexusSummary(amount, transaction.getExternalTimestamps().getCreatedDate(), transaction.getTransactionType()));

//        return Mono.fromCallable(() -> {
//            TaxableCollectionAmountExtractor amountExtractor = nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transaction, nexusStateRule);
//            BigDecimal amount = amountExtractor.extract();
//            return amount;
//        });
    }
}
