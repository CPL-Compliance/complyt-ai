package com.complyt.repositories;

import com.complyt.business.pagination.PaginationConstants;
import com.complyt.domain.transaction.Transaction;
import com.complyt.repositories.pagination.CriteriaBuilder;
import com.complyt.repositories.pagination.transaction.TransactionPaginationUtil;
import com.complyt.repositories.typedAggregations.TypedAggregationBuilder;
import com.complyt.security.TenantResolver;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;


@Repository
@Slf4j
@AllArgsConstructor
public class TransactionRepository {
    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    private TypedAggregationBuilder<Transaction> transactionTypedAggregationBuilder;


    public Mono<Transaction> save(@NonNull Transaction transaction) {
        return TenantResolver.resolve()
                .flatMap(tenantId -> {
                    Transaction transactionWithTenantId = transaction.setTenantId(tenantId);

                    return ContextLogger.observeCtx("Saving transaction: " + transactionWithTenantId.toString(), tenantId, log::info)
                            .then(reactiveMongoTemplate.save(transactionWithTenantId));
                });
    }

    @Deprecated
    public Mono<Transaction> findById(@NonNull String transactionId) {
        return TenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("_id").is(transactionId)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for transaction with ID " + transactionId + " and tenant ID " + tenantId, tenantId, log::info)
                            .then(reactiveMongoTemplate
                                    .findOne(query, Transaction.class));
                });
    }

    public Mono<Transaction> findByExternalIdAndSource(String externalId, String source) {
        return TenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(TransactionRepositoryCommonStagesBuilder
                            .tenantIdExternalIdAndSourceExactCriteria(tenantId, externalId, source));

                    return ContextLogger.observeCtx("Searching for transaction with external ID " + externalId + ", source" + source + ", and tenant ID " + tenantId, tenantId, log::info)
                            .then(reactiveMongoTemplate
                                    .findOne(query, Transaction.class));
                });
    }

    public Mono<Transaction> findByExternalIdAndSourceProjection(String externalId, String source) {
        return TenantResolver.resolve()
                .flatMap(tenantId -> {
                    TypedAggregation<Transaction> aggregation = Aggregation.newAggregation(Transaction.class,
                            Aggregation.match(TransactionRepositoryCommonStagesBuilder
                                    .tenantIdExternalIdAndSourceExactCriteria(tenantId, externalId, source)),
                            Aggregation.lookup()
                                    .from("customer")
                                    .localField("customerId")
                                    .foreignField("complytId")
                                    .pipeline(Aggregation.match(Criteria.where("tenantId").is(tenantId)))
                                    .as("customer"),
                            Aggregation.unwind("customer", true),
                            Aggregation.addFields().addFieldWithValue("items", TransactionProjectionStage.itemsMapAddFeildStageDocument()).build(),
                            Aggregation.stage(TransactionProjectionStage.projectionStageDocument()));

                    return ContextLogger.observeCtx("Searching for transaction with external ID " + externalId + ", source" + source + ", and tenant ID " + tenantId, log::info)
                            .then(reactiveMongoTemplate.aggregate(aggregation, Transaction.class).next());
                });
    }

    public Mono<Transaction> findByComplytId(UUID complytId) {
        return TenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("complytId").is(complytId)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for transaction with complyt ID " + complytId + " and tenant ID " + tenantId, tenantId, log::info)
                            .then(reactiveMongoTemplate
                                    .findOne(query, Transaction.class));
                });
    }

    public Flux<Transaction> findAll(int page, int size, Map<String, String> filterMap, String sortOrder, String sortBy) {
        int calculatedOffset = (page - 1) * size;
        Criteria criteriaFromFilterMap = CriteriaBuilder.build(filterMap, TransactionPaginationUtil.transactionFilterMap);
        String sortByProperty = TransactionPaginationUtil.transactionSortByFields.contains(sortBy) ? sortBy : PaginationConstants.DEFAULT_TRANSACTION_SORT_BY;
        Sort.Direction sortDirection = Sort.Direction.fromString(sortOrder);

        return TenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Criteria criteria = criteriaFromFilterMap != null ?
                            Criteria.where("tenantId").is(tenantId).andOperator(criteriaFromFilterMap) :
                            Criteria.where("tenantId").is(tenantId);
                    TypedAggregation<Transaction> aggregation = transactionTypedAggregationBuilder.getAllAggregation(tenantId, criteria, sortDirection, sortByProperty, calculatedOffset, size, false);

                    return ContextLogger.observeCtx("Searching for Transactions by criteria " + criteria.getCriteriaObject() + ", sorting by " + sortByProperty + ", ordered " + sortDirection + ", with page " + page + " and size " + size, tenantId, log::info)
                            .thenMany(reactiveMongoTemplate.aggregate(aggregation, Transaction.class));
                });
    }

    public Flux<Transaction> findAllProjection(int page, int size, Map<String, String> filterMap, String sortOrder, String sortBy) {
        int calculatedOffset = (page - 1) * size;
        Criteria criteriaFromFilterMap = CriteriaBuilder.build(filterMap, TransactionPaginationUtil.transactionFilterMap);
        String sortByProperty = TransactionPaginationUtil.transactionSortByFields.contains(sortBy) ? sortBy : PaginationConstants.DEFAULT_TRANSACTION_SORT_BY;
        Sort.Direction sortDirection = Sort.Direction.fromString(sortOrder);

        return TenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Criteria criteria = criteriaFromFilterMap != null ?
                            Criteria.where("tenantId").is(tenantId).andOperator(criteriaFromFilterMap) :
                            Criteria.where("tenantId").is(tenantId);
                    TypedAggregation<Transaction> aggregation = transactionTypedAggregationBuilder.getAllAggregation(tenantId, criteria, sortDirection, sortByProperty, calculatedOffset, size, true);

                    return ContextLogger.observeCtx("Searching for Transactions by criteria " + criteria.getCriteriaObject() + ", sorting by " + sortByProperty + ", ordered " + sortDirection + ", with page " + page + " and size " + size, tenantId, log::info)
                            .thenMany(reactiveMongoTemplate.aggregate(aggregation, Transaction.class));
                });
    }

    public Flux<Transaction> findAllBySource(String source) {
        return TenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId)
                            .and("source").is(source));

                    return ContextLogger.observeCtx("Searching for transactions by tenant ID " + tenantId + " and source " + source, tenantId, log::info)
                            .thenMany(reactiveMongoTemplate.find(query, Transaction.class));
                });
    }

    public Flux<Transaction> findAllByQuery(Query query) {
        return TenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query updatedQuery = query.addCriteria(Criteria.where("tenantId").is(tenantId))
                            .with(Sort.by(Sort.Direction.ASC, "externalTimestamps.createdDate"));

                    return ContextLogger.observeCtx("Searching for transactions by query: " + updatedQuery, tenantId, log::info)
                            .thenMany(reactiveMongoTemplate.find(updatedQuery, Transaction.class));
                });
    }
}