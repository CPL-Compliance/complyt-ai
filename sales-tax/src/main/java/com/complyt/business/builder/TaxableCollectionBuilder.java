package com.complyt.business.builder;

import com.complyt.business.sales_tax.checker.TaxableItemExistChecker;
import com.complyt.domain.Taxable;
import com.complyt.domain.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
@AllArgsConstructor
public class TaxableCollectionBuilder {

    @NonNull
    TaxableItemExistChecker taxableItemExistChecker;

    public Collection<Taxable> build(@NonNull Transaction transaction) {
        Collection<Taxable> taxables = new ArrayList<>(transaction.getItems());
        if (transaction.getShippingFee() != null && taxableItemExistChecker.hasTaxableItem(transaction.getItems())) {
            taxables.add(transaction.getShippingFee());
        }

        return taxables;
    }
}
