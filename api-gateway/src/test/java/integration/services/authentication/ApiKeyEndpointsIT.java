package integration.services.authentication;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class ApiKeyEndpointsIT extends TestContainersInitializerIT {
    @Test
    public void authentication_apiKey_post_clientCredentialsExists_Returns201() {
        WEB_TEST_CLIENT
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.API_KEY_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.getClientCredentialsJsonExample())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.clientId").exists()
                .jsonPath("$.clientSecret").exists();
    }

    @Test
    public void authentication_apiKey_post_noJwt_Returns401() {
        WEB_TEST_CLIENT
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.API_KEY_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.getNonExistingClientCredentialsJsonExample())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void authentication_apiKey_post_notSuitableJwt_Returns403() {
        WEB_TEST_CLIENT
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.API_KEY_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.getNonExistingClientCredentialsJsonExample())
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void authentication_apiKey_delete_clientCredentialsExists_Returns204() {
        WEB_TEST_CLIENT
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.API_KEY_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void authentication_apiKey_delete_noJwt_Returns204() {
        WEB_TEST_CLIENT
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.API_KEY_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isNoContent();
    }
}
