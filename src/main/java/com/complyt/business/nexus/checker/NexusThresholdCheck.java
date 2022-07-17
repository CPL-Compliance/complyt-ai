package com.complyt.business.nexus.checker;

import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NexusThresholdCheck implements NexusCheck<Pair<NexusCalculationSummary, NexusStateRule>> {

    @Override
    public boolean check(@NonNull Pair<NexusCalculationSummary, NexusStateRule> summaryAndRule) {
        NexusCalculationSummary nexusCalculationSummary = summaryAndRule.getValue0();
        NexusStateRule nexusStateRule = summaryAndRule.getValue1();
        Definition definition = nexusStateRule.getNexusThreshold().getDefinition();

        switch (definition) {
            case AMOUNT:
                return exceededAmount(nexusCalculationSummary,nexusStateRule);

            case COUNT:
                return exceededCount(nexusCalculationSummary,nexusStateRule);

            case AMOUNT_AND_COUNT:
                return exceededAmountAndCount(nexusCalculationSummary,nexusStateRule);

            case AMOUNT_OR_COUNT:
                return exceededAmountOrCount(nexusCalculationSummary,nexusStateRule);
        }
        return false;
    }

    public boolean exceededAmount(NexusCalculationSummary nexusCalculationSummary, NexusStateRule nexusStateRule){
        return nexusCalculationSummary.getAmount() >= nexusStateRule.getNexusThreshold().getAmount();
    }

    public boolean exceededCount(NexusCalculationSummary nexusCalculationSummary, NexusStateRule nexusStateRule){
        return nexusCalculationSummary.getCount() >= nexusStateRule.getNexusThreshold().getCount();
    }

    public boolean exceededAmountAndCount(NexusCalculationSummary nexusCalculationSummary, NexusStateRule nexusStateRule){
        return exceededAmount(nexusCalculationSummary,nexusStateRule) &&
                exceededCount(nexusCalculationSummary,nexusStateRule);
    }

    public boolean exceededAmountOrCount(NexusCalculationSummary nexusCalculationSummary, NexusStateRule nexusStateRule){
        return exceededAmount(nexusCalculationSummary,nexusStateRule) ||
                exceededCount(nexusCalculationSummary,nexusStateRule);
    }
}
