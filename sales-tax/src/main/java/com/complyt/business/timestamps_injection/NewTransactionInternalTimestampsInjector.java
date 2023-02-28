package com.complyt.business.timestamps_injection;

import com.complyt.domain.Transaction;
import com.complyt.domain.timestamps.Timestamps;
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
        LocalDateTime createdDate = timestamp;
        LocalDateTime updatedDate = timestamp;
        Timestamps timeStamps = new Timestamps(createdDate, updatedDate);

        return transaction.withInternalTimestamps(timeStamps);
    }
}
