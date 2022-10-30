package com.complyt.business.nexus.checker.qualification_check;

import com.complyt.domain.ShippingFee;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class ShippingFeeQualificationCheck implements QualificationCheck<ShippingFee>{

    @Override
    public boolean isQualified(ShippingFee shippingFee, @NonNull NexusStateRule nexusStateRule) {
        if (shippingFee == null) {
            return false;
        }

        boolean containsTaxable = nexusStateRule.getTaxableCategories().contains(shippingFee.getTaxableCategory());
        boolean containsTangible = nexusStateRule.getTangibleCategories().contains(shippingFee.getTangibleCategory());
        log.debug("Shipping fee with tax code: " + shippingFee.getTaxCode() + ", contains taxable: " + containsTangible + ", "
                + "contains tangible: " + containsTangible);

        return containsTaxable && containsTangible;
    }

}
