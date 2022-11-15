package com.complyt.business.sales_tax.checker;

import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@AllArgsConstructor
@Slf4j
public class SalesTaxApplyChecker implements SalesTaxApplyCheck<SalesTaxTracking> {

    @NonNull
    private Transaction transaction;

    public boolean check(@NonNull SalesTaxTracking salesTaxTracking) {
        LocalDateTime referenceDate = transaction.getExternalTimeStamps().getCreatedDate();
        LocalDateTime applicationDate = salesTaxTracking.getAppliedDate();

        boolean isSalesTaxEnforced = salesTaxTracking.isEnforcesSalesTax();
        boolean isPassedApplicationDate = referenceDate.compareTo(applicationDate) >= 0;
        boolean isApproved = checkIfApproved(salesTaxTracking, referenceDate);

        boolean isApplied = isSalesTaxEnforced && isPassedApplicationDate && isApproved;
        log.debug("Is sales tax applied for transaction returned : " + isApplied);
        return isApplied;
    }

    boolean checkIfApproved(@NonNull SalesTaxTracking salesTaxTracking, @NonNull LocalDateTime referenceDate) {
        return salesTaxTracking.isApproved() && referenceDate.compareTo(salesTaxTracking.getApprovalDate()) >= 0;
    }

}
