package com.complyt.business.timestamps_injection;

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
        LocalDateTime createdDate = transaction.getInternalTimestamps().getCreatedDate();
        LocalDateTime modifiedDate = LocalDateTime.now();
        Timestamps modifiedTimeStamps = new Timestamps(createdDate, modifiedDate);

        return transaction.withInternalTimestamps(modifiedTimeStamps);
    }
}
