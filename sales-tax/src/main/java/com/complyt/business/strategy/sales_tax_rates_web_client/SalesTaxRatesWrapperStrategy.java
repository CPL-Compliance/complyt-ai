package com.complyt.business.strategy.sales_tax_rates_web_client;

import com.complyt.business.strategy.FunctionSelectorByAddressStrategy;
import com.complyt.business.tax.SalesTaxRatesWebClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.MandatoryAddress;
import com.complyt.domain.transaction.tax.ComplytGtRates;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class SalesTaxRatesWrapperStrategy extends FunctionSelectorByAddressStrategy {

    @NonNull
    SalesTaxRatesWebClientWrapper<ComplytSalesTaxRates> salesTaxWebClientWrapper;

    @NonNull
    SalesTaxRatesWebClientWrapper<ComplytGtRates> gtWebClientWrapper;

    @Override
    protected Function<LocalDateTime, Mono<ComplytSalesTaxRates>> getFunctionForUsaOption(MandatoryAddress address) {
        return dateTime -> salesTaxWebClientWrapper.findByAddress(address, dateTime);
    }

    @Override
    protected Function<LocalDateTime, Mono<ComplytGtRates>> getFunctionForNonUsaOption(MandatoryAddress address) {
        return dateTime -> gtWebClientWrapper.findByAddress(address, dateTime);
    }
}
