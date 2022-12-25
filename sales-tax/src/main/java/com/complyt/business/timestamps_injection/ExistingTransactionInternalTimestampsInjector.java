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
public class ExistingTransactionInternalTimestampsInjector implements TimestampsInjector<Transaction> {

    @NonNull
    private final Transaction transaction;

    @Override
    public Transaction inject() {
        ComplytTimestamp createdDate = new ComplytTimestamp(transaction.getInternalTimestamps().getCreatedDate().getTimestamp());
        ComplytTimestamp modifiedDate = new ComplytTimestamp(LocalDateTime.now());
        Timestamps modifiedTimeStamps = new Timestamps(createdDate, modifiedDate);

        return transaction.withInternalTimestamps(modifiedTimeStamps);
    }
}
