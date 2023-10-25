package com.complyt.business.transaction.data_fetcher;

import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.CityCountyStateWrapper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@AllArgsConstructor
@Component
public class TransactionCityCountyStateFetcher implements CityCountyStateFetcher {

    @NonNull
    private SalesTaxWebClientWrapper<ComplytSalesTaxRates> salesTaxWebClientWrapper;

    @Override
    public Mono<CityCountyStateWrapper> fetch(Address address) {
        return salesTaxWebClientWrapper.findByAddress(address)
                .map(complytSalesTaxRates -> new CityCountyStateWrapper(complytSalesTaxRates.address().city(), complytSalesTaxRates.address().county(), complytSalesTaxRates.address().state()));
    }
}