package com.complyt.business.nexus;

import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
public interface ISalesTaxTrackingDateDeterminer{
     static LocalDateTime getSalesTaxTrackingAppliedDate(SalesTaxTracking salesTaxTracking) {
        LocalDateTime physicalDate = salesTaxTracking.getPhysicalNexusTracker().getEstablishedDate();
        LocalDateTime economicNexusDate = salesTaxTracking.getEconomicNexusTracker().getEstablishedDate();

        return salesTaxTracking.getPhysicalNexusTracker().isEstablished() && salesTaxTracking.getEconomicNexusTracker().isEstablished()
                ? physicalDate.isBefore(economicNexusDate)
                ? physicalDate
                : economicNexusDate
                : salesTaxTracking.getPhysicalNexusTracker().isEstablished()
                ? physicalDate
                : salesTaxTracking.getEconomicNexusTracker().isEstablished()
                ? economicNexusDate
                : EconomicNexusTracker.DEFAULT_ESTABLISHED_DATE;
    }
}
