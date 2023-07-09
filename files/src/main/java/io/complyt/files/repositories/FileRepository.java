package io.complyt.files.repositories;

import io.complyt.files.domain.File;
import io.complyt.files.security.TenantResolver;
import io.complyt.files.utils.observability.ContextLogger;
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

    public Mono<File> find() {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for files with tenant ID: " + tenantId, log::info)
                            .then(reactiveMongoTemplate.findOne(query, File.class));
                });
    }
}