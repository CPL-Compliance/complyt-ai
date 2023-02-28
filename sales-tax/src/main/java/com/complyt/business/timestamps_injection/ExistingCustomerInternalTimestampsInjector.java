package com.complyt.business.timestamps_injection;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.timestamps.Timestamps;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ExistingCustomerInternalTimestampsInjector implements TimestampsInjector<Customer> {

    @NonNull
    private final Customer customer;

    @Override
    public Customer inject() {
        LocalDateTime createdDate = customer.getInternalTimestamps().getCreatedDate();
        LocalDateTime modifiedDate = LocalDateTime.now();
        Timestamps modifiedTimeStamps = new Timestamps(createdDate, modifiedDate);

        return customer.withInternalTimestamps(modifiedTimeStamps);
    }
}