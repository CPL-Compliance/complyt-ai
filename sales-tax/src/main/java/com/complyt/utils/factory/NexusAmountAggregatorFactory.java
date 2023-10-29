package com.complyt.utils.factory;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.business.nexus.checker.qualification_check.QualificationChecker;
import com.complyt.business.nexus.data_extractor.TaxableCollectionAmountExtractor;
import com.complyt.domain.Taxable;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.transaction.Transaction;
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
    private QualificationChecker qualificationChecker;

    @NonNull
    private CollectionBuilder<Taxable> taxableCollectionBuilder;

    public TaxableCollectionAmountExtractor createTaxableCollectionAmountExtractor(@NonNull Transaction transaction, @NonNull NexusStateRule nexusStateRule) {
        List<Taxable> taxables = (List<Taxable>) taxableCollectionBuilder.build(transaction);

        return new TaxableCollectionAmountExtractor(qualificationChecker, taxables, nexusStateRule);
    }

}
