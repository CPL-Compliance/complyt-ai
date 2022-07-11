package com.complyt.business.query;

import lombok.NonNull;
import org.springframework.data.mongodb.core.query.Query;

public interface QueryBuilder<T> {
    Query build(@NonNull T t);
}
