package com.complyt.business.transaction;

import com.complyt.domain.transaction.Transaction;
import reactor.core.publisher.Mono;


public interface DiscountCalculator {

    Mono<Transaction> injectRecalculatedTotalAfterDiscount(Transaction transaction);

}
