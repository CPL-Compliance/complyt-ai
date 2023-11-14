package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.TransactionNexusSummary;
import com.complyt.domain.transaction.Transaction;
import com.complyt.utils.factory.NexusAmountAggregatorFactory;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class NexusTransactionSummaryCalculator {

    @NonNull
    private NexusAmountAggregatorFactory nexusAmountAggregatorFactory;

    public Mono<TransactionNexusSummary> extract(@NonNull Transaction transaction, @NonNull NexusStateRule nexusStateRule) {
        return Mono.just(nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transaction,nexusStateRule))
                .flatMap(TaxableCollectionAmountExtractor::extract)
                .map(amount -> new TransactionNexusSummary(amount, transaction.getExternalTimestamps().getCreatedDate(), transaction.getTransactionType()));
    }
}
