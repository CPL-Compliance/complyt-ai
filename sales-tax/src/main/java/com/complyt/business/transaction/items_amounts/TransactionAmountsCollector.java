package com.complyt.business.transaction.items_amounts;

import com.complyt.domain.transaction.Transaction;
import lombok.NonNull;

public interface TransactionAmountsCollector<T> {
    Transaction collect(@NonNull T t);
}
