package com.complyt.business.transaction;

import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemsDiscountCalculator implements DiscountCalculator {

    @Override
    public Mono<Transaction> injectRecalculatedTotalAfterDiscount(@NonNull Transaction transaction) {
        return Mono.just(transaction.setItems(
                recalculateItems(transaction.getItems())
        ));
    }

    private List<Item> recalculateItems(List<Item> itemsList) {
        return itemsList.stream()
                .map(item -> item.setCalculatedTotal(applyDiscountToItemsTotal(item)))
                .collect(Collectors.toList());
    }

    /**
     * @param item
     * @return an BigDecimal that is the value of the items total price after discounts
     * this is set in the field
     */
    private BigDecimal applyDiscountToItemsTotal(Item item) {
        return item.getDiscount() != null ?
                item.getTotalPrice().subtract(item.getDiscount()) :
                item.getTotalPrice();
    }
}
