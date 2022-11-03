package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.qualification_check.QualificationCheck;
import com.complyt.domain.Taxable;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
@AllArgsConstructor
public class TaxableAmountExtractor implements AmountExtractor {

    @NonNull
    private QualificationCheck qualificationCheck;

    @NonNull
    private Taxable taxable;

    @NonNull
    private NexusStateRule nexusStateRule;

    public float extract() {
        if (qualificationCheck.isQualified(taxable, nexusStateRule)) {
            return taxable.getTotalPrice();
        }

        return 0;
    }
}
