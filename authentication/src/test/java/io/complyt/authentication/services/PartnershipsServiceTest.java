package io.complyt.authentication.services;

import io.complyt.authentication.domain.Partnership;
import io.complyt.authentication.domain.Referral;
import io.complyt.authentication.domain.enums.PartnershipStatus;
import io.complyt.authentication.repositories.PartnershipRepository;
import io.complyt.authentication.v1.exceptions.types.PartnerNotFoundApiException;
import io.complyt.authentication.v1.exceptions.types.ReferralsNotFoundApiException;
import io.complyt.authentication.v1.exceptions.types.SpecificReferralNotFoundApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.unit_tests.TestUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartnershipsServiceTest {
    @InjectMocks
    PartnershipService partnershipService;
    @Mock
    PartnershipRepository partnershipRepository;

    ArrayList<Referral> referrals;

    Partnership partnership;

    Referral referral;

    String partnerTenantId;
    String referralTenantId;

    @BeforeEach
    void setUp() {
        referrals = new ArrayList<>();
        partnership = TestUtilities.createPartnership();
        referral = TestUtilities.createReferral();
        partnerTenantId = "partnerTenantId";
        referralTenantId = "referralTenantId";
    }

    @Test
    void findPartnership_validPartner_returnPartnership(){
        // When
        when(partnershipRepository.findPartnership()).thenReturn(Mono.just(partnership));

        Mono<Partnership> partnershipMono = partnershipService.findPartnership();

        StepVerifier.create(partnershipMono).expectNext(partnership).verifyComplete();
    }

    @Test
    void findByTenantId_validTenantId_returnException(){
        // When
        when(partnershipRepository.findPartnershipByPartnerTenantId(partnerTenantId)).thenReturn(Mono.just(partnership));

        Mono<Partnership> partnershipMono = partnershipService.findByTenantId(partnerTenantId);

        StepVerifier.create(partnershipMono).expectNext(partnership).verifyComplete();
    }

    @Test
    void findByTenantId_invalidTenantId_returnException(){
        // When
        when(partnershipRepository.findPartnershipByPartnerTenantId(partnerTenantId)).thenReturn(Mono.empty());

        Mono<Partnership> partnershipMono = partnershipService.findByTenantId(partnerTenantId);

        StepVerifier.create(partnershipMono).expectError(ReferralsNotFoundApiException.class).verify();
    }

    @Test
    void findSupportedTenantsForPartnerByTenantId_validTenantIdButEmptyReferralList_returnException(){
        // When
        when(partnershipRepository.findPartnershipByPartnerTenantId(partnerTenantId)).thenReturn(Mono.just(partnership));

        Mono<List<String>> referralsListMono = partnershipService.findSupportedTenantsForPartnerByTenantId(partnerTenantId);

        StepVerifier.create(referralsListMono).expectError(ReferralsNotFoundApiException.class).verify();
    }

    @Test
    void findSupportedTenantsForPartnerByTenantId_validTenantIdButAllReferralsCancelled_returnException(){
        // Given
        Map<String, Referral> expectedReferralList = new HashMap<>();
        expectedReferralList.put(referral.getTenantId(), referral.withPartnershipStatus(PartnershipStatus.CANCELLED));

        Partnership partnershipWithReferrals = partnership.withSupportedReferrals(expectedReferralList);

        // When
        when(partnershipRepository.findPartnershipByPartnerTenantId(partnerTenantId)).thenReturn(Mono.just(partnershipWithReferrals));

        Mono<List<String>> referralsListMono = partnershipService.findSupportedTenantsForPartnerByTenantId(partnerTenantId);

        StepVerifier.create(referralsListMono).expectError(ReferralsNotFoundApiException.class).verify();
    }

    @Test
    void findSupportedTenantsForPartnerByTenantId_validTenantId_returnReferralsList(){
        // Given
        Map<String, Referral> expectedReferralList = new HashMap<>();
        expectedReferralList.put(referral.getTenantId(), referral);

        List<String> expectedReferralTenantIdList = new ArrayList<>();
        expectedReferralTenantIdList.add(referral.getTenantId());

        Partnership partnershipWithReferrals = partnership.withSupportedReferrals(expectedReferralList);

        // When
        when(partnershipRepository.findPartnershipByPartnerTenantId(partnerTenantId)).thenReturn(Mono.just(partnershipWithReferrals));

        Mono<List<String>> referralsListMono = partnershipService.findSupportedTenantsForPartnerByTenantId(partnerTenantId);

        StepVerifier.create(referralsListMono).expectNext(expectedReferralTenantIdList).verifyComplete();
    }

    @Test
    void upsertReferralClient_validNewReferral_returnPartnership(){
        // Given
        Map<String, Referral> expectedReferralList = new HashMap<>();
        expectedReferralList.put(referral.getTenantId(), referral);
        Partnership partnershipWithReferrals = partnership.withSupportedReferrals(expectedReferralList);

        Referral newReferral = TestUtilities.createNewReferralWithTenantId("newReferralTenantId");

        Map<String, Referral> expectedUpdatedReferralList = new HashMap<>();
        expectedUpdatedReferralList.put(referral.getTenantId(), referral);
        expectedUpdatedReferralList.put(newReferral.getTenantId(), newReferral);
        Partnership partnershipWithUpdatedReferrals = partnershipWithReferrals.withSupportedReferrals(expectedUpdatedReferralList);

        // When
        when(partnershipRepository.findPartnership()).thenReturn(Mono.just(partnershipWithReferrals));
        when(partnershipRepository.saveReferral(newReferral)).thenReturn(Mono.just(partnershipWithUpdatedReferrals));

        Mono<Partnership> partnershipMono = partnershipService.upsertReferralClient(newReferral);

        StepVerifier.create(partnershipMono).expectNext(partnershipWithUpdatedReferrals).verifyComplete();
    }

    @Test
    void upsertReferralClient_existingReferral_getsUpdatedAndReturnPartnership(){
        // Given
        Map<String, Referral> expectedReferralList = new HashMap<>();
        expectedReferralList.put(referral.getTenantId(), referral);
        Partnership partnershipWithReferrals = partnership.withSupportedReferrals(expectedReferralList);

        // When
        when(partnershipRepository.findPartnership()).thenReturn(Mono.just(partnershipWithReferrals));
        when(partnershipRepository.updateReferral(referral)).thenReturn(Mono.just(partnershipWithReferrals));

        Mono<Partnership> partnershipMono = partnershipService.upsertReferralClient(referral);

        StepVerifier.create(partnershipMono).expectNext(partnershipWithReferrals).verifyComplete();
    }

    @Test
    void upsertReferralClient_existingReferralWithStatusCancelled_saveNewAndReturnPartnership(){
        // Given
        Map<String, Referral> expectedReferralList = new HashMap<>();
        Referral referralWithStatusCancelled = referral.withPartnershipStatus(PartnershipStatus.CANCELLED);
        expectedReferralList.put(referralWithStatusCancelled.getTenantId(), referralWithStatusCancelled);
        Partnership partnershipWithReferrals = partnership.withSupportedReferrals(expectedReferralList);

        Map<String, Referral> expectedUpdatedReferralList = new HashMap<>();
        expectedUpdatedReferralList.put(referralWithStatusCancelled.getTenantId(), referralWithStatusCancelled);
        expectedUpdatedReferralList.put(referral.getTenantId(), referral);
        Partnership partnershipWithUpdatedReferrals = partnershipWithReferrals.withSupportedReferrals(expectedUpdatedReferralList);

        // When
        when(partnershipRepository.findPartnership()).thenReturn(Mono.just(partnershipWithReferrals));
        when(partnershipRepository.saveReferral(referral)).thenReturn(Mono.just(partnershipWithUpdatedReferrals));

        Mono<Partnership> partnershipMono = partnershipService.upsertReferralClient(referral);

        StepVerifier.create(partnershipMono).expectNext(partnershipWithUpdatedReferrals).verifyComplete();
    }

    @Test
    void upsertReferralClient_NoPartnershipDocumentFound_returnException(){
        // When
        when(partnershipRepository.findPartnership()).thenReturn(Mono.empty());

        Mono<Partnership> partnershipMono = partnershipService.upsertReferralClient(referral);

        StepVerifier.create(partnershipMono).expectError(PartnerNotFoundApiException.class).verify();
    }

    @Test
    void markAsCancelledReferralClient_validReferral_returnPartnership(){
        // Given
        Map<String, Referral> expectedReferralList = new HashMap<>();
        expectedReferralList.put(referral.getTenantId(), referral);
        Partnership partnershipWithReferrals = partnership.withSupportedReferrals(expectedReferralList);

        Referral cancelledReferral = referral.withPartnershipStatus(PartnershipStatus.CANCELLED);
        Map<String, Referral> expectedUpdatedReferralList = new HashMap<>();
        expectedUpdatedReferralList.put(cancelledReferral.getTenantId(), cancelledReferral);
        Partnership partnershipWithUpdatedReferrals = partnershipWithReferrals.withSupportedReferrals(expectedUpdatedReferralList);

        // When
        when(partnershipRepository.findPartnership()).thenReturn(Mono.just(partnershipWithReferrals));
        when(partnershipRepository.updateReferral(referral)).thenReturn(Mono.just(partnershipWithUpdatedReferrals));

        Mono<Partnership> partnershipMono = partnershipService.markAsCancelledReferralClient(referral.getTenantId());

        StepVerifier.create(partnershipMono).expectNext(partnershipWithUpdatedReferrals).verifyComplete();
    }

    @Test
    void markAsCancelledReferralClient_existingReferralAlreadyCancelled_returnException(){
        // Given
        Referral cancelledReferral = referral.withPartnershipStatus(PartnershipStatus.CANCELLED);
        Map<String, Referral> expectedReferralList = new HashMap<>();
        expectedReferralList.put(cancelledReferral.getTenantId(), cancelledReferral);
        Partnership partnershipWithReferrals = partnership.withSupportedReferrals(expectedReferralList);

        // When
        when(partnershipRepository.findPartnership()).thenReturn(Mono.just(partnershipWithReferrals));

        Mono<Partnership> partnershipMono = partnershipService.markAsCancelledReferralClient(referral.getTenantId());

        StepVerifier.create(partnershipMono).expectError(SpecificReferralNotFoundApiException.class).verify();
    }

    @Test
    void markAsCancelledReferralClient_validReferralWithExistingTenants_returnPartnership(){
        // Given
        Referral newReferral = TestUtilities.createNewReferralWithTenantId("newTenantId");
        Map<String, Referral> expectedReferralList = new HashMap<>();
        expectedReferralList.put(referral.getTenantId(), referral);
        expectedReferralList.put(newReferral.getTenantId(), newReferral);
        Partnership partnershipWithReferrals = partnership.withSupportedReferrals(expectedReferralList);

        Referral cancelledReferral = referral.withPartnershipStatus(PartnershipStatus.CANCELLED);
        Map<String, Referral> expectedUpdatedReferralList = new HashMap<>();
        expectedUpdatedReferralList.put(cancelledReferral.getTenantId(), cancelledReferral);
        expectedUpdatedReferralList.put(newReferral.getTenantId(), newReferral);
        Partnership partnershipWithUpdatedReferrals = partnershipWithReferrals.withSupportedReferrals(expectedUpdatedReferralList);

        // When
        when(partnershipRepository.findPartnership()).thenReturn(Mono.just(partnershipWithReferrals));
        when(partnershipRepository.updateReferral(referral)).thenReturn(Mono.just(partnershipWithUpdatedReferrals));

        Mono<Partnership> partnershipMono = partnershipService.markAsCancelledReferralClient(referral.getTenantId());

        StepVerifier.create(partnershipMono).expectNext(partnershipWithUpdatedReferrals).verifyComplete();
    }

    @Test
    void markAsCancelledReferralClient_validReferralButEmptyList_returnException(){
        // When
        when(partnershipRepository.findPartnership()).thenReturn(Mono.just(partnership));

        Mono<Partnership> partnershipMono = partnershipService.markAsCancelledReferralClient(referral.getTenantId());

        StepVerifier.create(partnershipMono).expectError(SpecificReferralNotFoundApiException.class).verify();
    }

    @Test
    void markAsCancelledReferralClient_NoPartnershipDocumentFound_returnException(){
        // When
        when(partnershipRepository.findPartnership()).thenReturn(Mono.empty());

        Mono<Partnership> partnershipMono = partnershipService.markAsCancelledReferralClient(referral.getTenantId());

        StepVerifier.create(partnershipMono).expectError(PartnerNotFoundApiException.class).verify();
    }

    @Test
    void findByTenantId_tenantIdIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            partnershipService.findByTenantId(null);
        });

        assertEquals(nullPointerException.getMessage(), "tenantId is marked non-null but is null");
    }

    @Test
    void findSupportedTenantsForPartnerByTenantId_tenantIdIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            partnershipService.findSupportedTenantsForPartnerByTenantId(null);
        });

        assertEquals(nullPointerException.getMessage(), "tenantId is marked non-null but is null");
    }

}