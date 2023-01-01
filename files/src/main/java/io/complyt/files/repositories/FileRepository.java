package io.complyt.files.repositories;

import io.complyt.files.domain.File;
import io.complyt.files.security.TenantResolver;
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
                    log.debug("Executing find Link");

                    return reactiveMongoTemplate.findOne(query, File.class).log();
                });
    }
}
