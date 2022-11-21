package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionType;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.utils.factory.NexusAmountAggregatorFactory;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class NexusTransactionAmountExtractor implements NexusDataExtractor<Float, Transaction> {

    @NonNull
    private NexusAmountAggregatorFactory nexusAmountAggregatorFactory;

    @Override
    public Float extract(@NonNull Transaction transaction, @NonNull NexusStateRule nexusStateRule) {
        TaxableCollectionAmountExtractor amountExtractor = nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transaction, nexusStateRule);
        float totalAmount = amountExtractor.extract();

        return transaction.getTransactionType() == TransactionType.REFUND ? -1 * totalAmount : totalAmount;
    }
}
