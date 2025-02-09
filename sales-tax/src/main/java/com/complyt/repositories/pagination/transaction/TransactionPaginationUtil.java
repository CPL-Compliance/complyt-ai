package com.complyt.repositories.pagination.transaction;

import java.util.List;
import java.util.Map;

public interface TransactionPaginationUtil {

    /*
        The filterMap indicates whether each field should be queried as a fuzzy (regex) search or a regular (exact match) search.
        fuzzy (true) / exact (false)
    */
    Map<String, Boolean> transactionFilterMap = Map.of(
            "externalId", Boolean.FALSE,
            "documentName", Boolean.FALSE,
            "transactionStatus", Boolean.TRUE,
            "externalTimestamps.createdDate", Boolean.TRUE,
            "transactionType", Boolean.TRUE,
            "shippingAddress.country", Boolean.TRUE,
            "shippingAddress.state", Boolean.TRUE,
            "shippingAddress.city", Boolean.TRUE
    );

    List<String> transactionSortByFields = List.of(
            "externalId",
            "documentName",
            "transactionStatus",
            "externalTimestamps.createdDate",
            "transactionType",
            "shippingAddress.country",
            "shippingAddress.state",
            "shippingAddress.city",
            "shippingAddress.zip"
    );
}