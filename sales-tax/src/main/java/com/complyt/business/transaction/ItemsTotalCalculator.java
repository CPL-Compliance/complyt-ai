package com.complyt.business.transaction;

import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.Transaction;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemsTotalCalculator {
    public Mono<Transaction> injectRecalculatedTotal(@NonNull Transaction transaction) { //todo: is this name ok? sometimes it's not recalculating. and this is before the discount
        return Mono.just(transaction.withItems(
                recalculateItems(transaction.getItems())
        )).map(recalulatedTransaction -> recalulatedTransaction.getShippingFee() != null ?
                recalulatedTransaction.withShippingFee(shippingFeeCalculation(transaction.getShippingFee())) :
                recalulatedTransaction);
    }

    private List<Item> recalculateItems(List<Item> itemsList) {
        return itemsList.stream()
                .map(item -> item.withCalculatedTotal(applyDiscountToItemsTotal(item)))
                .collect(Collectors.toList());
    }

    /**
     *
     * @param item
     * @return an BigDecimal that is the value of the items total price after discounts
     * this is set in the field
     */
    private BigDecimal applyDiscountToItemsTotal(Item item) {
        return item.getDiscount() != null ?
                item.getTotalPrice().subtract(item.getDiscount()) :
                item.getTotalPrice();
    }

    /**
     * this is used to aligned shippingFee and items
     * in the future, if another way to calculate shipping fee will be intraduced,
     * this code will need to be changed
     */
    private ShippingFee shippingFeeCalculation(ShippingFee shippingFee) {
        return shippingFee.withCalculatedTotal(shippingFee.getTotalPrice());
    }
}
