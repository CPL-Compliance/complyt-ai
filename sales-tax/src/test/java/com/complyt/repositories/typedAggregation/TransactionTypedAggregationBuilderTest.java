package com.complyt.repositories.typedAggregation;

import com.complyt.domain.transaction.Transaction;
import com.complyt.repositories.TransactionProjectionStage;
import com.complyt.repositories.TransactionRepositoryCommonStagesBuilder;
import com.complyt.repositories.typedAggregations.TransactionTypedAggregationBuilder;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregationOptions;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class TransactionTypedAggregationBuilderTest {

    @InjectMocks
    TransactionTypedAggregationBuilder transactionTypedAggregationBuilder;

    String tenantId;
    Criteria criteria;
    Sort.Direction sortDirection;
    String sortByProperty;
    int calculatedOffset;
    int size;
    Boolean isProjectionAggregation;
    TypedAggregation<Transaction> expectedAggregation;




    @BeforeEach
    void setUp() {
        tenantId = "123";
        sortDirection = Sort.Direction.DESC;
        sortByProperty = "property";
        calculatedOffset = 1;
        size = 25;
        criteria = Criteria.where("tenantId").is("dummy");
    }

    @Test
    void getAll_Aggregation_IsProjectionIsFalse_ReturnAggregation() {
        isProjectionAggregation = false;

        ArrayList<AggregationOperation> operations = new ArrayList<>() {{
            add(Aggregation.match(criteria));
            add(Aggregation.sort(Sort.by(sortDirection, sortByProperty)));
            add(Aggregation.sort(Sort.by(sortDirection, sortByProperty)));
            add(Aggregation.skip(calculatedOffset));
            add(Aggregation.limit(size));
            add(TransactionRepositoryCommonStagesBuilder.customerLookupByTransactionCustomerIdAndTenant(tenantId));
            add(Aggregation.unwind("customer", true));
        }};

        expectedAggregation = Aggregation.newAggregation(Transaction.class,
                operations).withOptions(newAggregationOptions().cursorBatchSize(size).build());

        TypedAggregation<Transaction> actualAggregation = transactionTypedAggregationBuilder
                .getAllAggregation(tenantId, criteria, sortDirection, sortByProperty, calculatedOffset, size, isProjectionAggregation);

        assertEquals(expectedAggregation.toString(), actualAggregation.toString());
    }

    @Test
    void getAll_Aggregation_IsProjectionIsTrue_ReturnProjectionAggregation() {
        isProjectionAggregation = true;

        ArrayList<AggregationOperation> operations = new ArrayList<>() {{
            add(Aggregation.match(criteria));
            add(Aggregation.sort(Sort.by(sortDirection, sortByProperty)));
            add(Aggregation.sort(Sort.by(sortDirection, sortByProperty)));
            add(Aggregation.skip(calculatedOffset));
            add(Aggregation.limit(size));
            add(TransactionRepositoryCommonStagesBuilder.customerLookupByTransactionCustomerIdAndTenant(tenantId));
            add(Aggregation.unwind("customer", true));

            // being added only for isProjection = true
            add(Aggregation.addFields().addFieldWithValue("items", TransactionProjectionStage.itemsMapAddFeildStageDocument()).build());
            add(Aggregation.stage(TransactionProjectionStage.projectionStageDocument()));
        }};

        expectedAggregation = Aggregation.newAggregation(Transaction.class,
                operations).withOptions(newAggregationOptions().cursorBatchSize(size).build());

        TypedAggregation<Transaction> actualAggregation = transactionTypedAggregationBuilder
                .getAllAggregation(tenantId, criteria, sortDirection, sortByProperty, calculatedOffset, size, isProjectionAggregation);

        assertEquals(expectedAggregation.toString(), actualAggregation.toString());
    }
}