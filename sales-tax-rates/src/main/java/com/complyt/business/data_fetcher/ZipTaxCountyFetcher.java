package com.complyt.business.data_fetcher;

import com.complyt.domain.SalesTaxData;
import com.complyt.domain.zip_tax.ZipTaxData;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@AllArgsConstructor
@Component
public class ZipTaxCountyFetcher implements CountyFetcher {

    @Override
    public Mono<String> fetch(SalesTaxData salesTaxData) {
        ZipTaxData zipTaxData = (ZipTaxData) salesTaxData;
        String countyFromZipTax = zipTaxData.getResults().get(0).getGeoCounty();
        return Mono.just(countyFromZipTax);
    }
}
