package com.complyt.business.nexus.checker;

import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.SalesTaxTracking;
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
    private SalesTaxEnforcementCheck salesTaxEnforcementChecker;

    @NonNull
    private NexusThresholdCheck nexusThresholdCheck;

    public boolean hasNexus(@NonNull SalesTaxTracking salesTaxTracking) {
        boolean hasNexus = salesTaxEnforcementChecker.check(salesTaxTracking) &&
                (physicalNexusChecker.check(salesTaxTracking) || economicNexusChecker.check(salesTaxTracking));
        log.debug("Checking if client has nexus in state : " + salesTaxTracking.getState().getAbbreviation()
                + " Has given a result of : " + hasNexus);

        return hasNexus;
    }

    public boolean passedThreshold(@NonNull NexusCalculationSummary calculationSummary, @NonNull NexusStateRule stateRule) {
        Pair<NexusCalculationSummary, NexusStateRule> summaryAndRule = new Pair<>(calculationSummary, stateRule);
        boolean passedThreshold = nexusThresholdCheck.check(summaryAndRule);
        log.debug("Checking if client passed nexus' threshold in state : " + stateRule.getState().getAbbreviation()
                + " Has given a result of : " + passedThreshold);

        return passedThreshold;
    }
}
