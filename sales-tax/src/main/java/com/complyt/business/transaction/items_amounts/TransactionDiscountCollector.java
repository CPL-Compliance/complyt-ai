package com.complyt.business.transaction.items_amounts;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.domain.Discountable;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionDiscountCollector implements TransactionAmountsCollector<Transaction> {

    @NonNull
    CollectionBuilder discountableCollectionBuilder;

    @NonNull
    AmountCalculator<List<Discountable>> discountablesTotalDiscountCalculator;

    @Override
    public Transaction collect(@NonNull Transaction transaction) {

        List<Discountable> originalDiscountables = (List<Discountable>) discountableCollectionBuilder.build(transaction);
        BigDecimal totalDiscount = discountablesTotalDiscountCalculator.calculate(originalDiscountables);


        // creating a new transaction instead of 2 with to save 1 construction
        return transaction.withTotalDiscount(totalDiscount);
    }
}
