package integration.services.authentication;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class SecretKeyEndpointsIT extends TestContainersInitializerIT {
    @Test
    public void authentication_secretKey_post_pathNotExists_Returns404() {
        WEB_TEST_CLIENT
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SECRET_KEY_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isNotFound();
    }
}
