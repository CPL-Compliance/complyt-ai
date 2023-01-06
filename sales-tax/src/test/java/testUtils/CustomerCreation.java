package testUtils;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.domain.timestamps.Timestamps;

import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerCreation {
    public Customer createCustomer() {
        ComplytTimestamp complytTimestamp = new ComplytTimestamp(LocalDateTime.now());
        Timestamps internalTimeStamps = new Timestamps(complytTimestamp, complytTimestamp);
        ComplytTimestamp complytTimestampMinusOneMinute = new ComplytTimestamp(LocalDateTime.now().minusMinutes(1));
        Timestamps externalTimestamps = new Timestamps(complytTimestampMinusOneMinute, complytTimestamp);
        return new Customer(
                UUID.randomUUID(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "1",
                "name",
                null,
                UUID.randomUUID().toString(),
                CustomerType.RETAIL,
                internalTimeStamps,
                externalTimestamps
        );
    }

}
