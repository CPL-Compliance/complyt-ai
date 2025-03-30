package integration;

import io.complyt.authentication.AuthenticationApplication;
import io.complyt.authentication.domain.enums.PartnershipStatus;
import io.complyt.authentication.security.TenantResolver;
import io.complyt.authentication.v1.exceptions.types.ObjectNotFoundApiException;
import io.complyt.authentication.v1.exceptions.types.PartnerNotFoundApiException;
import io.complyt.authentication.v1.exceptions.types.ReferralsNotFoundApiException;
import io.complyt.authentication.v1.exceptions.types.SpecificReferralNotFoundApiException;
import io.complyt.authentication.v1.models.PartnershipDto;
import io.complyt.authentication.v1.models.ReferralDto;
import io.complyt.authentication.v1.routers.PartnershipRouter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import test_utils.integration_tests.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {SecurityConfig.class})
@SpringBootTest(classes = AuthenticationApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
public class PartnershipEndpointsIT extends TestContainersInitializerIT {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @MockBean
    TenantResolver tenantResolver;


    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri",
                () -> MONGO_CONTAINER.getReplicaSetUrl("authentication"));
    }

    @Order(1)
    @Test
    @WithMockUser
    public void getPartnership_partnershipExistsWithReferrals_Returns200() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));

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
                .value(partnershipDto -> {
                    assertEquals(7, partnershipDto.supportedReferrals().size());
                    assertEquals("partner name #1", partnershipDto.partnerName());

                });
    }

    @Order(1)
    @Test
    @WithMockUser
    public void getPartnership_partnershipExistsWithEmptyReferrals_Returns200() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant_empty"));

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
                .value(partnershipDto -> {
                    assertEquals(0, partnershipDto.supportedReferrals().size());
                    assertEquals("partner name #2", partnershipDto.partnerName());
                });
    }

    @Order(1)
    @Test
    @WithMockUser
    public void getPartnership_partnershipDoesntExists_Returns404() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant_not_exists"));

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ReferralsNotFoundApiException.class);
    }

    @Order(2)
    @Test
    @WithMockUser
    public void upsertReferral_partnershipExistsAndValidReferrals_Returns200() {
        ReferralDto referralDto = TestUtilities.createReferralDto();
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant_empty"));

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL + "/client")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(referralDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PartnershipDto.class)
                .value(partnershipDto -> {
                    assertEquals(1, partnershipDto.supportedReferrals().size());
                    assertEquals("partner name #2", partnershipDto.partnerName());
                    assertEquals("test referral name", partnershipDto.supportedReferrals().get(0).getName());
                    assertEquals(partnershipDto.supportedReferrals().get(0).getTimestamps().getCreatedDate(), partnershipDto.supportedReferrals().get(0).getTimestamps().getUpdatedDate());
                    assertEquals(partnershipDto.supportedReferrals().get(0).getPartnershipStatus(), PartnershipStatus.ACTIVE);

                });
    }

    @Order(3)
    @Test
    @WithMockUser
    public void upsertReferral_partnershipExistsAndReferralsExists_Returns200() {
        ReferralDto referralDto = TestUtilities.createReferralDto();
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant_empty"));

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL + "/client")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(referralDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PartnershipDto.class)
                .value(partnershipDto -> {
                    assertEquals(1, partnershipDto.supportedReferrals().size());
                    assertNotEquals(partnershipDto.supportedReferrals().get(0).getTimestamps().getCreatedDate(), partnershipDto.supportedReferrals().get(0).getTimestamps().getUpdatedDate());
                });
    }

    @Order(4)
    @Test
    @WithMockUser
    public void upsertReferral_partnershipExistsAndValidReferralsWithoutStatusAndTimestamp_Returns200() {
        ReferralDto referralDto = TestUtilities.createReferralDto().withTenantId("different referral tenantId").withPartnershipStatus(null).withTimestamps(null);
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant_empty"));

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL + "/client")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(referralDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PartnershipDto.class)
                .value(partnershipDto -> assertEquals(2, partnershipDto.supportedReferrals().size()));
    }

    @Order(5)
    @Test
    @WithMockUser
    public void upsertReferral_partnershipExistsAndInvalidReferralsWithoutName_Returns401() {
        ReferralDto referralDto = TestUtilities.createReferralDto().withName(null);
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant_empty"));

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL + "/client")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(referralDto)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ObjectNotFoundApiException.class);
    }

    @Order(5)
    @Test
    @WithMockUser
    public void upsertReferral_partnershipExistsAndInvalidReferralsWithoutTenantId_Returns401() {
        ReferralDto referralDto = TestUtilities.createReferralDto().withTenantId(null);
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant_empty"));

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL + "/client")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(referralDto)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ObjectNotFoundApiException.class);
    }

    @Order(5)
    @Test
    @WithMockUser
    public void deleteReferral_partnershipAndReferralsExists_Returns200() {
        ReferralDto referralDto = TestUtilities.createReferralDto();
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant_empty"));

        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL + "/client")
                        .queryParam("tenantId", referralDto.getTenantId())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PartnershipDto.class)
                .value(partnershipDto -> assertEquals(partnershipDto.supportedReferrals().get(0).getPartnershipStatus(), PartnershipStatus.CANCELLED));
    }

    @Order(5)
    @Test
    @WithMockUser
    public void deleteReferral_partnershipExistsButReferralNotExists_Returns404() {
        ReferralDto referralDto = TestUtilities.createReferralDto().withTenantId("Not existing tenantId");
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant_empty"));

        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL + "/client")
                        .queryParam("tenantId", referralDto.getTenantId())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(SpecificReferralNotFoundApiException.class);
    }

    @Order(5)
    @Test
    @WithMockUser
    public void deleteReferral_partnershipNotExists_Returns200() {
        ReferralDto referralDto = TestUtilities.createReferralDto();
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant_Not_Exists"));

        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(PartnershipRouter.BASE_URL + "/client")
                        .queryParam("tenantId", referralDto.getTenantId())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(PartnerNotFoundApiException.class);
    }
}