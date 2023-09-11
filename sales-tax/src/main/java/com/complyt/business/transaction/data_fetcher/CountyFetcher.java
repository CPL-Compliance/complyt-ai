package com.complyt.business.transaction.data_fetcher;

import com.complyt.domain.transaction.Address;
import reactor.core.publisher.Mono;

public interface CountyFetcher {
    Mono<String> fetch(Address address);
}
