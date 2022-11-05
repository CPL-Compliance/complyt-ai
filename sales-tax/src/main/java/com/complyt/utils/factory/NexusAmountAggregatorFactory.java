package com.complyt.utils.factory;

import com.complyt.business.builder.TaxableCollectionBuilder;
import com.complyt.business.nexus.checker.qualification_check.QualificationCheck;
import com.complyt.business.nexus.data_extractor.TaxableCollectionAmountExtractor;
import com.complyt.domain.Taxable;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class NexusAmountAggregatorFactory {

    @NonNull
    private QualificationCheck qualificationCheck;

    @NonNull
    private TaxableCollectionBuilder taxableCollectionBuilder;

    public TaxableCollectionAmountExtractor createTaxableCollectionAmountExtractor(@NonNull Transaction transaction, @NonNull NexusStateRule nexusStateRule) {
        List<Taxable> taxables = (List<Taxable>) taxableCollectionBuilder.build(transaction);

        return new TaxableCollectionAmountExtractor(qualificationCheck, taxables, nexusStateRule);
    }

}
