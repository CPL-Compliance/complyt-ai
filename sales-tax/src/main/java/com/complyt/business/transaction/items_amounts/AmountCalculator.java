package com.complyt.business.transaction.items_amounts;

import lombok.NonNull;

import java.math.BigDecimal;

public interface AmountCalculator<T> {
    BigDecimal calculate(@NonNull T t);
}
