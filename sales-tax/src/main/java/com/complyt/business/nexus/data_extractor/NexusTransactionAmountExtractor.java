package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.qualification_check.ItemQualificationCheck;
import com.complyt.business.nexus.checker.qualification_check.ShippingFeeQualificationCheck;
import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NexusTransactionAmountExtractor implements NexusDataExtractor<Float, Transaction> {

    @NonNull
    private ItemQualificationCheck itemQualificationCheck;

    @NonNull
    private ShippingFeeQualificationCheck shippingFeeQualificationCheck;

    @Override
    public Float extract(@NonNull Transaction transaction, @NonNull NexusStateRule nexusStateRule) {
        float amount = 0;
        for (Item item : transaction.getItems()) {
            if (itemQualificationCheck.isQualified(item, nexusStateRule)) {
                amount += item.getTotalPrice();
            }
        }

        if (shippingFeeQualificationCheck.isQualified(transaction.getShippingFee(), nexusStateRule)) {
            amount += transaction.getShippingFee().getPrice();
        }

        return amount;
    }
}
