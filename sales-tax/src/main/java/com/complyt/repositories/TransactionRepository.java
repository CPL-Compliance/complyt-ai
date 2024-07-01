package com.complyt.repositories;

import com.complyt.domain.transaction.Transaction;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregationOptions;

@Repository
@Slf4j
@AllArgsConstructor
public class TransactionRepository {
    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    private TenantResolver tenantResolver;


    public Mono<Transaction> save(@NonNull Transaction transaction) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Transaction transactionWithTenantId = transaction.setTenantId(tenantId);

                    return ContextLogger.observeCtx("Saving transaction: " + transactionWithTenantId.toString(), log::info)
                            .then(reactiveMongoTemplate.save(transactionWithTenantId));
                });
    }

    public Flux<Transaction> saveAll(@NonNull List<Transaction> transactions) {
        return tenantResolver.resolve()
                .map(tenantId -> transactions.stream().map(transaction -> transaction.setTenantId(tenantId)).collect(Collectors.toList()))
                .flatMapMany(transactionsWithTenantId -> ContextLogger.observeCtx("Saving transactions: " + transactionsWithTenantId.toString(), log::info)
                        .thenMany(reactiveMongoTemplate.insertAll(transactionsWithTenantId)));
    }

    @Deprecated
    public Mono<Transaction> findById(@NonNull String transactionId) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("_id").is(transactionId)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for transaction with ID " + transactionId + " and tenant ID " + tenantId, log::info)
                            .then(reactiveMongoTemplate
                                    .findOne(query, Transaction.class));
                });
    }

    public Mono<Transaction> findByExternalIdAndSource(String externalId, String source) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("externalId").is(externalId)
                            .and("source").is(source)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for transaction with external ID " + externalId + ", source" + source + ", and tenant ID " + tenantId, log::info)
                            .then(reactiveMongoTemplate
                                    .findOne(query, Transaction.class));
                });
    }

    public Mono<Transaction> findByComplytId(UUID complytId) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("complytId").is(complytId)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for transaction with complyt ID " + complytId + " and tenant ID " + tenantId, log::info)
                            .then(reactiveMongoTemplate
                                    .findOne(query, Transaction.class));
                });
    }

    public Flux<Transaction> findAll(int page, int size) {
        int calculatedOffset = (page - 1) * size;

        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    TypedAggregation<Transaction> aggregation = Aggregation.newAggregation(Transaction.class,
                                    Aggregation.match(Criteria.where("tenantId").is(tenantId)),
                                    Aggregation.sort(Sort.by(Sort.Direction.DESC, "externalTimestamps.createdDate")),
                                    Aggregation.skip(calculatedOffset),
                                    Aggregation.limit(size),
                                    Aggregation.lookup()
                                            .from("customer")
                                            .localField("customerId")
                                            .foreignField("complytId")
                                            .pipeline(Aggregation.match(Criteria.where("tenantId").is(tenantId)))
                                            .as("customer"),
                                    Aggregation.unwind("customer", true))
                            .withOptions(newAggregationOptions().cursorBatchSize(size).build());

                    return ContextLogger.observeCtx("Searching for transactions by tenant ID " + tenantId + " with page " + page + " and size " + size, log::info)
                            .thenMany(reactiveMongoTemplate.aggregate(aggregation, Transaction.class));
                });
    }


    public Flux<Transaction> findAllBySource(String source) {
        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId)
                            .and("source").is(source));

                    return ContextLogger.observeCtx("Searching for transactions by tenant ID " + tenantId + " and source " + source, log::info)
                            .thenMany(reactiveMongoTemplate.find(query, Transaction.class));
                });
    }

    public Flux<Transaction> findAllByQuery(Query query) {
        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query updatedQuery = query.addCriteria(Criteria.where("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for transactions by query: " + updatedQuery, log::info)
                            .thenMany(reactiveMongoTemplate.find(updatedQuery, Transaction.class));
                });
    }
}