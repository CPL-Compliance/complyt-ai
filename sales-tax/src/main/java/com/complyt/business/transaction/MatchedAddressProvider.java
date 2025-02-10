package com.complyt.business.transaction;

import com.complyt.business.transaction.data_checker.AddressValidationApplyChecker;
import com.complyt.business.transaction.data_fetcher.MatchedAddressFetcher;
import com.complyt.business.transaction.data_injector.TransactionMatchedAddressInjector;
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
public class MatchedAddressProvider implements SalesTaxDataProvider<Transaction, SalesTaxTrackingWithNexusInfo> {

    @NonNull
    private MatchedAddressFetcher addressFetcher;

    @NonNull
    private TransactionMatchedAddressInjector transactionMatchedAddressInjector;

    @NonNull
    private AddressValidationApplyChecker addressValidationApplyChecker;

    @Override
    public Mono<Transaction> provide(Transaction transaction, SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo) {
        return addressValidationApplyChecker.shouldValidateAddress(transaction, salesTaxTrackingWithNexusInfo) ?
                addressFetcher.fetch(transaction.getShippingAddress())
                        .flatMap(cityCountyWrapper -> transactionMatchedAddressInjector.inject(cityCountyWrapper, transaction)) :
                Mono.just(transaction);
    }
}