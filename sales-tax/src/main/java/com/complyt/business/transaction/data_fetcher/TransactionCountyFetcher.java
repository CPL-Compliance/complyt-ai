package com.complyt.business.transaction.data_fetcher;

import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@AllArgsConstructor
@Component
public class TransactionCountyFetcher implements CountyFetcher {

    @NonNull
    private SalesTaxWebClientWrapper<ComplytSalesTaxRates> salesTaxWebClientWrapper;

    @Override
    public Mono<String> fetch(Address address) {
        return salesTaxWebClientWrapper.findByAddress(address)
                .map(complytSalesTaxRates -> complytSalesTaxRates.address().county());
    }
}
