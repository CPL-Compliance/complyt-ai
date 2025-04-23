package com.complyt.business.timestamps_injection;

import com.complyt.business.nexus.ISalesTaxTrackingDateDeterminer;
import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
@Component
public class NexusDateApplyDateInitializer {

    @NonNull
    private ISalesTaxTrackingDateDeterminer dateDeterminer;

    public SalesTaxTracking init(@NonNull SalesTaxTracking salesTaxTracking) {
        LocalDateTime physicalEstablishedDate = salesTaxTracking.getPhysicalNexusTracker().getEstablishedDate();
        LocalDateTime appliedDate = dateDeterminer.getSalesTaxTrackingAppliedDate(salesTaxTracking);

        log.info("Setting appliedDate based on physicalNexusTracker: appliedDate={}, physicalEstablishedDate={}, AppliedDateUpdated={}", appliedDate, physicalEstablishedDate, !appliedDate.equals(salesTaxTracking.getAppliedDate()));
        return salesTaxTracking.setAppliedDate(appliedDate)
                .setPhysicalNexusTracker(salesTaxTracking.getPhysicalNexusTracker().setEstablishedDate(physicalEstablishedDate));
    }
}
