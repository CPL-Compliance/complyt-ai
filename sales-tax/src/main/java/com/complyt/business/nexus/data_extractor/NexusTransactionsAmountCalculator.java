package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.utils.factory.NexusAmountAggregatorFactory;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Component
@AllArgsConstructor
public class NexusTransactionsAmountCalculator implements NexusDataExtractor<BigDecimal, Transaction> {

    @NonNull
    private NexusAmountAggregatorFactory nexusAmountAggregatorFactory;

    @Override
    public Mono<BigDecimal> extract(@NonNull Transaction transaction, @NonNull NexusStateRule nexusStateRule) {
        return Mono.fromCallable(() -> {
                TaxableCollectionAmountExtractor amountExtractor = nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transaction, nexusStateRule);
                BigDecimal amount = amountExtractor.extract();

                // In case of a refund, the amount will be subtracted instead of added
                BigDecimal currentAmount = transaction.getTransactionType() == TransactionType.REFUND ? amount.multiply(BigDecimal.valueOf(-1)) : amount;

            return currentAmount;
        });
    }
}
