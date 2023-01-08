package com.complyt.business.transaction.items_amounts;

import com.complyt.domain.Transaction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionItemsAmountsCollector implements TransactionAmountsCollector {

    @NonNull
    TaxablesAmountCalculator taxablesAmountCalculator;

    @NonNull
    TangiblesAmountCalculator tangiblesAmountCalculator;

    @NonNull
    TotalItemsAmountCalculator totalItemsAmountCalculator;

    public Transaction collect(@NonNull Transaction transaction) {
        float taxablesAmount = taxablesAmountCalculator.calculate(transaction);
        float tangiblesAmount = tangiblesAmountCalculator.calculate(transaction);
        float totalItemsAmount = totalItemsAmountCalculator.calculate(transaction);

        return transaction
                .withTaxablesAmount(taxablesAmount)
                .withTangiblesAmount(tangiblesAmount)
                .withTotalItemsAmount(totalItemsAmount);
    }
}
