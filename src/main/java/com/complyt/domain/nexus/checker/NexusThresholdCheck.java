package com.complyt.domain.nexus.checker;

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

        return false;
    }
}
