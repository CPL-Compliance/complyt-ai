package com.complyt.repositories.pagination;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface CriteriaBuilder {

    static Criteria build(Map<String, String> filterMap, List<String> filterKeys) {
        List<Criteria> criterias = new ArrayList<>();

        filterMap.entrySet().stream()
                .filter(entry -> filterKeys.contains(entry.getKey()) && entry.getValue() != null && !entry.getValue().isEmpty())
                .map(entry -> Criteria.where(entry.getKey()).regex(entry.getValue(), "i"))
                .forEach(criterias::add);


        return criterias.isEmpty() ? null : new Criteria().andOperator(criterias.toArray(new Criteria[0]));
    }
}
