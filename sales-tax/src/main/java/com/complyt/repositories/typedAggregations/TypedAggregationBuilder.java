package com.complyt.repositories.typedAggregations;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

public interface TypedAggregationBuilder<T> {
    TypedAggregation<T> getAllAggregation(String tenantId, Criteria criteria, Sort.Direction sortDirection, String sortByProperty,
                                          int calculatedOffset, int size, Boolean isProjectionAggregation);
}