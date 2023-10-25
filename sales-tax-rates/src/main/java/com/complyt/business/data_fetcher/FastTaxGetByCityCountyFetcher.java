package com.complyt.business.data_fetcher;

import com.complyt.domain.CityCountyWrapper;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.fast_tax.FastTaxGetByCityCountyData;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class FastTaxGetByCityCountyFetcher implements CityCountyFetcher {

    @Override
    public Mono<CityCountyWrapper> fetch(@NonNull SalesTaxData salesTaxData) {
        FastTaxGetByCityCountyData fastTaxGetByCityCountyData = (FastTaxGetByCityCountyData) salesTaxData;
        CityCountyWrapper cityCountyWrapper = new CityCountyWrapper(fastTaxGetByCityCountyData.getCity(), fastTaxGetByCityCountyData.getCounty());

        return Mono.just(cityCountyWrapper);
    }
}
