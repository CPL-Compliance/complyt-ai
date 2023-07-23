package io.complyt.authentication.repositories;

import io.complyt.authentication.security.TenantResolver;
import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.utils.observability.ContextLogger;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileRepository {
    @NonNull
    ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    TenantResolver tenantResolver;

    public Mono<ApiKey> find() {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for files with tenant ID " + tenantId, log::info)
                            .then(reactiveMongoTemplate.findOne(query, ApiKey.class));
                });
    }
}