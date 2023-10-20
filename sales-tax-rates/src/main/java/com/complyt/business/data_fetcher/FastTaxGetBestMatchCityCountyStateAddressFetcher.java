package com.complyt.business.data_fetcher;

import com.complyt.domain.CityCountyState;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import com.complyt.domain.fast_tax.TaxInfoItem;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@AllArgsConstructor
@Component
public class FastTaxGetBestMatchCityCountyStateAddressFetcher implements CityCountyStateAddressFetcher {

    @Override
    public Mono<CityCountyState> fetch(@NonNull SalesTaxData salesTaxData) {
        FastTaxGetBestMatchData fastTaxGetBestMatchData = (FastTaxGetBestMatchData) salesTaxData;
        TaxInfoItem taxInfoItem = fastTaxGetBestMatchData.getTaxInfoItems().get(0);
        CityCountyState cityCountyState = new CityCountyState(taxInfoItem.city(), taxInfoItem.county(), taxInfoItem.stateAbbreviation());

        return Mono.just(cityCountyState);
    }
}