package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionType;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.utils.factory.NexusAmountAggregatorFactory;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@AllArgsConstructor
public class NexusTransactionsAmountCalculator implements NexusDataExtractor<Float, List<Transaction>> {

    @NonNull
    private NexusAmountAggregatorFactory nexusAmountAggregatorFactory;

    @Override
    public Mono<Float> extract(@NonNull List<Transaction> transactions, @NonNull NexusStateRule nexusStateRule) {
        return Mono.fromCallable(() -> {
            float totalAmount = 0;
            for (Transaction transaction : transactions) {
                TaxableCollectionAmountExtractor amountExtractor = nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transaction, nexusStateRule);
                float amount = amountExtractor.extract();
                totalAmount += transaction.getTransactionType() == TransactionType.REFUND ? -1 * amount : amount;
            }

            return totalAmount;
        });
    }
}
