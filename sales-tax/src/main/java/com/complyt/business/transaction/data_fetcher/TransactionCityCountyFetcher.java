package com.complyt.business.transaction.data_fetcher;

import com.complyt.business.tax.SalesTaxRatesWebClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.CityCountyWrapper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@AllArgsConstructor
@Component
public class TransactionCityCountyFetcher implements CityCountyFetcher {

    @NonNull
    private SalesTaxRatesWebClientWrapper<ComplytSalesTaxRates> salesTaxWebClientWrapper;

    @Override
    public Mono<CityCountyWrapper> fetch(Address address) {
        return salesTaxWebClientWrapper.findByAddress(address)
                .map(complytSalesTaxRates -> new CityCountyWrapper(complytSalesTaxRates.address().city(), complytSalesTaxRates.address().county()));
    }
}