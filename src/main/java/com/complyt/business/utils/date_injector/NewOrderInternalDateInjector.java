package com.complyt.business.utils.date_injector;

import com.complyt.domain.Order;
import com.complyt.domain.TimeStamps;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
@AllArgsConstructor
public class NewOrderInternalDateInjector implements DateInjector<Order> {

    @NonNull
    private Order order;

    @Override
    public Order inject() {
        TimeStamps timeStamps = new TimeStamps(new Date(),new Date());
        return order.withInternalTimeStamps(timeStamps);
    }
}
