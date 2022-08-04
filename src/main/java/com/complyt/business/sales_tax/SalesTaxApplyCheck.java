package com.complyt.business.sales_tax;

import com.complyt.domain.Order;
import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class SalesTaxApplyCheck {

    public boolean isApplied(@NonNull Order order, @NonNull SalesTaxTracking salesTaxTracking) {
        LocalDateTime referenceDate = order.getExternalTimeStamps().getCreatedDate();
        LocalDateTime applicationDate = salesTaxTracking.getAppliedDate();

        boolean isSalesTaxEnforced = salesTaxTracking.isEnforcesSalesTax();
        boolean isPassedApplicationDate = referenceDate.compareTo(applicationDate) >= 0;

        boolean isApplied = isSalesTaxEnforced && isPassedApplicationDate;
        log.debug("Is sales tax applied for order returned : " + isApplied);

        return isApplied;
    }
}
