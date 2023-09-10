package com.complyt.business.sales_tax.checker;

import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionType;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@AllArgsConstructor
@Slf4j
public class CustomerFullyExemptionChecker implements SalesTaxApplyChecker<Exemption> {

    @NonNull
    private Transaction transaction;

    public boolean check(@NonNull Exemption exemption) {
        LocalDateTime referenceDate = transaction.getExternalTimestamps().getCreatedDate();

        boolean isExemptionInTimeFrame = !referenceDate.isBefore(exemption.getValidationDates().getFromDate()) &&
                                         (exemption.getValidationDates().getToDate() == null || !referenceDate.isAfter(exemption.getValidationDates().getToDate()));
        boolean isFullyExemptionType = exemption.getExemptionType() == ExemptionType.FULLY;
        boolean isExemptionActive = isExemptionInTimeFrame && isFullyExemptionType;
        log.debug("Is exemption active returned : " + isExemptionActive);

        return isExemptionActive;
    }

}
