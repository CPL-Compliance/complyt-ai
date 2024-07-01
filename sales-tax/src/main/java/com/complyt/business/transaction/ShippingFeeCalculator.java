package com.complyt.business.transaction;

import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.Transaction;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
public class ShippingFeeCalculator implements DiscountCalculator {

    @Override
    public Mono<Transaction> injectRecalculatedTotalAfterDiscount(@NonNull Transaction transaction) {
        return transaction.getShippingFee() != null ?
                Mono.just(transaction.setShippingFee(shippingFeeCalculation(transaction.getShippingFee()))) :
                Mono.just(transaction);
    }
    /**
     * this is used to aligned shippingFee
     * in the future, if another way to calculate shipping fee will be intraduced,
     * this code will need to be changed
     */
    private ShippingFee shippingFeeCalculation(ShippingFee shippingFee) {
        return shippingFee.withCalculatedTotal(shippingFee.getTotalPrice());
    }
}
