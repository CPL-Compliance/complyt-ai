package com.complyt.repositories.pagination.transaction;

import java.util.List;

public interface TransactionPaginationUtil {
    List<String> transactionFilterKeys = List.of("externalId", "documentName", "transactionStatus", "externalTimestamps.createdDate", "transactionType");
    List<String> transactionSortByFields = List.of(
            "externalId", "documentName", "transactionStatus", "externalTimestamps.createdDate",
            "transactionType", "shippingAddress.country", "shippingAddress.state", "shippingAddress.city"
    );
    String DEFAULT_SORT_BY = "externalTimestamps.createdDate";
}