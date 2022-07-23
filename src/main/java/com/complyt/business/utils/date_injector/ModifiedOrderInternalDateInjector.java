package com.complyt.business.utils.date_injector;

import com.complyt.domain.Order;
import com.complyt.domain.TimeStamps;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
@AllArgsConstructor
public class ModifiedOrderInternalDateInjector implements DateInjector<Order> {

    @NonNull
    private Order order;

    @Override
    public Order inject() {
        Date createdDate = order.getInternalTimeStamps().getCreatedDate();
        Date modifiedDate = new Date();
        TimeStamps modifiedTimeStamps = new TimeStamps(createdDate,modifiedDate);

        return order.withInternalTimeStamps(modifiedTimeStamps);
    }
}
