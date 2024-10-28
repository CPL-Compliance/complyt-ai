package com.complyt.repositories.pagination;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface CriteriaBuilder {

    static Criteria build(Map<String, String> filterMap, List<String> filterKeys) {
        List<Criteria> criterias = new ArrayList<>();

        for (String key : filterMap.keySet()) {
            if (filterKeys.contains(key)) {
                criterias.add(Criteria.where(key).is(filterMap.get(key)));
            }
        }

        return !criterias.isEmpty() ? new Criteria().andOperator(criterias.toArray(new Criteria[0])) : null;
    }
}
