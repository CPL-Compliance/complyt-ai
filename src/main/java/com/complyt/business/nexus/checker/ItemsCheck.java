package com.complyt.business.nexus.checker;

import com.complyt.domain.Item;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ItemsCheck implements NexusCheck<Pair<List<Item>, NexusStateRule>> {

    /*
    * Checks if there is an item from the given list that should be counted
    * according to the nexus state rules given
    */
    @Override
    public boolean check(@NonNull Pair<List<Item>, NexusStateRule> itemsAndRule) {
        List<Item> items = itemsAndRule.getValue0();
        NexusStateRule nexusStateRule = itemsAndRule.getValue1();
        log.debug("Checking if items received should be counted into nexus aggregation, state rule info :" +
                "taxable categories - " + nexusStateRule.getTaxableCategories()  + " , tangible categories "
        + nexusStateRule.getTangibleCategories());

        for(Item item : items) {
            if(isCounted(item,nexusStateRule)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCounted(Item item, NexusStateRule nexusStateRule) {
        boolean containsTaxable = nexusStateRule.getTaxableCategories().contains(item.getTaxableCategory());
        boolean containsTangible = nexusStateRule.getTangibleCategories().contains(item.getTangibleCategory());
        log.debug("Item with tax code: " + item.getTaxCode() + ", containsTaxable: " + containsTangible + ", "
        + "containsTangible: " + containsTangible);

        return containsTaxable && containsTangible;
    }

}
