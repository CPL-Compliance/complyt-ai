package integration;

import io.complyt.authentication.AuthenticationApplication;
import io.complyt.authentication.v1.config.error_messages.GenericErrorMessages;
import io.complyt.authentication.v1.models.ApiKeyDto;
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
import test_utils.integration_tests.TestUtilities;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@SpringBootTest(classes = AuthenticationApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
public class TokenEndpointsIT extends TestContainersInitializerIT {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;


    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri",
                () -> MONGO_CONTAINER.getReplicaSetUrl("authentication"));
    }

    @Order(1)
    @Test
    @WithMockUser
    public void postApiKey_jsonTypeApiKeyNotExists_Returns404() {
        ApiKeyDto apiKeyDto = new ApiKeyDto("e2019b6f-a8c1-415c-b8b0-3fd6725c9a67", "e25f4d90-1051-44f7-89fb-4c6097af7747");

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(apiKeyDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(TokenDto.class);
    }
    @Order(2)
    @Test
    @WithMockUser
    public void postApiKey_urlEncodedTypeApiKeyNotExists_Returns404() {
        String urlEncodedApiKey = "clientId=e2019b6f-a8c1-415c-b8b0-3fd6725c9a67&clientSecret=e25f4d90-1051-44f7-89fb-4c6097af7747";

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                })
                .bodyValue(urlEncodedApiKey)
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(TokenDto.class);
    }
    @Order(3)
    @Test
    @WithMockUser
    public void postApiKey_jsonTypeApiKeyExistsButDoesntHaveToken_ReturnsAccessTokenWithExpirationDateTimeLessThenNowPlusExpiresIn() {
        ApiKeyDto apiKeyDto = new ApiKeyDto(TestUtilities.apiKeyClientId, TestUtilities.apiKeyClientSecret);

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(apiKeyDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenDto.class)
                .value(tokenDto -> assertTrue(tokenDto.expireAt().isBefore(LocalDateTime.now()
                        .plusSeconds(tokenDto.expiresIn()))));
    }
    @Order(4)
    @Test
    @WithMockUser
    public void postApiKey_urlEncodedTypeApiKeyExistsButDoesntHaveToken_ReturnsAccessTokenWithExpirationDateTimeLessThenNowPlusExpiresIn() {
        String urlEncodedApiKey = "clientId=" + TestUtilities.apiKeyClientId + "&clientSecret=" +
                TestUtilities.apiKeyClientSecret;

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                })
                .bodyValue(urlEncodedApiKey)
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenDto.class)
                .value(tokenDto -> assertTrue(tokenDto.expireAt().isBefore(LocalDateTime.now()
                        .plusSeconds(tokenDto.expiresIn()))));
    }

    @Order(4)
    @Test
    @WithMockUser
    public void postApiKey_UnsupportedMediaType_Returns415() {

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TokenRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("{}")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.UNSUPPORTED_MEDIA_TYPE, map.get("message")));
    }
    
}