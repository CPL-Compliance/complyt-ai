package integration.services.files;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilesEndpointsIT extends TestContainersInitializerIT implements FilesEndpointsITTemplate {

    @Order(1)
    @Test
    @Override
    public void get_InsufficientScopes_Returns403() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.FILES_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_NO_SCOPES);
                })
                .exchange()
                .expectStatus().isForbidden();
    }

    @Order(1)
    @Test
    @Override
    public void get_NoAccessToken_Returns401() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.FILES_BASE_URL)
                        .build())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Order(2)
    @Test
    @Override
    public void getAll_Exists_Returns200() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.FILES_BASE_URL)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.link").exists();

    }

    @Order(2)
    @Test
    @Override
    public void getByAll_DoesntExists_Returns200EmptyList() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.FILES_BASE_URL)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN_DIFFERENT_TENANT))
                .exchange()
                .expectStatus().isNotFound();
    }

}
