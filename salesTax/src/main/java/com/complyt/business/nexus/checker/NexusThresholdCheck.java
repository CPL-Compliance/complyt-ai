package com.complyt.business.nexus.checker;

import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
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
        ThresholdStrategy thresholdStrategy = new ThresholdStrategy(nexusCalculationSummary, nexusStateRule);
        boolean exceededThreshold = thresholdStrategy.isExceeded();
        log.debug("Exceeded threshold : " + exceededThreshold);

        return exceededThreshold;
    }
}

@Slf4j
@ToString
@Getter
class ThresholdStrategy {

    private final NexusCalculationSummary calculationSummary;
    private final NexusStateRule stateRule;
    private boolean exceeded;

    public ThresholdStrategy(NexusCalculationSummary nexusCalculationSummary, NexusStateRule nexusStateRule) {
        calculationSummary = nexusCalculationSummary;
        stateRule = nexusStateRule;
        setIsNexusExceeded();
    }

    private void setIsNexusExceeded() {
        Definition definition = stateRule.getNexusThreshold().getDefinition();
        log.debug("Checking if nexus has been exceeded, threshold definition : " + definition);

        switch (definition) {
            case AMOUNT:
                exceeded = exceededAmount(calculationSummary, stateRule);
                return;

            case COUNT:
                exceeded = exceededCount(calculationSummary, stateRule);
                return;

            case AMOUNT_AND_COUNT:
                exceeded = exceededAmountAndCount(calculationSummary, stateRule);
                return;

            // AMOUNT_OR_COUNT
            default:
                exceeded = exceededAmountOrCount(calculationSummary, stateRule);
        }
    }

    public boolean exceededAmount(NexusCalculationSummary nexusCalculationSummary, NexusStateRule nexusStateRule) {
        return nexusCalculationSummary.getAmount() >= nexusStateRule.getNexusThreshold().getAmount();
    }

    public boolean exceededCount(NexusCalculationSummary nexusCalculationSummary, NexusStateRule nexusStateRule) {
        return nexusCalculationSummary.getCount() >= nexusStateRule.getNexusThreshold().getCount();
    }

    public boolean exceededAmountAndCount(NexusCalculationSummary nexusCalculationSummary, NexusStateRule nexusStateRule) {
        return exceededAmount(nexusCalculationSummary, nexusStateRule) &&
                exceededCount(nexusCalculationSummary, nexusStateRule);
    }

    public boolean exceededAmountOrCount(NexusCalculationSummary nexusCalculationSummary, NexusStateRule nexusStateRule) {
        return exceededAmount(nexusCalculationSummary, nexusStateRule) ||
                exceededCount(nexusCalculationSummary, nexusStateRule);
    }
}
