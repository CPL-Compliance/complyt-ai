package com.complyt.business.sales_tax;

import com.complyt.domain.Transaction;
import com.complyt.domain.customer.ExemptionType;
import com.complyt.domain.customer.exemption.Exemption;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class CustomerFullyExemptionCheck {

    public boolean isFullyExempted(@NonNull Transaction transaction, @NonNull Exemption exemption) {
        boolean noExemptionStates = transaction.getCustomer().getExemptionsStates() == null;
        if (noExemptionStates) {
            return false;
        }

        boolean stateNotInExemptionsList = !transaction.getCustomer().getExemptionsStates().containsKey(transaction.getShippingAddress().getState());
        if (stateNotInExemptionsList) {
            return false;
        }

        boolean exemptionIsPartially = transaction.getCustomer().getExemptionsStates().get(transaction.getShippingAddress().getState()) == ExemptionType.PARTIALLY;
        if (exemptionIsPartially) {
            return false;
        }

        return isExemptionActive(transaction, exemption);

    }

    boolean isExemptionActive(@NonNull Transaction transaction, @NonNull Exemption exemption) {
        LocalDateTime referenceDate = transaction.getExternalTimeStamps().getCreatedDate();
        boolean isExemptionInTimeFrame = referenceDate.compareTo(exemption.getValidationDates().getFromDate()) >= 0 &&
                referenceDate.compareTo(exemption.getValidationDates().getToDate()) <= 0;
        boolean isFullyExemptionType = exemption.getExemptionType() == ExemptionType.FULLY;

        return isExemptionInTimeFrame && isFullyExemptionType;
    }
}
