package com.complyt.business.data_fetcher;

import com.complyt.domain.CityCountyStateWrapper;
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
    public Mono<CityCountyStateWrapper> fetch(@NonNull SalesTaxData salesTaxData) {
        FastTaxGetBestMatchData fastTaxGetBestMatchData = (FastTaxGetBestMatchData) salesTaxData;
        TaxInfoItem taxInfoItem = fastTaxGetBestMatchData.getTaxInfoItems().get(0);
        CityCountyStateWrapper cityCountyStateWrapper = new CityCountyStateWrapper(taxInfoItem.city(), taxInfoItem.county(), taxInfoItem.stateAbbreviation());

        return Mono.just(cityCountyStateWrapper);
    }
}