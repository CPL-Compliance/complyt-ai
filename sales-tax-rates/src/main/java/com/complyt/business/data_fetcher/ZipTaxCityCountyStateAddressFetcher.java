package com.complyt.business.data_fetcher;

import com.complyt.domain.CityCountyStateWrapper;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.zip_tax.Result;
import com.complyt.domain.zip_tax.ZipTaxData;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@AllArgsConstructor
@Component
public class ZipTaxCityCountyStateAddressFetcher implements CityCountyStateAddressFetcher {

    @Override
    public Mono<CityCountyStateWrapper> fetch(@NonNull SalesTaxData salesTaxData) {
        ZipTaxData zipTaxData = (ZipTaxData) salesTaxData;
        Result result = zipTaxData.getResults().get(0);
        CityCountyStateWrapper cityCountyStateWrapper = new CityCountyStateWrapper(result.geoCity(), result.geoCounty(), result.geoState());

        return Mono.just(cityCountyStateWrapper);
    }
}