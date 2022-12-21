package com.complyt.business.timestamps_injection;

import com.complyt.domain.TimeStamps;
import com.complyt.domain.customer.Customer;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ExistingCustomerInternalTimestampsInjector implements TimestampsInjector<Customer> {

    @NonNull
    private final Customer customer;

    @Override
    public Customer inject() {
        LocalDateTime createdDate = customer.getInternalTimeStamps().getCreatedDate();
        LocalDateTime modifiedDate = LocalDateTime.now();
        TimeStamps modifiedTimeStamps = new TimeStamps(createdDate, modifiedDate);

        return customer.withInternalTimeStamps(modifiedTimeStamps);
    }
}