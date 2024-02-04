package com.complyt.business.transaction.items_amounts;

import com.complyt.domain.Discountable;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

public class DiscountablesTotalDiscountCalculator<T> implements AmountCalculator<List<Discountable>> {

    @Override
    public BigDecimal calculate(@NonNull List<Discountable> discountables) {
        return discountables.stream()
                .map(Discountable::getDiscount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }
}
