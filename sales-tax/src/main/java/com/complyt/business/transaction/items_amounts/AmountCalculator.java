package com.complyt.business.transaction.items_amounts;

import lombok.NonNull;

public interface AmountCalculator<T> {
    float calculate(@NonNull T t);
}
