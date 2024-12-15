package com.complyt.repositories;

import com.complyt.domain.customer.Customer;
import com.complyt.repositories.pagination.CriteriaBuilder;
import com.complyt.repositories.pagination.customer.CustomerPaginationUtil;
import com.complyt.security.TenantResolver;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
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
public class CustomerRepository {

    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    private TenantResolver tenantResolver;

    public Flux<Customer> findByName(@NonNull String name) {
        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query query = Query.query(Criteria.where("name").regex("^" + name, "i")
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for customers with name " + name + " and tenant ID " + tenantId, tenantId, log::info)
                            .thenMany(reactiveMongoTemplate.find(query, Customer.class));
                });
    }

    public Mono<Customer> findOneByName(@NonNull String name) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("name").is("^" + name)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for a customer with name " + name + " and tenant ID " + tenantId, tenantId, log::info)
                            .then(reactiveMongoTemplate.findOne(query, Customer.class));
                });
    }

    public Flux<Customer> findAll(int page, int size, Map<String, String> filterMap, String sortOrder, String sortBy) {
        int calculatedOffset = (page - 1) * size;
        Criteria criteriaFromFilterMap = CriteriaBuilder.build(filterMap, CustomerPaginationUtil.customerFilterKeys);
        String sortByProperty = CustomerPaginationUtil.customerSortByFields.contains(sortBy) ? sortBy : CustomerPaginationUtil.DEFAULT_SORT_BY;
        Sort.Direction sortDirection = Sort.Direction.fromString(sortOrder);

        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Criteria criteria = criteriaFromFilterMap != null ? Criteria.where("tenantId").is(tenantId).andOperator(criteriaFromFilterMap) : Criteria.where("tenantId").is(tenantId);

                    Query query = Query.query(criteria)
                            .skip(calculatedOffset).limit(size)
                            .with(Sort.by(sortDirection, sortByProperty));

                    return ContextLogger.observeCtx("Searching for customers with tenant ID " + tenantId + " with page " + page + " and size " + size, tenantId, log::info)
                            .thenMany(reactiveMongoTemplate.find(query, Customer.class));
                });
    }

    public Flux<Customer> findAllBySource(String source) {
        return tenantResolver.resolve()
                .flatMapMany(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId)
                            .and("source").is(source));

                    return ContextLogger.observeCtx("Searching for customers with source " + source + " and tenant ID " + tenantId, tenantId, log::info)
                            .thenMany(reactiveMongoTemplate.find(query, Customer.class));
                });
    }

    public Mono<Customer> save(@NonNull Customer customer) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Customer customerWithTenantId = customer.withTenantId(tenantId);
                    return ContextLogger.observeCtx("Saving Customer " + customerWithTenantId.toString(), tenantId, log::info)
                            .then(reactiveMongoTemplate.save(customerWithTenantId));
                });
    }

    public Mono<Customer> findById(String id) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("_id").is(id).and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for a customer with id of: " + id + " and tenant ID " + tenantId, tenantId, log::info)
                            .then(reactiveMongoTemplate.findOne(query, Customer.class));
                });
    }

    public Mono<Customer> findByExternalIdAndSource(String externalId, String source) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("externalId").is(externalId)
                            .and("source").is(source)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for a customer with externalId "
                                    + externalId + ", source " + source + ", and tenant ID: " + tenantId, tenantId, log::info)
                            .then(reactiveMongoTemplate.findOne(query, Customer.class));
                });
    }

    public Mono<Customer> findByComplytId(UUID complytId) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("complytId").is(complytId)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for a customer with complytId "
                                    + complytId + " and tenant ID " + tenantId, tenantId, log::info)
                            .then(reactiveMongoTemplate.findOne(query, Customer.class));
                });
    }

    public Mono<Customer> findById(ObjectId id) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("_id").is(id)
                            .and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for a customer with ID "
                                    + id.toString() + " and tenant ID " + tenantId, tenantId, log::info)
                            .then(reactiveMongoTemplate.findOne(query, Customer.class));
                });
    }
}