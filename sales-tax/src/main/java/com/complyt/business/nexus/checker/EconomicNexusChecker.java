package com.complyt.business.nexus.checker;

import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EconomicNexusChecker implements NexusCheck<SalesTaxTracking> {
    @Override
    public boolean check(@NonNull SalesTaxTracking salesTaxTracking) {
        return salesTaxTracking.getEconomicNexusTracker().isEstablished();
    }
}
