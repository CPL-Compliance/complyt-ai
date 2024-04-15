package com.complyt.business.tax.gt;

import com.complyt.domain.transaction.tax.GtAddress;
import com.complyt.domain.transaction.tax.GtRates;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.Transaction;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class TransactionGtRatesHandler {

    @NonNull
    private TaxableGtRatesProvider<List<Item>> itemsGtRatesProvider;

    @NonNull
    private TaxableGtRatesProvider<ShippingFee> shippingFeeGtRatesProvider;

    public Mono<Transaction> setRates(@NonNull Transaction transaction, @NonNull GtRates gtRates) {
        String region = transaction.getShippingAddress().region() != null ? transaction.getShippingAddress().region() : "";
        List<Item> itemsWithRates = itemsGtRatesProvider.setGtRates(transaction.getItems(), gtRates,
                new GtAddress(transaction.getShippingAddress().country(), region));

        if (transaction.getShippingFee() != null) {
            ShippingFee shippingFeeWithRates = shippingFeeGtRatesProvider.setGtRates(transaction.getShippingFee(), gtRates,
                    new GtAddress(transaction.getShippingAddress().country(), region));
            transaction = transaction.withShippingFee(shippingFeeWithRates);
        }

        return ContextLogger.observeCtx("Set Gt Rates to Transaction", log::info)
                .then(Mono.just(transaction.withItems(itemsWithRates)));
    }

}