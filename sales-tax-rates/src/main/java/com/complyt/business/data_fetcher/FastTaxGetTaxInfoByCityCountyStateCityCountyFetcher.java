package com.complyt.business.data_fetcher;

import com.complyt.domain.CityCountyWrapper;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.fast_tax.FastTaxGetByCityCountyStateData;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@EqualsAndHashCode
public class FastTaxGetTaxInfoByCityCountyStateCityCountyFetcher implements CityCountyFetcher {

    @Override
    public Mono<CityCountyWrapper> fetch(@NonNull SalesTaxData salesTaxData) {
        FastTaxGetByCityCountyStateData fastTaxGetByCityCountyStateData = (FastTaxGetByCityCountyStateData) salesTaxData;
        CityCountyWrapper cityCountyWrapper = new CityCountyWrapper(fastTaxGetByCityCountyStateData.getCity(), fastTaxGetByCityCountyStateData.getCounty());

        return Mono.just(cityCountyWrapper);
    }
}