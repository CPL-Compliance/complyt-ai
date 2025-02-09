package com.complyt.repositories.pagination.customer;

import java.util.List;
import java.util.Map;

public interface CustomerPaginationUtil {

    /*
        The filterMap indicates whether each field should be queried as a fuzzy (regex) search or a regular (exact match) search.
        fuzzy (true) / exact (false)
    */
    Map<String, Boolean> customerFilterMap = Map.of(
            "externalId", Boolean.TRUE,
            "name", Boolean.TRUE,
            "customerType", Boolean.TRUE,
            "address.country", Boolean.TRUE,
            "address.state", Boolean.TRUE,
            "address.city", Boolean.TRUE
    );
    List<String> customerSortByFields = List.of(
            "externalId",
            "name",
            "customerType",
            "address.country",
            "address.state",
            "address.city"
    );

}