package com.complyt.repositories.typedAggregations;

import com.complyt.domain.transaction.Transaction;
import com.complyt.repositories.TransactionProjectionStage;
import com.complyt.repositories.TransactionRepositoryCommonStagesBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregationOptions;

@Component
public class TransactionTypedAggregationBuilder implements TypedAggregationBuilder<Transaction> {
    @Override
    public TypedAggregation<Transaction> getAllAggregation(String tenantId, Criteria criteria, Sort.Direction sortDirection, String sortByProperty,
                                                           int calculatedOffset, int size, Boolean isProjectionAggregation) {

        ArrayList<AggregationOperation> operations = new ArrayList<>() {{
            add(Aggregation.match(criteria));
            add(Aggregation.sort(Sort.by(sortDirection, sortByProperty)));
            add(Aggregation.sort(Sort.by(sortDirection, sortByProperty)));
            add(Aggregation.skip(calculatedOffset));
            add(Aggregation.limit(size));
            add(TransactionRepositoryCommonStagesBuilder.customerLookupByTransactionCustomerIdAndTenant(tenantId));
            add(Aggregation.unwind("customer", true));
        }};

        if (isProjectionAggregation) {
            operations.add(Aggregation.addFields().addFieldWithValue("items", TransactionProjectionStage.itemsMapAddFeildStageDocument()).build());
            operations.add(Aggregation.stage(TransactionProjectionStage.projectionStageDocument()));
        }

        TypedAggregation<Transaction> aggregation = Aggregation.newAggregation(Transaction.class,
                operations).withOptions(newAggregationOptions().cursorBatchSize(size).build());

        return aggregation;
    }
}
