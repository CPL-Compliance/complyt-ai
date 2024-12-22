package io.complyt.business.external_fetcher;


import io.complyt.domain.AddressData;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import io.complyt.domain.fast_tax.TaxInfoItem;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.channels.UnresolvedAddressException;

@EqualsAndHashCode
@Component
@Slf4j
public class FastTaxGetBestMatchCityCountyFetcher implements CityCountyFetcher {

    @Override
    public Mono<CachedAddressData> fetch(@NonNull AddressData addressData, @NonNull CachedAddressData cachedAddressData) {
        FastTaxGetBestMatchData fastTaxGetBestMatchData = (FastTaxGetBestMatchData) addressData;
        if (fastTaxGetBestMatchData.getTaxInfoItems() == null ) {
            return Mono.error(new UnresolvedAddressException());
        }
        TaxInfoItem taxInfoItem = fastTaxGetBestMatchData.getTaxInfoItems().get(0);
        cachedAddressData = cachedAddressData.withCity(taxInfoItem.city()).withCounty(taxInfoItem.county());
        log.info("Adding county, city to Here by FastTax: " + cachedAddressData);

        return Mono.just(cachedAddressData);
    }
}