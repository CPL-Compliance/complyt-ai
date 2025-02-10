package com.complyt.business.transaction.data_injector;

import com.complyt.domain.transaction.*;
import com.complyt.utils.observability.ContextLogger;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public record TransactionMatchedAddressInjector() implements TransactionDataInjector<MatchedAddressData> {

    @Override
    public Mono<Transaction> inject(@NonNull MatchedAddressData matchedAddressData, @NonNull Transaction transaction) {
        ShippingAddress modifiedAddress = transaction.getShippingAddress()
                .withMatchedAddressData(matchedAddressData);

        return ContextLogger.observeCtx("Inject Matched Address to shipping Address " + matchedAddressData, log::info)
                .then(Mono.just(transaction.withShippingAddress(modifiedAddress)));
    }
}