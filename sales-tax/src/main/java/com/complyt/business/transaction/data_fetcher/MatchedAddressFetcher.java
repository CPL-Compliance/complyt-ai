package com.complyt.business.transaction.data_fetcher;

import com.complyt.domain.transaction.MatchedAddressData;
import com.complyt.domain.transaction.ShippingAddress;
import reactor.core.publisher.Mono;

public interface MatchedAddressFetcher {
    Mono<MatchedAddressData> fetch(ShippingAddress address);
}
