package com.complyt.business.data_fetcher;

import com.complyt.domain.SalesTaxData;
import com.complyt.domain.fast_tax.FastTaxData;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@AllArgsConstructor
@Component
public class FastTaxCountyFetcher implements CountyFetcher {

    @Override
    public Mono<String> fetch(SalesTaxData salesTaxData) {
        FastTaxData fastTaxData = (FastTaxData) salesTaxData;
        String countyFromFastTax = fastTaxData.getTaxInfoItems().get(0).getCounty();
        return Mono.just(countyFromFastTax);
    }
}
