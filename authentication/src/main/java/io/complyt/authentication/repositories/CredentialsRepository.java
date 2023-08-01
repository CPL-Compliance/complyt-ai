package io.complyt.authentication.repositories;

import io.complyt.authentication.domain.Credentials;
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
public class CredentialsRepository {
    @NonNull
    ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<Credentials> findByComplytClientId(final @NonNull String complytClientId) {
        Query query = Query.query(Criteria.where("complytClientId").is(complytClientId));

        return ContextLogger.observeCtx("Searching for credentials by complytClientId " + complytClientId, log::info)
                .then(reactiveMongoTemplate.findOne(query, Credentials.class));
    }

    public Mono<Credentials> save(Credentials credentials) {
        return ContextLogger.observeCtx("Saving Credentials", log::info)
                .then(reactiveMongoTemplate.save(credentials));
    }
}
