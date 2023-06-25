package com.complyt.business.data_fetcher;

import com.complyt.annotations.Generated;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.taxjar.TaxJarData;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@AllArgsConstructor
@Generated
@Component
public class TaxJarCountyFetcher implements CountyFetcher {

    @Override
    public Mono<String> fetch(@NonNull SalesTaxData salesTaxData) {
        TaxJarData taxJarData = (TaxJarData) salesTaxData;
        String countyFromTaxJar = taxJarData.getRate().getCounty();
        return Mono.just(countyFromTaxJar);
    }

}