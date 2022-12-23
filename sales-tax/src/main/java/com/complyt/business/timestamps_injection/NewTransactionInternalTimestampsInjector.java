package com.complyt.business.timestamps_injection;

import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.domain.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
public class NewTransactionInternalTimestampsInjector implements TimestampsInjector<Transaction> {

    @NonNull
    private final Transaction transaction;

    @Override
    public Transaction inject() {
        LocalDateTime timestamp = LocalDateTime.now();
        ComplytTimestamp createdDate = new ComplytTimestamp(timestamp);
        ComplytTimestamp updatedDate = new ComplytTimestamp(timestamp);
        Timestamps timeStamps = new Timestamps(createdDate, updatedDate);

        return transaction.withInternalTimestamps(timeStamps);
    }
}
