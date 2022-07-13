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

    @Override
    public boolean check(@NonNull Pair<List<Item>, NexusStateRule> objects) {
        List<Item> items = objects.getValue0();
        NexusStateRule nexusStateRule = objects.getValue1();

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

        return containsTaxable && containsTangible;
    }

}
