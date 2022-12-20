package com.complyt.business.dates_injection;

import com.complyt.domain.TimeStamps;
import com.complyt.domain.customer.Customer;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@AllArgsConstructor
public class NewCustomerInternalDateInjector implements DateInjector<Customer> {

    @NonNull
    private final Customer customer;

    @Override
    public Customer inject() {
        TimeStamps timeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        return customer.withInternalTimeStamps(timeStamps);
    }
}