package com.complyt.business.timestamps_injection;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.timestamps.ComplytTimestamp;
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
        LocalDateTime timestamp = LocalDateTime.now();
        ComplytTimestamp createdDate = new ComplytTimestamp(timestamp);
        ComplytTimestamp updatedDate = new ComplytTimestamp(timestamp);
        Timestamps timeStamps = new Timestamps(createdDate, updatedDate);

        return customer.withInternalTimestamps(timeStamps);
    }
}