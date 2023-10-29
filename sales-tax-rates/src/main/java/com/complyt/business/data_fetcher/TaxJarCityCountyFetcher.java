package com.complyt.business.data_fetcher;

import com.complyt.annotations.Generated;
import com.complyt.domain.CityCountyWrapper;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.taxjar.TaxJarData;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@Generated
@Component
public class TaxJarCityCountyFetcher implements CityCountyFetcher {

    @Override
    public Mono<CityCountyWrapper> fetch(@NonNull SalesTaxData salesTaxData) {
        TaxJarData taxJarData = (TaxJarData) salesTaxData;
        CityCountyWrapper cityCountyWrapper = new CityCountyWrapper(taxJarData.getRate().getCity(), taxJarData.getRate().getCounty());

        return Mono.just(cityCountyWrapper);
    }

}