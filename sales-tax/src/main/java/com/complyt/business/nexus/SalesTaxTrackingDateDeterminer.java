package com.complyt.business.nexus;

import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@AllArgsConstructor
@Component
public class SalesTaxTrackingDateDeterminer {

    @NonNull
    ApplicationDateCreator applicationDateCreator;

     public LocalDateTime getSalesTaxTrackingAppliedDate(SalesTaxTracking salesTaxTracking) {
         LocalDateTime physicalDate = salesTaxTracking.getPhysicalNexusTracker().getEstablishedDate();
         LocalDateTime economicNexusDate = salesTaxTracking.getEconomicNexusTracker().getEstablishedDate();
         economicNexusDate = applicationDateCreator.create(salesTaxTracking.getNexusStateRule().timeFrame(), economicNexusDate);

         if (salesTaxTracking.getPhysicalNexusTracker().isEstablished() && salesTaxTracking.getEconomicNexusTracker().isEstablished()){
             return physicalDate.isBefore(economicNexusDate) ? physicalDate : economicNexusDate;
         }

         if( salesTaxTracking.getPhysicalNexusTracker().isEstablished()) return physicalDate;

         if ( salesTaxTracking.getEconomicNexusTracker().isEstablished()) return economicNexusDate;

         return EconomicNexusTracker.DEFAULT_ESTABLISHED_DATE;
     }
}
