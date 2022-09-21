package com.complyt.business.date_injector;

import com.complyt.domain.TimeStamps;
import com.complyt.domain.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
public class ModifiedTransactionInternalDateInjector implements DateInjector<Transaction> {

    @NonNull
    private Transaction transaction;

    @Override
    public Transaction inject() {
        LocalDateTime createdDate = transaction.getInternalTimeStamps().getCreatedDate();
        LocalDateTime modifiedDate = LocalDateTime.now();
        TimeStamps modifiedTimeStamps = new TimeStamps(createdDate,modifiedDate);

        return transaction.withInternalTimeStamps(modifiedTimeStamps);
    }
}
