package com.complyt.utils.date_injector;

import com.complyt.domain.TimeStamps;
import com.complyt.domain.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
public class NewTransactionInternalDateInjector implements DateInjector<Transaction> {

    @NonNull
    private Transaction transaction;

    @Override
    public Transaction inject() {
        TimeStamps timeStamps = new TimeStamps(LocalDateTime.now(),LocalDateTime.now());
        return transaction.withInternalTimeStamps(timeStamps);
    }
}
