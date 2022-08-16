package com.complyt.business.sales_tax;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.ExemptionType;
import com.complyt.services.ExemptionService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomerFullyExemptionCheck {

    @NonNull
    @Qualifier("exemptionServiceImpl")
    private ExemptionService exemptionService;

    public boolean isFullyExempted(@NonNull Customer customer, @NonNull String state) {
        if (customer.getExemptionsStates() == null) {
            return false;
        }

        if (!customer.getExemptionsStates().containsKey(state)) {
            return false;
        }

        ExemptionType exemptionType = customer.getExemptionsStates().get(state);
        return exemptionType == ExemptionType.FULLY;
    }
}
