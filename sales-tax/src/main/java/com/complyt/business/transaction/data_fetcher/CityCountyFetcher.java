package com.complyt.business.transaction.data_fetcher;

import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.CityCountyWrapper;
import reactor.core.publisher.Mono;

public interface CityCountyFetcher {
    Mono<CityCountyWrapper> fetch(Address address);
}
