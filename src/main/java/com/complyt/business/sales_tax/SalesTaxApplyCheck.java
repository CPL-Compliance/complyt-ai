package com.complyt.business.sales_tax;

import com.complyt.domain.Transaction;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
@Slf4j
public class SalesTaxApplyCheck {

    @NonNull
    private CustomerFullyExemptionCheck customerFullyExemptionCheck;

    public boolean isApplied(@NonNull Transaction transaction, @NonNull SalesTaxTracking salesTaxTracking) {
        LocalDateTime referenceDate = transaction.getExternalTimeStamps().getCreatedDate();
        LocalDateTime applicationDate = salesTaxTracking.getAppliedDate();

        boolean isSalesTaxEnforced = salesTaxTracking.isEnforcesSalesTax();
        boolean isPassedApplicationDate = referenceDate.compareTo(applicationDate) >= 0;
        boolean isApproved = checkIfApproved(salesTaxTracking, referenceDate);
        boolean isFullyExempted = checkIfFullyExempted(transaction);

        boolean isApplied = isSalesTaxEnforced && isPassedApplicationDate && isApproved && !isFullyExempted;
        log.debug("Is sales tax applied for order returned : " + isApplied);

        return isApplied;
    }

    boolean checkIfApproved(@NonNull SalesTaxTracking salesTaxTracking, @NonNull LocalDateTime referenceDate) {
        boolean isApproved = salesTaxTracking.isApproved();
        boolean dateApplied = referenceDate.compareTo(salesTaxTracking.getApprovalDate()) >= 0;

        return isApproved && dateApplied;
    }

    boolean checkIfFullyExempted(@NonNull Transaction transaction) {
        Customer customer = transaction.getCustomer();
        String state = transaction.getShippingAddress().getState();

        return customerFullyExemptionCheck.isFullyExempted(customer, state);
    }
}
