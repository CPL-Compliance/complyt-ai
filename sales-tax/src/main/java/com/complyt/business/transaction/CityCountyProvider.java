package com.complyt.business.transaction;

import com.complyt.business.transaction.data_checker.AddressValidationApplyChecker;
import com.complyt.business.transaction.data_fetcher.CityCountyFetcher;
import com.complyt.business.transaction.data_injector.TransactionCityCountyInjector;
import com.complyt.domain.decorator.SalesTaxTrackingWithNexusInfo;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class CityCountyProvider implements SalesTaxDataProvider<Transaction, SalesTaxTrackingWithNexusInfo> {

    @NonNull
    private CityCountyFetcher addressFetcher;

    @NonNull
    private TransactionCityCountyInjector transactionCityCountyInjector;

    @NonNull
    private AddressValidationApplyChecker addressValidationApplyChecker;

    @Override
    public Mono<Transaction> provide(Transaction transaction, SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo) {
        return addressValidationApplyChecker.shouldValidateAddress(transaction, salesTaxTrackingWithNexusInfo) ?
                addressFetcher.fetch(transaction.getShippingAddress())
                        .flatMap(cityCountyWrapper -> transactionCityCountyInjector.inject(cityCountyWrapper, transaction)) :
                Mono.just(transaction);
    }
}