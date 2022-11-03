package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.qualification_check.QualificationCheck;
import com.complyt.domain.Taxable;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;

@EqualsAndHashCode
@AllArgsConstructor
public class TaxableCollectionAmountExtractor implements AmountExtractor {

    @NonNull
    private QualificationCheck qualificationCheck;

    @NonNull
    private Collection<Taxable> taxables;

    @NonNull
    private NexusStateRule nexusStateRule;

    public float extract() {
        List<Taxable> qualifiedTaxables = taxables.stream().filter(item -> qualificationCheck.isQualified(item, nexusStateRule)).toList();
        float amount = 0;

        for(Taxable taxable : qualifiedTaxables)
            amount += taxable.getTotalPrice();

        return amount;
    }
}
