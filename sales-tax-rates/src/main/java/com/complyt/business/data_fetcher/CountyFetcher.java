package com.complyt.business.data_fetcher;

import com.complyt.domain.SalesTaxData;
import reactor.core.publisher.Mono;

public interface CountyFetcher {
    Mono<String> fetch(SalesTaxData salesTaxData);
}
