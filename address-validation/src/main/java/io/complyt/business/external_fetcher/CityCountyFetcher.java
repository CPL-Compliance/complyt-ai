package io.complyt.business.external_fetcher;

import io.complyt.domain.AddressData;
import io.complyt.domain.CachedAddressData;
import reactor.core.publisher.Mono;

public interface CityCountyFetcher {
    Mono<CachedAddressData> fetch(AddressData addressData, CachedAddressData cachedAddressData);
}
