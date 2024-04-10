package io.complyt.authentication.repositories;

import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.domain.enums.ApiKeyStatus;
import io.complyt.authentication.utils.observability.ContextLogger;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
        // Criteria to filter out documents with the status CANCELLED
        Criteria statusCriteria = Criteria.where("status").in(ApiKeyStatus.ACTIVE, ApiKeyStatus.ROTATED);
        Criteria clientIdCriteria = Criteria.where("complytClientId").is(complytClientId);
        Query query = Query.query(new Criteria().andOperator(clientIdCriteria, statusCriteria));

        return ContextLogger.observeCtx("Searching for credentials by complytClientId " + complytClientId, log::info)
                .then(reactiveMongoTemplate.findOne(query, Credentials.class));
    }

    public Mono<Credentials> findActiveCredentialsByComplytClientId(final @NonNull String complytClientId) {
        Criteria statusCriteria = Criteria.where("status").is(ApiKeyStatus.ACTIVE);
        Criteria clientIdCriteria = Criteria.where("complytClientId").is(complytClientId);
        Query query = Query.query(new Criteria().andOperator(clientIdCriteria, statusCriteria));

        return ContextLogger.observeCtx("Searching for credentials by complytClientId " + complytClientId, log::info)
                .then(reactiveMongoTemplate.findOne(query, Credentials.class));
    }

    public Mono<Credentials> save(final @NonNull Credentials credentials) {
        return ContextLogger.observeCtx("Saving Credentials", log::info)
                .then(reactiveMongoTemplate.save(credentials));
    }

    public Mono<Credentials> markAsCancelled(final @NonNull String complytClientId) {
        Query query = Query.query(Criteria.where("complytClientId").is(complytClientId));
        Update update = Update.update("status", ApiKeyStatus.CANCELLED);


        return ContextLogger.observeCtx("Updating credentials status to cancelled for complytClientId " + complytClientId, log::info)
                .then(reactiveMongoTemplate.findAndModify(query, update, Credentials.class));
    }

}
