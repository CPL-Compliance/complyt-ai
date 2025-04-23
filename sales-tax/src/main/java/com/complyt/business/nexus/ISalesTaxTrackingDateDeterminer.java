package com.complyt.business.nexus;

import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@AllArgsConstructor
@Component
public class ISalesTaxTrackingDateDeterminer{

    @NonNull
    ApplicationDateCreator applicationDateCreator;

     public LocalDateTime getSalesTaxTrackingAppliedDate(SalesTaxTracking salesTaxTracking) {
         LocalDateTime physicalDate = salesTaxTracking.getPhysicalNexusTracker().getEstablishedDate();
         LocalDateTime economicNexusDate = salesTaxTracking.getEconomicNexusTracker().getEstablishedDate();
         economicNexusDate =  applicationDateCreator.create(salesTaxTracking.getNexusStateRule().timeFrame(), economicNexusDate);

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
