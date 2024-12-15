package com.complyt.business.data_fetcher;

import com.complyt.domain.CityCountyWrapper;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import com.complyt.domain.fast_tax.TaxInfoItem;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.channels.UnresolvedAddressException;

@EqualsAndHashCode
@Component
public class FastTaxGetBestMatchCityCountyFetcher implements CityCountyFetcher {

    @Override
    public Mono<CityCountyWrapper> fetch(@NonNull SalesTaxData salesTaxData) {
        FastTaxGetBestMatchData fastTaxGetBestMatchData = (FastTaxGetBestMatchData) salesTaxData;
        if (fastTaxGetBestMatchData.getTaxInfoItems() == null ) {
            return Mono.error(new UnresolvedAddressException());
        }
        TaxInfoItem taxInfoItem = fastTaxGetBestMatchData.getTaxInfoItems().get(0);
        CityCountyWrapper cityCountyWrapper = new CityCountyWrapper(taxInfoItem.city(), taxInfoItem.county());

        return Mono.just(cityCountyWrapper);
    }
}