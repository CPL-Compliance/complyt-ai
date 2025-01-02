package com.complyt.repositories;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;

public interface TransactionRepositoryCommonStagesBuilder {
    static Criteria tenantIdExternalIdAndSourceExactCriteria(String tenantId, String externalId, String source) {
        return Criteria.where("tenantId").is(tenantId)
                .and("externalId").is(externalId)
                .and("source").is(source);
    }

    static LookupOperation customerLookupByTransactionCustomerIdAndTenant(String tenantId) {
        return Aggregation.lookup()
                .from("customer")
                .localField("customerId")
                .foreignField("complytId")
                .pipeline(Aggregation.match(Criteria.where("tenantId").is(tenantId)))
                .as("customer");
    }
}
