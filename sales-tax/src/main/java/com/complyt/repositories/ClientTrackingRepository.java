package com.complyt.repositories;

import com.complyt.domain.ClientTracking;
import com.complyt.security.TenantResolver;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@Slf4j
@AllArgsConstructor
@Repository
public class ClientTrackingRepository {

    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    private TenantResolver tenantResolver;

    public Mono<ClientTracking> findClient() {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for client tracking with tenant ID " + tenantId, log::info)
                            .then(reactiveMongoTemplate.findOne(query, ClientTracking.class));
                });
    }

    public Mono<ClientTracking> findById(@NonNull String id) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("_id").is(id).and("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for client tracking with ID " + id + " and tenant ID " + tenantId, log::info)
                            .then(reactiveMongoTemplate.findOne(query, ClientTracking.class));
                });
    }

    public Mono<ClientTracking> save(@NonNull ClientTracking clientTracking) {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    ClientTracking clientTrackingWithTenantId = clientTracking.withTenantId(tenantId);

                    return ContextLogger.observeCtx("Saving client tracking " + clientTrackingWithTenantId.toString(), log::info)
                            .then(reactiveMongoTemplate.save(clientTrackingWithTenantId));
                });
    }

    public Mono<ClientTracking> saveByTenantId(@NonNull ClientTracking clientTracking, @NonNull String tenantId) {
        return ContextLogger.observeCtx("Saving client tracking " + clientTracking.toString(), log::info)
                .then(reactiveMongoTemplate.save(clientTracking));
    }

    public Flux<ClientTracking> findByName(@NonNull String name) {
        Query query = new Query(Criteria.where("name").regex("^" + Pattern.quote(name) + "$", "i"));

        return ContextLogger.observeCtx("Searching for client tracking by name: " + name, log::info)
                .thenMany(reactiveMongoTemplate.find(query, ClientTracking.class));
    }

    public Flux<ClientTracking> findAll(int page, int size) {
        int calculatedOffset = (page - 1) * size;
        Query query = new Query()
                .limit(size)
                .skip(calculatedOffset);
        return ContextLogger.observeCtx("Searching for all client tracking", log::info)
                .thenMany(reactiveMongoTemplate.find(query, ClientTracking.class));
    }

    public Mono<ClientTracking> findByTenantId(@NonNull String tenantId) {
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));

        return ContextLogger.observeCtx("Searching for client tracking by tenant ID: " + tenantId, log::info)
                .then(reactiveMongoTemplate.findOne(query, ClientTracking.class));
    }


}