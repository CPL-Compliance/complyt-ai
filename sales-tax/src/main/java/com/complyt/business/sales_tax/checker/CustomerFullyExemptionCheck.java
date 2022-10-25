package com.complyt.business.sales_tax.checker;

import com.complyt.domain.Transaction;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionType;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@AllArgsConstructor
@Slf4j
public class CustomerFullyExemptionCheck implements SalesTaxApplyChecker <Exemption> {

    @NonNull
    private Transaction transaction;

    public boolean check(@NonNull Exemption exemption) {
        LocalDateTime referenceDate = transaction.getExternalTimeStamps().getCreatedDate();
        boolean isExemptionInTimeFrame = referenceDate.compareTo(exemption.getValidationDates().getFromDate()) >= 0 &&
                referenceDate.compareTo(exemption.getValidationDates().getToDate()) <= 0;
        boolean isFullyExemptionType = exemption.getExemptionType() == ExemptionType.FULLY;
        boolean isExemptionActive = isExemptionInTimeFrame && isFullyExemptionType;
        log.debug("Is exemption active returned : " + isExemptionActive);

        return isExemptionActive;
    }

}
