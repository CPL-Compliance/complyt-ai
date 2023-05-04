package com.complyt.business.data_fetcher;

import com.complyt.domain.SalesTaxData;
import com.complyt.domain.zip_tax.ZipTaxData;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@AllArgsConstructor
@Component
public class ZipTaxCountyFetcher implements CountyFetcher {

    @Override
    public Mono<String> fetch(@NonNull SalesTaxData salesTaxData) {
        ZipTaxData zipTaxData = (ZipTaxData) salesTaxData;
        String countyFromZipTax = zipTaxData.getResults().get(0).geoCounty();
        return Mono.just(countyFromZipTax);
    }
}
