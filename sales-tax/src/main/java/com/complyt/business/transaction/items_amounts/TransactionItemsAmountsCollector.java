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
public class TransactionItemsAmountsCollector implements TransactionAmountsCollector<Transaction> {
    
    @NonNull
    AmountCalculator<List<Taxable>> tangibleItemsAmountCalculator;

    @NonNull
    AmountCalculator<List<Taxable>> totalItemsAmountCalculator;

    @NonNull
    CollectionBuilder<Taxable> taxableCollectionBuilder;

    public Transaction collect(@NonNull Transaction transaction) {

        List<Taxable> taxables = (List<Taxable>) taxableCollectionBuilder.build(transaction);
        BigDecimal taxableItemsAmount = new TaxableItemsAmountCalculator(
                transaction.getShippingAddress().city(),
                transaction.getShippingAddress().region())
                .calculate(taxables, transaction.getIsTaxInclusive());

        BigDecimal tangibleItemsAmount = tangibleItemsAmountCalculator.calculate(taxables, transaction.getIsTaxInclusive());
        BigDecimal totalItemsAmount = totalItemsAmountCalculator.calculate(taxables, transaction.getIsTaxInclusive());

        return transaction.setTaxableItemsAmount(taxableItemsAmount)
                .setTangibleItemsAmount(tangibleItemsAmount)
                .setTotalItemsAmount(totalItemsAmount);
    }
}