package integration.services.authentication;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class TokenEndpointsIT extends TestContainersInitializerIT {
    String expectedJwt = "accessToken";

    @Test
    public void authentication_token_post_jsonTypeApiKeyExistsButDoesntHaveToken_ReturnsAccessToken() {
        WEB_TEST_CLIENT
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TOKEN_BASE_URL)
                        .build())
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
                .bodyValue(TestUtilities.apiKey4JsonExample())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accessToken").isEqualTo(expectedJwt);
    }

    @Test
    public void authentication_token_post_urlEncodedTypeApiKeyExistsButDoesntHaveToken_ReturnsAccessToken() {
        WEB_TEST_CLIENT
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TOKEN_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                })
                .bodyValue(TestUtilities.apiKey3UrlEncodedExample())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accessToken").isEqualTo(expectedJwt);
    }
}
