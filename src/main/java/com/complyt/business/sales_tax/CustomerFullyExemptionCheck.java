package com.complyt.business.sales_tax;

import com.complyt.domain.Transaction;
import com.complyt.domain.customer.ExemptionType;
import com.complyt.domain.customer.exemption.Exemption;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
@Slf4j
public class CustomerFullyExemptionCheck {

    public boolean isFullyExempted(@NonNull Transaction transaction, @NonNull Exemption exemption) {
        boolean noExemptionStates = transaction.getCustomer().getExemptionsStates() == null;
        if (noExemptionStates) {
            log.debug("Customer has no exemption states - no fully exemption for this transaction");
            return false;
        }

        boolean stateNotInExemptionsList = !transaction.getCustomer().getExemptionsStates().containsKey(transaction.getShippingAddress().getState());
        if (stateNotInExemptionsList) {
            log.debug("State does not exist in customer's exemptions states list - no fully exemption for this transaction");
            return false;
        }

        boolean exemptionIsPartially = transaction.getCustomer().getExemptionsStates().get(transaction.getShippingAddress().getState()) == ExemptionType.PARTIALLY;
        if (exemptionIsPartially) {
            log.debug("Customer's exemption for this state is partially - no fully exemption for this transaction");
            return false;
        }

        return isExemptionActive(transaction, exemption);
    }

    boolean isExemptionActive(@NonNull Transaction transaction, @NonNull Exemption exemption) {
        LocalDateTime referenceDate = transaction.getExternalTimeStamps().getCreatedDate();
        boolean isExemptionInTimeFrame = referenceDate.compareTo(exemption.getValidationDates().getFromDate()) >= 0 &&
                referenceDate.compareTo(exemption.getValidationDates().getToDate()) <= 0;
        boolean isFullyExemptionType = exemption.getExemptionType() == ExemptionType.FULLY;
        boolean isExemptionActive = isExemptionInTimeFrame && isFullyExemptionType;
        log.debug("Is exemption active returned : " + isExemptionActive);

        return isExemptionActive;
    }
}
