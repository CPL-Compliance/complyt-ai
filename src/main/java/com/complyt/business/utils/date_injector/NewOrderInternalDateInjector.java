package com.complyt.business.utils.date_injector;

import com.complyt.domain.Order;
import com.complyt.domain.TimeStamps;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
public class NewOrderInternalDateInjector implements DateInjector<Order> {

    @NonNull
    private Order order;

    @Override
    public Order inject() {
        TimeStamps timeStamps = new TimeStamps(LocalDateTime.now(),LocalDateTime.now());
        return order.withInternalTimeStamps(timeStamps);
    }
}
