package com.complyt.services.crud;

import reactor.core.publisher.Flux;

import java.util.Map;

public interface PaginationAble<T> {
    Flux<T> findAll(int page, int size, Map<String, String> filterMap, String sortOrder, String sortBy);
}
