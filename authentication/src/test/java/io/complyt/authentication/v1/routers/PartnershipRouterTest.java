package io.complyt.authentication.v1.routers;

import io.complyt.authentication.domain.Partnership;
import io.complyt.authentication.domain.Referral;
import io.complyt.authentication.domain.enums.PartnershipStatus;
import io.complyt.authentication.facades.PartnershipFacade;
import io.complyt.authentication.v1.config.ApiExceptionConfig;
import io.complyt.authentication.v1.exceptions.GlobalErrorAttributes;
import io.complyt.authentication.v1.exceptions.GlobalExceptionHandler;
import io.complyt.authentication.v1.exceptions.types.PartnerNotFoundApiException;
import io.complyt.authentication.v1.exceptions.types.ReferralsNotFoundApiException;
import io.complyt.authentication.v1.exceptions.types.SpecificReferralNotFoundApiException;
import io.complyt.authentication.v1.handlers.PartnershipHandler;
import io.complyt.authentication.v1.mappers.PartnershipMapper;
import io.complyt.authentication.v1.models.PartnershipDto;
import io.complyt.authentication.v1.validators.ValidatorConfig;
import io.complyt.authentication.v1.validators.query_params.QueryParamsExtractorEmpty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import test_utils.unit_tests.TestUtilities;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest
@ContextConfiguration(classes = {PartnershipRouter.class, PartnershipHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class, GlobalErrorAttributes.class, GlobalExceptionHandler.class,
        QueryParamsExtractorEmpty.class})
class PartnershipRouterTest {
    @InjectMocks
    PartnershipRouter partnershipRouter;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PartnershipFacade partnershipFacade;

    Partnership partnership;

    Referral referral;

    @BeforeEach
    void setUp() {
        partnership = TestUtilities.createPartnership();
        referral = TestUtilities.createReferral();

    }

    @Test
    @WithMockUser
    public void getAllReferrals_Exists_Returns200() {
        // Given
        List<Referral> referrals = new ArrayList<>();
        referrals.add(referral);
        Partnership updatedPartnership = partnership.withSupportedReferrals(referrals);

        PartnershipDto partnershipDto = PartnershipMapper.INSTANCE.partnershipToPartnershipDto(updatedPartnership);

        // When
        when(partnershipFacade.getPartnership()).thenReturn(Mono.just(updatedPartnership));

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PartnershipDto.class)
                .isEqualTo(partnershipDto);
    }

    @Test
    @WithMockUser
    public void getAllReferrals_DoesntExists_Returns401() {
        // When
        when(partnershipFacade.getPartnership()).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser
    public void getAllReferrals_DoesntExists_Returns4201() {
        // When
        when(partnershipFacade.getPartnership()).thenReturn(Mono.error(new ReferralsNotFoundApiException()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser
    public void upsertReferral_newReferral_Returns200() {
        // Given
        List<Referral> referrals = new ArrayList<>();
        referrals.add(referral);
        Partnership updatedPartnership = partnership.withSupportedReferrals(referrals);

        PartnershipDto partnershipDto = PartnershipMapper.INSTANCE.partnershipToPartnershipDto(updatedPartnership);

        // When
        when(partnershipFacade.upsertReferralClient(referral)).thenReturn(Mono.just(updatedPartnership));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL + "/client")
                        .build())
                .bodyValue(referral)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PartnershipDto.class)
                .isEqualTo(partnershipDto);
    }

    @Test
    @WithMockUser
    public void upsertReferral_newReferralWithContentTypeJson_Returns200() {
        // Given
        List<Referral> referrals = new ArrayList<>();
        referrals.add(referral);
        Partnership updatedPartnership = partnership.withSupportedReferrals(referrals);

        PartnershipDto partnershipDto = PartnershipMapper.INSTANCE.partnershipToPartnershipDto(updatedPartnership);

        // When
        when(partnershipFacade.upsertReferralClient(referral)).thenReturn(Mono.just(updatedPartnership));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL + "/client")
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(referral)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PartnershipDto.class)
                .isEqualTo(partnershipDto);
    }

    @Test
    public void upsertReferral_newReferralWithContentTypeUrlEncoded_Returns401() {
        // Given
        String body = "tenantId=tenantId&name=name&partnershipStatus=ACTIVE";
        List<Referral> referrals = new ArrayList<>();
        referrals.add(referral);
        Partnership updatedPartnership = partnership.withSupportedReferrals(referrals);

        // When
        when(partnershipFacade.upsertReferralClient(referral)).thenReturn(Mono.just(updatedPartnership));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL + "/client")
                        .build())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser
    public void upsertReferral_PartnerDoesntExist_Returns401() {
        // When
        when(partnershipFacade.upsertReferralClient(referral)).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL + "/client")
                        .build())
                .bodyValue(referral)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser
    public void upsertReferral_PartnerDoesntExist_Returns404() {
        // When
        when(partnershipFacade.upsertReferralClient(referral)).thenReturn(Mono.error(new PartnerNotFoundApiException()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL + "/client")
                        .build())
                .bodyValue(referral)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser
    public void deleteReferral_newReferral_Returns200() {
        // Given
        List<Referral> referrals = new ArrayList<>();
        referrals.add(referral.withPartnershipStatus(PartnershipStatus.CANCELLED));
        Partnership updatedPartnership = partnership.withSupportedReferrals(referrals);

        PartnershipDto partnershipDto = PartnershipMapper.INSTANCE.partnershipToPartnershipDto(updatedPartnership);

        // When
        when(partnershipFacade.markReferralAsCancelled(referral.getTenantId())).thenReturn(Mono.just(updatedPartnership));

        // Then
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL + "/client")
                        .queryParam("tenantId", referral.getTenantId())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PartnershipDto.class)
                .isEqualTo(partnershipDto);
    }

    @Test
    @WithMockUser
    public void deleteReferral_PartnerDoesntExistReturnMonoEmpty_Returns404() {
        // When
        when(partnershipFacade.markReferralAsCancelled(referral.getTenantId())).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL + "/client")
                        .queryParam("tenantId", referral.getTenantId())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser
    public void deleteReferral_PartnerDoesntExistReturnException_Returns404() {
        // When
        when(partnershipFacade.markReferralAsCancelled(referral.getTenantId())).thenReturn(Mono.error(new SpecificReferralNotFoundApiException()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL + "/client")
                        .queryParam("tenantId", referral.getTenantId())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser
    public void deleteReferral_sentWithoutQueryParam_Returns404() {
        when(partnershipFacade.markReferralAsCancelled("Invalid tenantId")).thenReturn(Mono.error(new SpecificReferralNotFoundApiException()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL + "/client")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void getTokenForPartner_NullHandler_ThrowsNullPointerException() {
        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            partnershipRouter.getPartnership(null);
        });

        // Then
        assertEquals("partnershipHandler is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void upsertReferral_NullHandler_ThrowsNullPointerException() {
        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            partnershipRouter.upsertReferral(null);
        });

        // Then
        assertEquals("partnershipHandler is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void deleteReferral_NullHandler_ThrowsNullPointerException() {
        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            partnershipRouter.deleteReferral(null);
        });

        // Then
        assertEquals("partnershipHandler is marked non-null but is null", exception.getMessage());
    }
}