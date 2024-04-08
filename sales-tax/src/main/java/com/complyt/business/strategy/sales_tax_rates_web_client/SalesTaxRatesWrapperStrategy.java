package com.complyt.business.strategy.sales_tax_rates_web_client;

import com.complyt.business.strategy.FunctionSelectorByAddressStrategy;
import com.complyt.business.tax.SalesTaxRatesWebClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.tax.ComplytGtRates;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@AllArgsConstructor
public class SalesTaxRatesWrapperStrategy extends FunctionSelectorByAddressStrategy {
    @NonNull
    SalesTaxRatesWebClientWrapper<ComplytSalesTaxRates> salesTaxWebClientWrapper;
    @NonNull
    SalesTaxRatesWebClientWrapper<ComplytGtRates> gtWebClientWrapper;

    @Override
    protected Function<Address, Mono<ComplytSalesTaxRates>> getFunctionForUsaOption(Transaction transaction) {
        return address -> salesTaxWebClientWrapper.findByAddress(transaction.getShippingAddress());
    }

    @Override
    protected Function<Address, Mono<ComplytGtRates>> getFunctionForNonUsaOption(Transaction transaction) {
        return address -> gtWebClientWrapper.findByAddress(transaction.getShippingAddress());
    }
}
