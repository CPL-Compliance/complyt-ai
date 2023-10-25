package com.complyt.business.data_fetcher;

import com.complyt.domain.CityCountyStateWrapper;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.fast_tax.FastTaxGetByCityCountyStateData;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class FastTaxGetByCityCountyStateAddressFetcher implements CityCountyStateAddressFetcher {

    @Override
    public Mono<CityCountyStateWrapper> fetch(@NonNull SalesTaxData salesTaxData) {
        FastTaxGetByCityCountyStateData fastTaxGetByCityCountyStateData = (FastTaxGetByCityCountyStateData) salesTaxData;
        CityCountyStateWrapper cityCountyStateWrapper = new CityCountyStateWrapper(fastTaxGetByCityCountyStateData.getCity(), fastTaxGetByCityCountyStateData.getCounty(), fastTaxGetByCityCountyStateData.getStateAbbreviation());

        return Mono.just(cityCountyStateWrapper);
    }
}
