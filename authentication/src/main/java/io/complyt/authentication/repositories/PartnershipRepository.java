package io.complyt.authentication.repositories;

import io.complyt.authentication.domain.Partnership;
import io.complyt.authentication.domain.Referral;
import io.complyt.authentication.domain.enums.PartnershipStatus;
import io.complyt.authentication.security.TenantResolver;
import io.complyt.authentication.utils.observability.ContextLogger;
import io.complyt.authentication.v1.exceptions.types.ObjectNotFoundApiException;
import io.complyt.authentication.v1.exceptions.types.PartnerNotFoundApiException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
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
public class PartnershipRepository {
    @NonNull
    ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<Partnership> findPartnership() {
        return TenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId));

                    return ContextLogger.observeCtx("Searching for partnership by tenantId " + tenantId, log::info)
                            .then(reactiveMongoTemplate.findOne(query, Partnership.class));
                });
    }

    public Mono<Partnership> findPartnershipByPartnerTenantId(final @NonNull String tenantId) {
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));

        return ContextLogger.observeCtx("Searching for partnership by tenantId " + tenantId, log::info)
                .then(reactiveMongoTemplate.findOne(query, Partnership.class));
    }

    public Mono<Partnership> saveReferral(final @NonNull Referral referral) {
        return TenantResolver.resolve()
                .flatMap(tenantId -> {
                    Query query = Query.query(Criteria.where("tenantId").is(tenantId));
                    String dynamicMapKey = "supportedReferrals." + referral.getTenantId();
                    Update update = new Update().set(dynamicMapKey, referral);

                    return ContextLogger.observeCtx("Adding referral to Partnership with tenantId: " + tenantId, log::info)
                            .then(reactiveMongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), Partnership.class));
                });
    }

    public Mono<Partnership> updateReferral(final @NonNull Referral referral) {
        return TenantResolver.resolve()
                .flatMap(tenantId -> {
                    String referralTenantId = referral.getTenantId();

                    Query query = Query.query(Criteria.where("tenantId").is(tenantId)
                            .and("supportedReferrals." + referralTenantId + ".partnershipStatus").is(PartnershipStatus.ACTIVE));

                    Update update = new Update()
                            .set("supportedReferrals." + referralTenantId + ".name", referral.getName())
                            .set("supportedReferrals." + referralTenantId + ".partnershipStatus", referral.getPartnershipStatus())
                            .set("supportedReferrals." + referralTenantId + ".timestamps", referral.getTimestamps());

                    return reactiveMongoTemplate.findAndModify(query, update,
                                    FindAndModifyOptions.options().returnNew(true), Partnership.class)
                                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()))
                            .flatMap(updatedPartnership ->
                                    ContextLogger.observeCtx("Updated referral for Partnership with tenantId: " + tenantId, log::info)
                                            .thenReturn(updatedPartnership)
                            );
                });
    }

}
