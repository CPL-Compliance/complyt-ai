package com.complyt.business.utils.data_fetcher;

import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@AllArgsConstructor
@Component
public class TransactionFastTaxCountyFetcher implements CountyFetcher {

    @NonNull
    private SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @Override
    public Mono<String> fetch(Address address) {
        return salesTaxWebClientWrapper.findByAddress(address)
                .map(salesTaxData -> {
                    FastTaxData fastTaxData = (FastTaxData) salesTaxData;
                    String countyFromFastTax = fastTaxData.getTaxInfoItems().get(0).getCounty();
                    return countyFromFastTax;
                });
    }
}
