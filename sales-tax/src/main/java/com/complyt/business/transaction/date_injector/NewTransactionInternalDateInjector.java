package com.complyt.business.transaction.date_injector;

import com.complyt.domain.Timestamps;
import com.complyt.domain.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
public class NewTransactionInternalDateInjector implements DateInjector<Transaction> {

    @NonNull
    private final Transaction transaction;

    @Override
    public Transaction inject() {
        Timestamps timeStamps = new Timestamps(LocalDateTime.now(), LocalDateTime.now());
        return transaction.withInternalTimestamps(timeStamps);
    }
}
