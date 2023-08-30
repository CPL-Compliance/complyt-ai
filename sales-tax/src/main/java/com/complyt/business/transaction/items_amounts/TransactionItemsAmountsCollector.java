package com.complyt.business.transaction.items_amounts;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.domain.Taxable;
import com.complyt.domain.Transaction;
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
    AmountCalculator<List<Taxable>> taxableItemsAmountCalculator;

    @NonNull
    AmountCalculator<List<Taxable>> tangibleItemsAmountCalculator;

    @NonNull
    AmountCalculator<List<Taxable>> totalItemsAmountCalculator;

    @NonNull
    CollectionBuilder<Taxable> taxableCollectionBuilder;

    public Transaction collect(@NonNull Transaction transaction) {
        List<Taxable> items = (List<Taxable>) taxableCollectionBuilder.build(transaction);

        BigDecimal taxableItemsAmount = taxableItemsAmountCalculator.calculate(items);
        BigDecimal tangibleItemsAmount = tangibleItemsAmountCalculator.calculate(items);
        BigDecimal totalItemsAmount = totalItemsAmountCalculator.calculate(items);

        return new Transaction(
                transaction.getComplytId(), transaction.getId(),
                transaction.getExternalId(), transaction.getSource(), transaction.getDocumentName(),
                transaction.getItems(), transaction.getBillingAddress(),
                transaction.getShippingAddress(), transaction.getCustomerId(), transaction.getCustomer(),
                transaction.getSalesTax(), transaction.getTransactionStatus(),
                transaction.getTenantId(), transaction.getInternalTimestamps(),
                transaction.getExternalTimestamps(), transaction.getTransactionType(),
                transaction.getShippingFee(), transaction.getCreatedFrom(),
                taxableItemsAmount, tangibleItemsAmount, totalItemsAmount, transaction.getTransactionFilingStatus()
        );
    }
}
