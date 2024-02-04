package com.complyt.business.transaction.items_amounts;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.business.builder.DiscountableCollectionBuilder;
import com.complyt.business.transaction.ItemTotalRecalculator;
import com.complyt.domain.Discountable;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionDiscountCollector implements TransactionAmountsCollector<Transaction> {

    @NonNull
    CollectionBuilder discountableCollectionBuilder;

    @NonNull
    AmountCalculator<List<Discountable>> discountablesTotalDiscountCalculator;

    @NonNull
    ItemTotalRecalculator itemTotalRecalculator;

    @Override
    public Transaction collect(@NonNull Transaction transaction) {
        List<Item> recalculatedTotalItemDiscountables = itemTotalRecalculator.recalculate(transaction.getItems());

        List<Discountable> originalDiscountables = (List<Discountable>) discountableCollectionBuilder.build(transaction);
        BigDecimal totalDiscount = discountablesTotalDiscountCalculator.calculate(originalDiscountables);


        // creating a new transaction instead of 2 with to save 1 construction
        return new Transaction (
        transaction.getComplytId(), transaction.getId(),
                transaction.getExternalId(), transaction.getSource(), transaction.getDocumentName(),
                recalculatedTotalItemDiscountables,
                transaction.getBillingAddress(),transaction.getShippingAddress(), transaction.getCustomerId(), transaction.getCustomer(),
                transaction.getSalesTax(), transaction.getTransactionStatus(),
                transaction.getTenantId(), transaction.getInternalTimestamps(),
                transaction.getExternalTimestamps(), transaction.getTransactionType(),
                transaction.getShippingFee(), transaction.getCreatedFrom(),
                transaction.getTaxableItemsAmount(), transaction.getTangibleItemsAmount(), transaction.getTotalItemsAmount(),
                totalDiscount,
                transaction.getTransactionFilingStatus()
        );
    }
}
