package io.complyt.filing.repositories;

import io.complyt.filing.domain.Link;
import io.complyt.filing.security.TenantResolver;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Repository
public class LinkRepository {
    @NonNull
    ReactiveMongoTemplate reactiveMongoTemplate;

    @NonNull
    TenantResolver tenantResolver;

    public Mono<Link> find() {
        return tenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId));
                    log.debug("Executing find Link");

                    return reactiveMongoTemplate.findOne(query, Link.class).log();
                });
    }
}
