package com.complyt.repositories;

import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDateTime;

public interface AggregationBuilder<T> {
    TypedAggregation<T> build(LocalDateTime requiredDate, Criteria matchCriteria, String stateName);
}
