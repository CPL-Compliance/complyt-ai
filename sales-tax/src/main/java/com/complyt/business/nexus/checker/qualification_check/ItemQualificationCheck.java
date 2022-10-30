package com.complyt.business.nexus.checker.qualification_check;

import com.complyt.domain.Item;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ItemQualificationCheck implements QualificationCheck<Item>{

    public boolean isQualified(@NonNull Item item, @NonNull NexusStateRule nexusStateRule) {
        boolean containsTaxable = nexusStateRule.getTaxableCategories().contains(item.getTaxableCategory());
        boolean containsTangible = nexusStateRule.getTangibleCategories().contains(item.getTangibleCategory());
        log.debug("Item with tax code: " + item.getTaxCode() + ", contains taxable: " + containsTangible + ", "
                + "contains tangible: " + containsTangible);

        return containsTaxable && containsTangible;
    }
}
