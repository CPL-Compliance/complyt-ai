package com.complyt.business.data_fetcher;

import com.complyt.domain.CityCountyWrapper;
import com.complyt.domain.SalesTaxData;
import reactor.core.publisher.Mono;

public interface CityCountyFetcher {
    Mono<CityCountyWrapper> fetch(SalesTaxData salesTaxData);
}
