package com.complyt.business.transaction.items_amounts;

import com.complyt.domain.Discountable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class DiscountablesTotalDiscountCalculator implements AmountCalculator<List<Discountable>> {

    @Override
    public BigDecimal calculate(@NonNull List<Discountable> discountables) {
         BigDecimal discountAmount = discountables.stream()
                 .map(Discountable::getDiscount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("Total discount of transaction calculated: " + discountAmount);

        return discountAmount;
    }
}
