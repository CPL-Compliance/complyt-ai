package integration;

import io.complyt.authentication.AuthenticationApplication;
import io.complyt.authentication.v1.models.TokenDto;
import io.complyt.authentication.v1.routers.TokenRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@SpringBootTest(classes = AuthenticationApplication.class)
@AutoConfigureWebTestClient
public class TokenEndpointsIT extends TestContainersInitializerIT {
    @Autowired
    private WebTestClient webTestClient;

    String apiKey = "e2019b6f-a8c1-415c-b8b0-3fd6725c9a67-e25f4d90-1051-44f7-89fb-4c6097af7748";

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri",
                () -> MONGO_CONTAINER.getReplicaSetUrl("authentication"));
    }

    @Test
    @WithMockUser
    public void postApiKey_Exists_ReturnsAccessToken() {
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
}
