package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.qualification_check.QualificationCheck;
import com.complyt.domain.ShippingFee;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
@AllArgsConstructor
public class ShippingFeeAmountExtractor implements AmountExtractor {

    @NonNull
    private QualificationCheck qualificationCheck;

    @NonNull
    private ShippingFee shippingFee;

    @NonNull
    private NexusStateRule nexusStateRule;

    public float extract() {
        if (qualificationCheck.isQualified(shippingFee, nexusStateRule)) {
            return shippingFee.getPrice();
        }

        return 0;
    }
}
