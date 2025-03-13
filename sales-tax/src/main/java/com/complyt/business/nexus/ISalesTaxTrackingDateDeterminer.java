package com.complyt.business.nexus;

import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public interface ISalesTaxTrackingDateDeterminer{
    default LocalDateTime getSalesTaxTrackingAppliedDate(SalesTaxTracking salesTaxTracking) {
        LocalDateTime physicalDate = salesTaxTracking.getPhysicalNexusTracker().getEstablishedDate();
        LocalDateTime economicNexusDate = salesTaxTracking.getEconomicNexusTracker().getEstablishedDate();

        return salesTaxTracking.getPhysicalNexusTracker().isEstablished() && salesTaxTracking.getEconomicNexusTracker().isEstablished()
                ? physicalDate.isBefore(economicNexusDate)
                ? physicalDate
                : economicNexusDate
                : salesTaxTracking.getEconomicNexusTracker().isEstablished()
                ? economicNexusDate
                : EconomicNexusTracker.DEFAULT_ESTABLISHED_DATE;
    }
}
