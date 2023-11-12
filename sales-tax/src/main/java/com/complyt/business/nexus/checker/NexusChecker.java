package com.complyt.business.nexus.checker;

import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.utils.factory.DateRange;
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
    private PhysicalNexusChecker physicalNexusChecker;

    @NonNull
    private EconomicNexusChecker economicNexusChecker;

    @NonNull
    private SalesTaxEnforcementChecker salesTaxEnforcementChecker;

    @NonNull
    private NexusThresholdChecker nexusThresholdChecker;

    public boolean hasNexus(@NonNull SalesTaxTracking salesTaxTracking) {
        boolean hasNexus = salesTaxEnforcementChecker.check(salesTaxTracking) &&
                           (physicalNexusChecker.check(salesTaxTracking) || economicNexusChecker.check(salesTaxTracking));
        log.debug("Checking if client has nexus in state : " + salesTaxTracking.getState().getAbbreviation()
                  + " Has given a result of : " + hasNexus);

        return hasNexus;
    }

    public boolean passedThreshold(@NonNull SalesTaxTracking salesTaxTracking, @NonNull DateRange dateRange) {
        NexusCalculationSummary nexusCalculationSummary = salesTaxTracking.getNexusCalculationSummaries().get(dateRange.getEnd().toLocalDate());
        if (nexusCalculationSummary == null) return false;
        boolean passedThreshold = nexusThresholdChecker.check(new Pair<>(nexusCalculationSummary, salesTaxTracking.getNexusStateRule()));
        log.debug("Checking if client passed nexus' threshold in state : " + salesTaxTracking.getNexusStateRule().state().getAbbreviation()
                  + " Has given a result of : " + passedThreshold);

        return passedThreshold;
    }
}
