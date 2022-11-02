package com.complyt.utils.factory;

import com.complyt.business.nexus.checker.qualification_check.QualificationCheck;
import com.complyt.business.nexus.data_extractor.IAmountExtractor;
import com.complyt.business.nexus.data_extractor.ItemAmountExtractor;
import com.complyt.business.nexus.data_extractor.NexusTransactionAmountAggregator;
import com.complyt.business.nexus.data_extractor.ShippingFeeAmountExtractor;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class NexusAmountAggregatorFactory {

    @NonNull
    private QualificationCheck qualificationCheck;


    public NexusTransactionAmountAggregator createNexusTransactionAmountAggregator(@NonNull Transaction transaction, @NonNull NexusStateRule nexusStateRule) {
        List<IAmountExtractor> amountExtractors = new ArrayList<>();

        amountExtractors.add(new ItemAmountExtractor(qualificationCheck, transaction.getItems(), nexusStateRule));

        if (transaction.getShippingFee() != null) {
            amountExtractors.add(new ShippingFeeAmountExtractor(qualificationCheck, transaction.getShippingFee(), nexusStateRule));
        }
        
        return new NexusTransactionAmountAggregator(amountExtractors);
    }
}
