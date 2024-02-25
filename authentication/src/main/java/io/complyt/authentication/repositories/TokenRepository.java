package io.complyt.authentication.repositories;

import io.complyt.authentication.domain.Token;
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
public class TokenRepository {
    @NonNull
    ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<Token> findByComplytClientId(final @NonNull String complytClientId) {
        Query query = Query.query(Criteria.where("complytClientId").is(complytClientId));

        return ContextLogger.observeCtx("Searching for token by ComplytClientId " + complytClientId, log::info)
                .then(reactiveMongoTemplate.findOne(query, Token.class));
    }

    public Mono<Token> save(final @NonNull Token token) {
        return ContextLogger.observeCtx("Saving token: " + token, log::info)
                .then(reactiveMongoTemplate.save(token));
    }

    public Mono<Token> deleteByComplytClientId(final @NonNull String complytClientId) {
        Query query = Query.query(Criteria.where("complytClientId").is(complytClientId));

        return ContextLogger.observeCtx("Deleting token by ComplytClientId " + complytClientId, log::info)
                .then(reactiveMongoTemplate.findAndRemove(query, Token.class));
    }
}