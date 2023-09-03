package integration;

import io.complyt.authentication.AuthenticationApplication;
import io.complyt.authentication.v1.models.TokenDto;
import io.complyt.authentication.v1.routers.TokenRouter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@SpringBootTest(classes = AuthenticationApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
public class TokenEndpointsIT extends TestContainersInitializerIT {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    String apiKey = "e2019b6f-a8c1-415c-b8b0-3fd6725c9a67-e25f4d90-1051-44f7-89fb-4c6097af7748";

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri",
                () -> MONGO_CONTAINER.getReplicaSetUrl("authentication"));
    }

    @Order(1)
    @Test
    @WithMockUser
    public void postApiKey_apiKeyNotExists_Returns404() {
        String apiKeyNotExistsInDb = "e2019b6f-a8c1-415c-b8b0-3fd6725c9a67-e25f4d90-1051-44f7-89fb-4c6097af7747";

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .queryParam("api_key", apiKeyNotExistsInDb)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(TokenDto.class);
    }

    @Order(2)
    @Test
    @WithMockUser
    public void postApiKey_apiKeyExistsButDoesntHaveToken_ReturnsAccessToken() {
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .queryParam("api_key", apiKey)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenDto.class);
    }

    @Order(3)
    @Test
    @WithMockUser
    public void postApiKey_csrfIsMissing_Returns403() {
        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .queryParam("api_key", apiKey)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }
}
