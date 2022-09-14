package com.complyt.utils.data_fetcher;

import com.complyt.domain.Address;
import reactor.core.publisher.Mono;

public interface CountyFetcher {
    Mono<String> fetch(Address address);
}
