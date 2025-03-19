package com.complyt.business.timestamps_injection;

import com.complyt.business.nexus.ISalesTaxTrackingDateDeterminer;
import com.complyt.business.timestamps_injection.provider.NexusAppliedDateProvider;
import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
public class SalesTaxTrackingPhysicalNexusDateApplierInjector implements TimestampsInjector<SalesTaxTracking> {

    @NonNull
    private final SalesTaxTracking salesTaxTracking;

    @Override
    public SalesTaxTracking inject() {
        LocalDateTime physicalEstablishedDate = salesTaxTracking.getPhysicalNexusTracker().getEstablishedDate();
        LocalDateTime appliedDate = ISalesTaxTrackingDateDeterminer.getSalesTaxTrackingAppliedDate(salesTaxTracking);

        log.info("Setting appliedDate based on physicalNexusTracker: appliedDate={}, physicalEstablishedDate={}, AppliedDateUpdated={}", appliedDate, physicalEstablishedDate, !appliedDate.equals(salesTaxTracking.getAppliedDate()));
        return salesTaxTracking.setAppliedDate(appliedDate)
                .setPhysicalNexusTracker(salesTaxTracking.getPhysicalNexusTracker().setEstablishedDate(physicalEstablishedDate));
    }
}
