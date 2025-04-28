package io.complyt.authentication.facades;

import io.complyt.authentication.domain.Partnership;
import io.complyt.authentication.domain.Referral;
import io.complyt.authentication.domain.enums.PartnershipStatus;
import io.complyt.authentication.services.PartnershipService;
import io.complyt.authentication.v1.exceptions.types.PartnerNotFoundApiException;
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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartnershipFacadeTest {
    @InjectMocks
    PartnershipFacade partnershipFacade;

    @Mock
    PartnershipService partnershipService;

    Partnership partnership;
    Referral referral;
    String referralTenantId;

    @BeforeEach
    void setup() {
        partnership = TestUtilities.createPartnership();
        referral = TestUtilities.createReferral();
        referralTenantId = TestUtilities.createTenantId();
    }

    @Test
    void getPartnership_validRequest_returnPartnership() {
        // When
        when(partnershipService.findPartnership()).thenReturn(Mono.just(partnership));

        Mono<Partnership> partnershipMono = partnershipFacade.getPartnership();

        StepVerifier.create(partnershipMono).expectNext(partnership).verifyComplete();
    }

    @Test
        void getPartnership_noPartnershipFound_returnException() {
        // When
        when(partnershipService.findPartnership()).thenReturn(Mono.empty());

        Mono<Partnership> partnershipMono = partnershipFacade.getPartnership();

        StepVerifier.create(partnershipMono).expectError(PartnerNotFoundApiException.class).verify();
    }

    @Test
    void upsertReferralClient_validRequest_returnPartnership() {
        // Given
        Map<String, Referral> referrals = new HashMap<>();
        referrals.put(referral.getTenantId(), referral);
        Partnership updatedPartnership = partnership.withSupportedReferrals(referrals);

        // When
        when(partnershipService.upsertReferralClient(referral)).thenReturn(Mono.just(updatedPartnership));

        Mono<Partnership> partnershipMono = partnershipFacade.upsertReferralClient(referral);

        StepVerifier.create(partnershipMono).expectNext(updatedPartnership).verifyComplete();
    }

    @Test
    void upsertReferralClient_noPartnershipFound_returnException() {
        // When
        when(partnershipService.upsertReferralClient(referral)).thenReturn(Mono.empty());

        Mono<Partnership> partnershipMono = partnershipFacade.upsertReferralClient(referral);

        StepVerifier.create(partnershipMono).expectError(PartnerNotFoundApiException.class).verify();
    }

    @Test
    void markReferralAsCancelled_validRequest_returnPartnership() {
        // Given
        Map<String, Referral> referrals = new HashMap<>();
        referrals.put(referral.getTenantId(), referral.withPartnershipStatus(PartnershipStatus.CANCELLED));
        Partnership updatedPartnership = partnership.withSupportedReferrals(referrals);

        // When
        when(partnershipService.markAsCancelledReferralClient(referral.getTenantId())).thenReturn(Mono.just(updatedPartnership));

        Mono<Partnership> partnershipMono = partnershipFacade.markReferralAsCancelled(referral.getTenantId());

        StepVerifier.create(partnershipMono).expectNext(updatedPartnership).verifyComplete();
    }

    @Test
    void markReferralAsCancelled_noPartnershipFound_returnException() {
        // When
        when(partnershipService.markAsCancelledReferralClient(referral.getTenantId())).thenReturn(Mono.empty());

        Mono<Partnership> partnershipMono = partnershipFacade.markReferralAsCancelled(referral.getTenantId());

        StepVerifier.create(partnershipMono).expectError(SpecificReferralNotFoundApiException.class).verify();
    }
}