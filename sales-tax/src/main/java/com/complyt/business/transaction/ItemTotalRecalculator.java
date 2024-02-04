package com.complyt.business.transaction;

import com.complyt.domain.Discountable;
import com.complyt.domain.transaction.Item;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ItemTotalRecalculator {
    public List<Item> recalculate(@NonNull List<Item> items) {
        return items.stream()
                .map(discountable -> discountable.withTotalPrice(totalPriceCalculation(discountable)))
                .collect(Collectors.toList());
    }

    private BigDecimal totalPriceCalculation(Item item) {
        return item.getDiscount() != null ?
                item.getUnitPrice().multiply(item.getQuantity())
                        .subtract(item.getDiscount()) :
                item.getUnitPrice().multiply(item.getQuantity());

    }
}
