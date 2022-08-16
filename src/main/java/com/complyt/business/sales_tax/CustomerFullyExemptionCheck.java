package com.complyt.business.sales_tax;

import com.complyt.domain.Transaction;
import com.complyt.domain.customer.ExemptionType;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.services.ExemptionService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class CustomerFullyExemptionCheck {

    @NonNull
    @Qualifier("exemptionServiceImpl")
    private ExemptionService exemptionService;

    public Mono<Boolean> isFullyExempted(@NonNull Transaction transaction) {
        boolean noExemptionStates = transaction.getCustomer().getExemptionsStates() == null;
        if (noExemptionStates) {
            return Mono.just(false);
        }

        boolean stateNotInExemptionsList = !transaction.getCustomer().getExemptionsStates().containsKey(transaction.getShippingAddress().getState());
        if (stateNotInExemptionsList) {
            return Mono.just(false);
        }

        boolean exemptionIsPartially = transaction.getCustomer().getExemptionsStates().get(transaction.getShippingAddress().getState()) == ExemptionType.PARTIALLY;
        if (exemptionIsPartially) {
            return Mono.just(false);
        }

        return exemptionService.findByClientCustomerAndState(transaction)
                .map(exemption -> isExemptionActive(transaction, exemption));
    }

    boolean isExemptionActive(@NonNull Transaction transaction, @NonNull Exemption exemption) {
        LocalDateTime referenceDate = transaction.getExternalTimeStamps().getCreatedDate();

        return referenceDate.compareTo(exemption.getValidationDates().getToDate()) >= 0 &&
                referenceDate.compareTo(exemption.getValidationDates().getFromDate()) <= 0;
    }
}
