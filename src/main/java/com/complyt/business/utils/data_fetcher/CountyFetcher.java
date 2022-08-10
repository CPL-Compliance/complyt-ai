package com.complyt.business.utils.data_fetcher;

import com.complyt.domain.Transaction;
import reactor.core.publisher.Mono;

public interface CountyFetcher {
    Mono<Transaction> fetch(Transaction transaction);
}
