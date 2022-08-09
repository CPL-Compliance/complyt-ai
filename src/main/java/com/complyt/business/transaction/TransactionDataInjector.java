package com.complyt.business.transaction;

import com.complyt.domain.Transaction;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface TransactionDataInjector<T> {
    Mono<Transaction> act(Map<String, T> t);
}