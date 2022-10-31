package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.qualification_check.ShippingFeeQualificationCheck;
import com.complyt.domain.ShippingFee;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
@AllArgsConstructor
public class ShippingFeeAmountExtractor implements IAmountExtractor {

    @NonNull
    private ShippingFeeQualificationCheck shippingFeeQualificationCheck;

    @NonNull
    private ShippingFee shippingFee;

    @NonNull
    private NexusStateRule nexusStateRule;

    public float extract() {
        if (shippingFeeQualificationCheck.isQualified(shippingFee, nexusStateRule)) {
            return shippingFee.getPrice();
        }

        return 0;
    }
}
