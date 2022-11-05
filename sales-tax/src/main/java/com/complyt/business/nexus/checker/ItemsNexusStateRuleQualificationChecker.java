package com.complyt.business.nexus.checker;

import com.complyt.business.nexus.checker.qualification_check.QualificationCheck;
import com.complyt.domain.Taxable;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class ItemsNexusStateRuleQualificationChecker implements NexusCheck<Pair<List<Taxable>, NexusStateRule>> {

    /*
     * Checks if there is an item from the given list that should be counted
     * according to the nexus state rules given
     */

    @NonNull
    private QualificationCheck qualificationCheck;

    @Override
    public boolean check(@NonNull Pair<List<Taxable>, NexusStateRule> itemsAndRule) {
        List<Taxable> items = itemsAndRule.getValue0();
        NexusStateRule nexusStateRule = itemsAndRule.getValue1();
        log.debug("Checking if items received should be counted into nexus aggregation, state rule info :" +
                "taxable categories - " + nexusStateRule.getTaxableCategories() + " , tangible categories "
                + nexusStateRule.getTangibleCategories());

        for (Taxable item : items) {
            if (qualificationCheck.isQualified(item, nexusStateRule)) {
                return true;
            }
        }
        return false;
    }

}
