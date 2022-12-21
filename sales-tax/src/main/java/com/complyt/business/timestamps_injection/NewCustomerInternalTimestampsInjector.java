package com.complyt.business.timestamps_injection;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.timestamps.Timestamps;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@AllArgsConstructor
public class NewCustomerInternalTimestampsInjector implements TimestampsInjector<Customer> {

    @NonNull
    private final Customer customer;

    @Override
    public Customer inject() {
        Timestamps timeStamps = new Timestamps(LocalDateTime.now(), LocalDateTime.now());
        return customer.withInternalTimestamps(timeStamps);
    }
}