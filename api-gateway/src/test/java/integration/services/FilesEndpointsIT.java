package integration.services;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import io.complyt.apigateway.ApiGatewayApplication;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = ApiGatewayApplication.class
        , webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
        , properties = {"server.port=8765", "management.server.port=8765"}
)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(profiles = {"integration-test", "stubFastTax"})
public class FilesEndpointsIT extends TestContainersInitializerIT implements FilesEndpointsITTemplate {

    @Autowired
    private WebTestClient webTestClient;

    @Order(-1)
    @Test
    @Override
    public void checkConnection() {
        while (!IS_FILES_REGISTERED) {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(TestUtilities.FILES_BASE_URL)
                            .build())
                    .headers(headers -> headers
                            .setBearerAuth(TOKEN))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().value(status -> IS_FILES_REGISTERED = status != 503);
        }
    }

    @Order(1)
    @Test
    @Override
    public void get_InsufficientScopes_Returns403() {
        webTestClient
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
        webTestClient
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
        webTestClient
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
        webTestClient
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
