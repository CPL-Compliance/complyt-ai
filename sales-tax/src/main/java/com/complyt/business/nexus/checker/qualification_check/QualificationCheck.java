package com.complyt.business.nexus.checker.qualification_check;

import com.complyt.domain.ITaxAble;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QualificationCheck {

     public boolean isQualified(ITaxAble taxAble, @NonNull NexusStateRule nexusStateRule) {
        if(taxAble == null) {
            log.debug("Null taxable item passed - failed qualification check");
            return false;
        }

        boolean containsTaxable = nexusStateRule.getTaxableCategories().contains(taxAble.getTaxableCategory());
        boolean containsTangible = nexusStateRule.getTangibleCategories().contains(taxAble.getTangibleCategory());
        log.debug("Taxable item with tax code : " + taxAble.getTaxCode() + ", contains taxable: " + containsTangible + ", "
                + "contains tangible: " + containsTangible);
        return containsTaxable && containsTangible;

    }
}
