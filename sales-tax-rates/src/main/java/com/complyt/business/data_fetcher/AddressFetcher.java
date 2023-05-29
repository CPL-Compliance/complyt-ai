package com.complyt.business.data_fetcher;

import com.complyt.domain.Address;
import com.complyt.domain.SalesTaxData;
import reactor.core.publisher.Mono;

public interface AddressFetcher {
    Mono<Address> fetch(SalesTaxData salesTaxData);
}
