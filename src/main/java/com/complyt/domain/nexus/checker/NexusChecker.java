package com.complyt.domain.nexus.checker;

import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusTracking;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class NexusChecker {
    @NonNull
    private PhysicalNexusCheck physicalNexusChecker;

    @NonNull
    private EconomicNexusCheck economicNexusChecker;

    @NonNull
    private NexusEnforcementCheck nexusEnforcementChecker;

    @NonNull
    private NexusThresholdCheck nexusThresholdCheck;

    public boolean hasNexus(@NonNull NexusTracking nexusTracking) {
        return physicalNexusChecker.check(nexusTracking) || economicNexusChecker.check(nexusTracking) ||
                nexusEnforcementChecker.check(nexusTracking);
    }

    public boolean exceededNexus(@NonNull NexusCalculationSummary calculationSummary, @NonNull NexusStateRule stateRule){
        Pair<NexusCalculationSummary,NexusStateRule> summaryAndRule = new Pair<>(calculationSummary,stateRule);
        return nexusThresholdCheck.check(summaryAndRule);
    }
}
