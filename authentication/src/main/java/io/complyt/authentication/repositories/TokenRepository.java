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

    public Mono<Token> findByApiKey(final @NonNull String encodedApiKey) {
        Query query = Query.query(Criteria.where("apiKey").is(encodedApiKey));

        return ContextLogger.observeCtx("Searching for a token by encodedApiKey " + encodedApiKey, log::info)
                .then(reactiveMongoTemplate.findOne(query, Token.class));
    }
}