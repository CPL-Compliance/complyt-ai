package com.complyt.repositories.pagination;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface CriteriaBuilder {

    static Criteria build(Map<String, String> filterMap, List<String> filterKeys) {
        List<Criteria> criterias = new ArrayList<>();

        filterMap.keySet().stream()
                .filter(filterKeys::contains)
                .map(key -> Criteria.where(key).is(filterMap.get(key)))
                .forEach(criterias::add);

        return !criterias.isEmpty() ? new Criteria().andOperator(criterias.toArray(new Criteria[0])) : null;
    }
}
