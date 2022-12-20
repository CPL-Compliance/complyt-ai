package com.complyt.business.sales_tax.checker;

import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionType;
import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@AllArgsConstructor
@Slf4j
public class SalesTaxApplyCheck implements SalesTaxApplyChecker<SalesTaxTracking> {

    @NonNull
    private Transaction transaction;

    public boolean check(@NonNull SalesTaxTracking salesTaxTracking) {
        LocalDateTime referenceDate = transaction.getExternalTimestamps().getCreatedDate();
        LocalDateTime applicationDate = salesTaxTracking.getAppliedDate();

        boolean isSalesTaxEnforced = salesTaxTracking.isEnforcesSalesTax();
        boolean isPassedApplicationDate = referenceDate.compareTo(applicationDate) >= 0;
        boolean transactionIsNotOfTypeRefund = transaction.getTransactionType() != TransactionType.REFUND;
        boolean isApproved = salesTaxTracking.isApproved() && referenceDate.compareTo(salesTaxTracking.getApprovalDate()) >= 0;

        boolean isApplied = isSalesTaxEnforced && isPassedApplicationDate && isApproved && transactionIsNotOfTypeRefund;
        log.debug("Is sales tax applied for transaction returned : " + isApplied);
        return isApplied;
    }

}
