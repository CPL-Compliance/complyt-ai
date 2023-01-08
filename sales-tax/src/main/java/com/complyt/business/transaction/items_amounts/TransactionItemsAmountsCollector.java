package com.complyt.business.transaction.items_amounts;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.domain.Taxable;
import com.complyt.domain.Transaction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionItemsAmountsCollector implements TransactionAmountsCollector<Transaction> {

    @NonNull
    AmountCalculator<List<Taxable>> taxableItemsAmountCalculator;

    @NonNull
    AmountCalculator<List<Taxable>> tangibleItemsAmountCalculator;

    @NonNull
    AmountCalculator<List<Taxable>> totalItemsAmountCalculator;

    @NonNull
    CollectionBuilder<Taxable> taxableCollectionBuilder;

    public Transaction collect(@NonNull Transaction transaction) {
        List<Taxable> items = (List<Taxable>) taxableCollectionBuilder.build(transaction);

        float taxableItemsAmount = taxableItemsAmountCalculator.calculate(items);
        float tangibleItemsAmount = tangibleItemsAmountCalculator.calculate(items);
        float totalItemsAmount = totalItemsAmountCalculator.calculate(items);

        return transaction
                .withTaxableItemsAmount(taxableItemsAmount)
                .withTangibleItemsAmount(tangibleItemsAmount)
                .withTotalItemsAmount(totalItemsAmount);
    }
}
