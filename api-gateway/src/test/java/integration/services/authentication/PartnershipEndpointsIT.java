package integration.services.authentication;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PartnershipEndpointsIT extends TestContainersInitializerIT {
    @Order(1)
    @Test
    public void authentication_getPartnership_validTokenWithPartnershipScope_ReturnsRelevantPartnership() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.PARTNERSHIP_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.tenantId").isEqualTo("it_tenant")
                .jsonPath("$.partnerName").isEqualTo("partner name #1")
                .jsonPath("$.supportedReferrals.length()").isEqualTo(5)
                .jsonPath("$.supportedReferrals.*[?(@.tenantId == 'org_Wf01rJjWqDZ9vCaO')]").exists();
    }

    @Order(1)
    @Test
    public void authentication_getPartnership_InvalidTokenWithoutPartnershipScope_ReturnsForbidden() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.PARTNERSHIP_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_NO_SCOPES);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isForbidden();
    }

    @Order(1)
    @Test
    public void authentication_getPartnership_validTokenWithPartnershipScopeButTenantNotExists_ReturnsNotFound() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.PARTNERSHIP_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_DIFFERENT_TENANT);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(1)
    @Test
    public void authentication_upsertReferral_validTokenAndValidReferral_ReturnsRelevantPartnership() {
        WEB_TEST_CLIENT
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.PARTNERSHIP_BASE_URL + "/client")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.referralJsonExample("sysTest referral tenantId #1", "referralName #1"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.tenantId").isEqualTo("it_tenant")
                .jsonPath("$.partnerName").isEqualTo("partner name #1")
                .jsonPath("$.supportedReferrals.length()").isEqualTo(6)
                .jsonPath("$.supportedReferrals.*[?(@.tenantId == 'sysTest referral tenantId #1' && @.partnershipStatus == 'ACTIVE')]").exists()
                .jsonPath("$.supportedReferrals.*[?(@.tenantId == 'sysTest referral tenantId #1' && @.partnershipStatus == 'CANCELLED')]").doesNotExist();
    }

    @Order(2)
    @Test
    public void authentication_upsertReferral_validTokenAndValidPartialReferral_ReturnsRelevantPartnership() {
        WEB_TEST_CLIENT
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.PARTNERSHIP_BASE_URL + "/client")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.partialReferralJsonExample("sysTest referral tenantId #2", "referralName #2"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.tenantId").isEqualTo("it_tenant")
                .jsonPath("$.partnerName").isEqualTo("partner name #1")
                .jsonPath("$.supportedReferrals.length()").isEqualTo(7)
                .jsonPath("$.supportedReferrals.*[?(@.tenantId == 'sysTest referral tenantId #2')]").exists();
    }

    @Order(2)
    @Test
    public void authentication_upsertReferral_validTokenAndNoReferralSend_ReturnsError() {
        WEB_TEST_CLIENT
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.PARTNERSHIP_BASE_URL + "/client")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isNotFound(); // should be 400 ?
    }

    @Order(3)
    @Test
    public void authentication_upsertReferral_MissingTokenInReferral_ReturnsError() {
        WEB_TEST_CLIENT
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.PARTNERSHIP_BASE_URL + "/client")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.referralWithNullTenantIdJsonExample("referralName #1"))
                .exchange()
                .expectStatus().isUnauthorized(); // should be 400 ?
    }

    @Order(4)
    @Test
    public void authentication_deleteReferral_validTokenAndExistingReferral_ReturnsRelevantPartnership() {
        WEB_TEST_CLIENT
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.PARTNERSHIP_BASE_URL + "/client")
                        .queryParam("tenantId", "sysTest referral tenantId #1")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.tenantId").isEqualTo("it_tenant")
                .jsonPath("$.partnerName").isEqualTo("partner name #1")
                .jsonPath("$.supportedReferrals.length()").isEqualTo(7)
                .jsonPath("$.supportedReferrals.*[?(@.tenantId == 'sysTest referral tenantId #1' && @.partnershipStatus == 'CANCELLED')]").exists()
                .jsonPath("$.supportedReferrals.*[?(@.tenantId == 'sysTest referral tenantId #1' && @.partnershipStatus == 'ACTIVE')]").doesNotExist();
    }

    @Order(5)
    @Test
    public void authentication_deleteReferral_validTokenAndNotExistingReferral_ReturnsError() {
        WEB_TEST_CLIENT
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.PARTNERSHIP_BASE_URL + "/client")
                        .queryParam("tenantId", "sysTest referral tenantId - does not exist")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(5)
    @Test
    public void authentication_deleteReferral_validTokenAndNotExistingPartnership_ReturnsError() {
        WEB_TEST_CLIENT
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.PARTNERSHIP_BASE_URL + "/client")
                        .queryParam("tenantId", "sysTest referral tenantId #1")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_DIFFERENT_TENANT);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isNotFound();
    }
}
