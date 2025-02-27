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
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionLevelTaxRateCalculator implements AmountCalculator<Transaction> {

    @NonNull
    private CollectionBuilder<Taxable> taxableCollectionBuilder;

    @NonNull
    AmountCalculator<List<Taxable>> totalItemsAmountCalculator;

    @Override
    public BigDecimal calculate(@NonNull Transaction transaction, Boolean isTaxInclusive) {
        List<Taxable> taxables = (List<Taxable>) taxableCollectionBuilder.build(transaction);
        BigDecimal totalItemsAmount = totalItemsAmountCalculator.calculate(taxables, transaction.getIsTaxInclusive());

        return Objects.equals(totalItemsAmount, BigDecimal.ZERO) ?
                transaction.getSalesTax().rate() :
                transaction.getSalesTax().amount().divide(totalItemsAmount, 5, RoundingMode.HALF_UP);
    }

}