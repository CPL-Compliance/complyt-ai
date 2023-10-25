package com.complyt.business.data_fetcher;

import com.complyt.domain.CityCountyStateWrapper;
import com.complyt.domain.SalesTaxData;
import reactor.core.publisher.Mono;

public interface CityCountyStateAddressFetcher {
    Mono<CityCountyStateWrapper> fetch(SalesTaxData salesTaxData);
}
