package com.complyt.business.sales_tax;

import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class SalesTaxApplyCheck {

    public boolean isApplied(@NonNull Transaction transaction, @NonNull SalesTaxTracking salesTaxTracking) {
        LocalDateTime referenceDate = transaction.getExternalTimeStamps().getCreatedDate();
        LocalDateTime applicationDate = salesTaxTracking.getAppliedDate();

        boolean isSalesTaxEnforced = salesTaxTracking.isEnforcesSalesTax();
        boolean isPassedApplicationDate = referenceDate.compareTo(applicationDate) >= 0;
        boolean isApproved = salesTaxTracking.isApproved() &&
                referenceDate.compareTo(salesTaxTracking.getApprovalDate()) >= 0;

        boolean isApplied = isSalesTaxEnforced && isPassedApplicationDate && isApproved;
        log.debug("Is sales tax applied for order returned : " + isApplied);

        return isApplied;
    }
}