package com.complyt.business.nexus.checker;

import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SalesTaxEnforcementCheck implements NexusCheck<SalesTaxTracking> {

    @Override
    public boolean check(@NonNull SalesTaxTracking salesTaxTracking) {
        return salesTaxTracking.isEnforcesSalesTax();
    }
}
