package com.complyt.business.data_fetcher;

import com.complyt.annotations.Generated;
import com.complyt.domain.CityCountyState;
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
public class TaxJarCityCountyStateAddressFetcher implements CityCountyStateAddressFetcher {

    @Override
    public Mono<CityCountyState> fetch(@NonNull SalesTaxData salesTaxData) {
        TaxJarData taxJarData = (TaxJarData) salesTaxData;
        CityCountyState cityCountyState = new CityCountyState(taxJarData.getRate().getCity(), taxJarData.getRate().getCounty(), taxJarData.getRate().getState());

        return Mono.just(cityCountyState);
    }

}