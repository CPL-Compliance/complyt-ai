package com.complyt.business.nexus.checker.qualification_check;

import com.complyt.domain.Taxable;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QualificationChecker {

    public boolean isQualified(Taxable taxAble, @NonNull NexusStateRule nexusStateRule) {
        if (taxAble == null) {
            log.debug("Null taxable item passed - Failed qualification check");
            return false;
        }

        boolean containsTaxable = nexusStateRule.taxableCategories().contains(taxAble.getTaxableCategory());
        boolean containsTangible = nexusStateRule.tangibleCategories().contains(taxAble.getTangibleCategory());
        log.debug("Taxable item with tax code : " + taxAble.getTaxCode() + ", contains taxable: " + containsTangible + ", "
                  + "contains tangible: " + containsTangible);

        return containsTaxable && containsTangible;
    }
}
