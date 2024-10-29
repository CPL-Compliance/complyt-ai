package com.complyt.business.transaction.items_amounts;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.domain.Taxable;
import com.complyt.domain.transaction.Transaction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FinalTransactionAmountCollector implements TransactionAmountsCollector<Transaction> {

    @NonNull
    AmountCalculator<List<Taxable>> totalItemsAmountCalculator;

    @NonNull
    CollectionBuilder<Taxable> taxableCollectionBuilder;

    public Transaction collect(@NonNull Transaction transaction) {

        List<Taxable> taxables = (List<Taxable>) taxableCollectionBuilder.build(transaction);
        BigDecimal finalTransactionAmount = totalItemsAmountCalculator.calculate(taxables,false);

        return transaction.setFinalTransactionAmount(finalTransactionAmount);
    }
}