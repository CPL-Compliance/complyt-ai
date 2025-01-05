package com.complyt.repositories.pagination;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface CriteriaBuilder {

    /*
       The filterMap contains key-value pairs sent from the client.
       The filterKeys indicates whether each field should be queried as a fuzzy (regex) search or a regular (exact match) search.
    */
    static Criteria build(Map<String, String> filterMap, Map<String, Boolean> filterKeys) {
        List<Criteria> criterias = new ArrayList<>();

        filterMap.entrySet().stream()
                .filter(entry ->
                        filterKeys.containsKey(entry.getKey()) &&
                                !entry.getValue().isEmpty()
                )
                .map(entry -> {
                    boolean isRegex = Boolean.TRUE.equals(filterKeys.get(entry.getKey()));
                    if (isRegex) {
                        return Criteria.where(entry.getKey()).regex(entry.getValue(), "i");
                    } else {
                        return Criteria.where(entry.getKey()).is(entry.getValue());
                    }
                })
                .forEach(criterias::add);


        return criterias.isEmpty() ? null : new Criteria().andOperator(criterias.toArray(new Criteria[0]));
    }
}
