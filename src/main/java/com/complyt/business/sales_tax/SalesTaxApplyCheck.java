package com.complyt.business.sales_tax;

import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
@Slf4j
public class SalesTaxApplyCheck {

    @NonNull
    private CustomerFullyExemptionCheck customerFullyExemptionCheck;

    public Mono<Boolean> isApplied(@NonNull Transaction transaction, @NonNull SalesTaxTracking salesTaxTracking) {
        LocalDateTime referenceDate = transaction.getExternalTimeStamps().getCreatedDate();
        LocalDateTime applicationDate = salesTaxTracking.getAppliedDate();

        boolean isSalesTaxEnforced = salesTaxTracking.isEnforcesSalesTax();
        boolean isPassedApplicationDate = referenceDate.compareTo(applicationDate) >= 0;
        boolean isApproved = checkIfApproved(salesTaxTracking, referenceDate);

        if (!isSalesTaxEnforced || !isPassedApplicationDate || !isApproved) {
            log.debug("Is sales tax applied for order returned False ");
            return Mono.just(false);
        }

        return checkIfFullyExempted(transaction)
                .map(isFullyExempted -> {
                    boolean isSalesTaxApplied = !isFullyExempted;
                    log.debug("Is sales tax applied for order returned : " + isSalesTaxApplied);
                    return isSalesTaxApplied;
                });
    }

    boolean checkIfApproved(@NonNull SalesTaxTracking salesTaxTracking, @NonNull LocalDateTime referenceDate) {
        return salesTaxTracking.isApproved() && referenceDate.compareTo(salesTaxTracking.getApprovalDate()) >= 0;
    }

    Mono<Boolean> checkIfFullyExempted(@NonNull Transaction transaction) {
        return customerFullyExemptionCheck.isFullyExempted(transaction);
    }
}
