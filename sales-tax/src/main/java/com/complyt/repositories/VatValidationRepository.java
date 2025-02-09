package com.complyt.repositories;

import com.complyt.domain.ValidatedVat;
import com.complyt.domain.VatDetailsToValidate;
import com.complyt.utils.observability.ContextLogger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Repository
@Slf4j
public class VatValidationRepository {
    @NonNull
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<ValidatedVat> find(@NonNull VatDetailsToValidate vatDetails) {
        Query query = Query.query(Criteria.where("countryCode").is(vatDetails.getCountryCode())
                .and("vatNumber").is(vatDetails.getVatNumber()));

        return ContextLogger.observeCtx("Searching validated vat: " + vatDetails.getCountryCode() +", " + vatDetails.getVatNumber(), log::info)
                .then(reactiveMongoTemplate.findOne(query, ValidatedVat.class));
    }

    public Mono<ValidatedVat> save(@NonNull ValidatedVat validatedVat) {
        return reactiveMongoTemplate.save(validatedVat);
    }
}