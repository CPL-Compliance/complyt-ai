package com.complyt.business.data_fetcher;

import com.complyt.domain.CityCountyState;
import com.complyt.domain.SalesTaxData;
import reactor.core.publisher.Mono;

public interface CityCountyStateAddressFetcher {
    Mono<CityCountyState> fetch(SalesTaxData salesTaxData);
}
