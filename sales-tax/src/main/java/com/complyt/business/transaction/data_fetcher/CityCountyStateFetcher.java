package com.complyt.business.transaction.data_fetcher;

import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.CityCountyStateWrapper;
import reactor.core.publisher.Mono;

public interface CityCountyStateFetcher {
    Mono<CityCountyStateWrapper> fetch(Address address);
}
