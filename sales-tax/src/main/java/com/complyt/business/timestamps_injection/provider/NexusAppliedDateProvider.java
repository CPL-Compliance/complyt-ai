package com.complyt.business.timestamps_injection.provider;

import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
@Component
public class NexusAppliedDateProvider implements AppliedDateProvider {

    @Override
    public LocalDateTime getAppliedDate(SalesTaxTracking salesTaxTracking, LocalDateTime updatedAppliedDate) {
        LocalDateTime appliedDate = salesTaxTracking.getAppliedDate();
        boolean isDefaultAppliedDate = appliedDate.isEqual(EconomicNexusTracker.DEFAULT_ESTABLISHED_DATE);

        // Updating appliedDate if default or lower than existing value
        return isDefaultAppliedDate
                ? updatedAppliedDate
                : appliedDate.isBefore(updatedAppliedDate) ? appliedDate : updatedAppliedDate;
    }
}
