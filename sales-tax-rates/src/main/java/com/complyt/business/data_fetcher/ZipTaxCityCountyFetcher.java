package com.complyt.business.data_fetcher;

import com.complyt.domain.CityCountyWrapper;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.zip_tax.Result;
import com.complyt.domain.zip_tax.ZipTaxData;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@Component
public class ZipTaxCityCountyFetcher implements CityCountyFetcher {

    @Override
    public Mono<CityCountyWrapper> fetch(@NonNull SalesTaxData salesTaxData) {
        ZipTaxData zipTaxData = (ZipTaxData) salesTaxData;
        Result result = zipTaxData.getResults().get(0);
        CityCountyWrapper cityCountyWrapper = new CityCountyWrapper(result.geoCity(), result.geoCounty());

        return Mono.just(cityCountyWrapper);
    }
}