package com.complyt.repositories.pagination.customer;

import java.util.List;

public interface CustomerPaginationUtil {

    List<String> customerFilterKeys = List.of("externalId", "name", "customerType", "address.country", "address.state", "address.city");
    List<String> customerSortByFields = List.of("externalId", "name", "customerType", "address.country", "address.state", "address.city");
    String DEFAULT_SORT_BY = "externalTimestamps.createdDate";

}