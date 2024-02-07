package com.complyt.business.transaction;

import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.Transaction;
import com.complyt.security.TenantResolver;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemsTotalCalculator {
    public Mono<Transaction> injectRecalculatedTotal(@NonNull Transaction transaction) { //todo: is this name ok? sometimes it's not recalculating. and this is before the discount
        //todo: should I add the discount calculation to here? this will tie the discount to the total, which is weird...
        //todo: but the discount amount will be removed from this field later in the flow, so maybe it should be here? this is wierd af
        return Mono.just(transaction.withItems(
                recalculateItems(transaction.getItems())
        )).map(recalulatedTransaction -> recalulatedTransaction.getShippingFee() != null ?
                recalulatedTransaction.withShippingFee(shippingFeeCalculation(transaction.getShippingFee())) :
                recalulatedTransaction);
    }

    private List<Item> recalculateItems(List<Item> itemsList) {
        return itemsList.stream()
                .map(item -> item.withCalculatedTotal(totalPriceCalculation(item)))
                .collect(Collectors.toList());
    }

    //todo: maybe this should be in the domains...
    private BigDecimal totalPriceCalculation(Item item) {
        return item.getTotalPrice() != null ?
                item.getTotalPrice() :
                item.getUnitPrice().multiply(item.getQuantity());
    }

    private ShippingFee shippingFeeCalculation(ShippingFee shippingFee) {
        return shippingFee.withCalculatedAmount(shippingFee.getTotalPrice());
    }
}
