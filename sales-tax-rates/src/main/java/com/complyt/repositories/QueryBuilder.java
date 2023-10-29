package com.complyt.repositories;

import org.springframework.data.mongodb.core.query.Query;

public interface QueryBuilder<T> {
    Query build(T t);
}
