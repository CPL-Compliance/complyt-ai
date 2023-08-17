package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.qualification_check.QualificationChecker;
import com.complyt.domain.Taxable;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode
@AllArgsConstructor
public class TaxableCollectionAmountExtractor implements AmountExtractor {

    @NonNull
    private QualificationChecker qualificationChecker;

    @NonNull
    private Collection<Taxable> taxables;

    @NonNull
    private NexusStateRule nexusStateRule;

    public BigDecimal extract() {
        List<Taxable> qualifiedTaxables = taxables.stream().filter(item -> qualificationChecker.isQualified(item, nexusStateRule)).toList();
        BigDecimal amount = BigDecimal.ZERO;

        for (Taxable taxable : qualifiedTaxables)
            amount = amount.add(taxable.getTotalPrice());

        return amount;
    }
}
