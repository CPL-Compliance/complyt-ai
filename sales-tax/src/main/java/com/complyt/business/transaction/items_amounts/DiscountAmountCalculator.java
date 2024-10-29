package com.complyt.business.transaction.items_amounts;

import lombok.NonNull;

import java.math.BigDecimal;

public interface DiscountAmountCalculator<T> {
    BigDecimal calculate(@NonNull T t);
}