package io.complyt.authentication.repositories;

import io.complyt.authentication.domain.Partnership;
import io.complyt.authentication.domain.Referral;
import io.complyt.authentication.domain.enums.PartnershipStatus;
import io.complyt.authentication.security.TenantResolver;
import io.complyt.authentication.v1.exceptions.types.ObjectNotFoundApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.unit_tests.TestUtilities;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PartnershipRepositoryTest {

    @InjectMocks
    PartnershipRepository partnershipRepository;
    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;
    @Mock
    TenantResolver tenantResolver;

    String partnerTenantId;
    String referralTenant;
    Referral referral;
    Partnership partnership;

    @BeforeEach
    void setup() {
        partnerTenantId = "partnerTenantId";
        referralTenant = "referralTenant";
        referral = TestUtilities.createReferral();
        partnership = TestUtilities.createPartnership();
    }

    @Test
    void findPartnership_partnerTenantId_returnsPartnershipDocument() {
        // Given
        Query query = Query.query(Criteria.where("tenantId").is(partnerTenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(partnerTenantId));
        when(reactiveMongoTemplate.findOne(query, Partnership.class)).thenReturn(Mono.just(partnership));

        // Then
        Mono<Partnership> partnershipMono = partnershipRepository.findPartnership();

        StepVerifier.create(partnershipMono).expectNext(partnership).verifyComplete();
    }

    @Test
    void findPartnership_partnerTenantIdDoesNotExists_returnsMonoEmpty() {
        // Given
        Query query = Query.query(Criteria.where("tenantId").is(partnerTenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(partnerTenantId));
        when(reactiveMongoTemplate.findOne(query, Partnership.class)).thenReturn(Mono.empty());

        // Then
        Mono<Partnership> partnershipMono = partnershipRepository.findPartnership();

        StepVerifier.create(partnershipMono).verifyComplete();
    }

    @Test
    void findPartnershipByPartnerTenantId_partnerTenantId_returnsPartnershipDocument() {
        // Given
        Query query = Query.query(Criteria.where("tenantId").is(partnerTenantId));

        // When
        when(reactiveMongoTemplate.findOne(query, Partnership.class)).thenReturn(Mono.just(partnership));

        // Then
        Mono<Partnership> partnershipMono = partnershipRepository.findPartnershipByPartnerTenantId(partnerTenantId);

        StepVerifier.create(partnershipMono).expectNext(partnership).verifyComplete();
    }

    @Test
    void findPartnershipByPartnerTenantId_partnerTenantIdDoesNotExists_returnsMonoEmpty() {
        // Given
        Query query = Query.query(Criteria.where("tenantId").is(partnerTenantId));

        // When
        when(reactiveMongoTemplate.findOne(query, Partnership.class)).thenReturn(Mono.empty());

        // Then
        Mono<Partnership> partnershipMono = partnershipRepository.findPartnershipByPartnerTenantId(partnerTenantId);

        StepVerifier.create(partnershipMono).verifyComplete();
    }

    @Test
    void saveReferral_validReferral_returnPartnershipDocument() {
        // Given
        Query query = Query.query(Criteria.where("tenantId").is(partnerTenantId));
        Update update = new Update().push("supportedReferrals", referral);

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(partnerTenantId));
        when(reactiveMongoTemplate.findAndModify(eq(query), eq(update), any(FindAndModifyOptions.class), eq(Partnership.class)))
                .thenReturn(Mono.just(partnership));

        // Then
        Mono<Partnership> partnershipMono = partnershipRepository.saveReferral(referral);

        StepVerifier.create(partnershipMono)
                .expectNext(partnership) // Ensure returned partnership is correct
                .verifyComplete();
    }

    @Test
    void updateReferral_ValidReferral_ReturnsUpdatedPartnership() {
        // Given
        Query expectedQuery = Query.query(Criteria.where("tenantId").is(partnerTenantId)
                .and("supportedReferrals").elemMatch(
                        Criteria.where("tenantId").is(referral.getTenantId())
                                .and("partnershipStatus").is(PartnershipStatus.ACTIVE)
                ));

        Update expectedUpdate = new Update()
                .set("supportedReferrals.$.name", referral.getName())
                .set("supportedReferrals.$.partnershipStatus", referral.getPartnershipStatus())
                .set("supportedReferrals.$.timestamps", referral.getTimestamps());

        List<Referral> referrals = new ArrayList<>();
        referrals.add(referral);
        Partnership expectedPartnership = partnership.withSupportedReferrals(referrals);

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(partnerTenantId));
        when(reactiveMongoTemplate.findAndModify(eq(expectedQuery), eq(expectedUpdate), any(FindAndModifyOptions.class), eq(Partnership.class)))
                .thenReturn(Mono.just(expectedPartnership));

        // Then
        Mono<Partnership> partnershipMono = partnershipRepository.updateReferral(referral);

        StepVerifier.create(partnershipMono)
                .expectNext(expectedPartnership)
                .verifyComplete();
    }

    @Test
    void updateReferral_NoMatchingReferral_ThrowsObjectNotFoundException() {
        // Given
        Query expectedQuery = Query.query(Criteria.where("tenantId").is(partnerTenantId)
                .and("supportedReferrals").elemMatch(
                        Criteria.where("tenantId").is(referral.getTenantId())
                                .and("partnershipStatus").is(PartnershipStatus.ACTIVE)
                ));

        Update expectedUpdate = new Update()
                .set("supportedReferrals.$.name", referral.getName())
                .set("supportedReferrals.$.partnershipStatus", referral.getPartnershipStatus())
                .set("supportedReferrals.$.timestamps", referral.getTimestamps());

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(partnerTenantId));
        when(reactiveMongoTemplate.findAndModify(eq(expectedQuery), eq(expectedUpdate), any(FindAndModifyOptions.class), eq(Partnership.class)))
                .thenReturn(Mono.empty()); // No partnership found

        // Then
        Mono<Partnership> result = partnershipRepository.updateReferral(referral);

        StepVerifier.create(result)
                .expectError(ObjectNotFoundApiException.class)
                .verify();
    }

    @Test
    void updateReferral_TenantResolverFails_ReturnsError() {
        // When
        when(tenantResolver.resolve()).thenReturn(Mono.error(new RuntimeException("Tenant resolution failed")));

        // Then
        Mono<Partnership> result = partnershipRepository.updateReferral(referral);

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof RuntimeException &&
                        error.getMessage().equals("Tenant resolution failed"))
                .verify();
    }

    @Test
    void findPartnershipByPartnerTenantId_tenantIdIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            partnershipRepository.findPartnershipByPartnerTenantId(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "tenantId is marked non-null but is null");
    }

    @Test
    void saveReferral_referralIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            partnershipRepository.saveReferral(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "referral is marked non-null but is null");
    }

    @Test
    void updateReferral_referralIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            partnershipRepository.updateReferral(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "referral is marked non-null but is null");
    }
}