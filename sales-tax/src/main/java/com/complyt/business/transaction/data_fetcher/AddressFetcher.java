package com.complyt.business.transaction.data_fetcher;

import com.complyt.domain.Address;
import reactor.core.publisher.Mono;

public interface AddressFetcher {
    Mono<Address> fetch(Address address);
}
