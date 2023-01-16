package com.complyt.business.transaction.items_amounts;

import com.complyt.domain.Transaction;
import lombok.NonNull;

public interface TransactionAmountsCollector<T> {
    Transaction collect(@NonNull T t);
}
