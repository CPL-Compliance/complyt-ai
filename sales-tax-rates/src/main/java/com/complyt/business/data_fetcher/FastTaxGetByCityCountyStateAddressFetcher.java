package com.complyt.business.data_fetcher;

import com.complyt.domain.CityCountyState;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.fast_tax.FastTaxGetByCityCountyStateData;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class FastTaxGetByCityCountyStateAddressFetcher implements CityCountyStateAddressFetcher {

    @Override
    public Mono<CityCountyState> fetch(@NonNull SalesTaxData salesTaxData) {
        FastTaxGetByCityCountyStateData fastTaxGetByCityCountyStateData = (FastTaxGetByCityCountyStateData) salesTaxData;
        CityCountyState cityCountyState = new CityCountyState(fastTaxGetByCityCountyStateData.getCity(), fastTaxGetByCityCountyStateData.getCounty(), fastTaxGetByCityCountyStateData.getStateAbbreviation());

        return Mono.just(cityCountyState);
    }
}
