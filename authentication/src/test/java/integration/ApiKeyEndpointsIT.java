package integration;

import io.complyt.authentication.AuthenticationApplication;
import io.complyt.authentication.v1.config.error_messages.GenericErrorMessages;
import io.complyt.authentication.v1.models.ApiKeyDto;
import io.complyt.authentication.v1.models.CredentialsDto;
import io.complyt.authentication.v1.routers.ApiKeyRouter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import test_utils.unit_tests.TestUtilities;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {SecurityConfig.class})
@SpringBootTest(classes = AuthenticationApplication.class)
@AutoConfigureWebTestClient
public class ApiKeyEndpointsIT extends TestContainersInitializerIT {
    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri",
                () -> MONGO_CONTAINER.getReplicaSetUrl("authentication"));
    }

    @Test
    @WithMockUser
    public void post_Exists_Returns201() {
        CredentialsDto credentialsDto = TestUtilities.createCredentialsDto();

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(ApiKeyRouter.BASE_URL)
                        .build())
                .bodyValue(credentialsDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ApiKeyDto.class)
                .returnResult();
    }

    @Test
    @WithMockUser
    public void post_UnsupportedMediaType_Returns415() {

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(ApiKeyRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.UNSUPPORTED_MEDIA_TYPE, map.get("message")));
    }

//    @Test
//    @WithMockUser
//    public void delete_Exists_Returns204() {
//        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();
//
//        webTestClient
//                .mutateWith(csrf())
//                .delete()
//                .uri(uriBuilder -> uriBuilder
//                        .path(ApiKeyRouter.BASE_URL)
//                        .queryParam("clientId", apiKeyDto.clientId())
//                        .queryParam("clientSecret", apiKeyDto.clientSecret())
//                        .build())
//                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED)
//                .exchange()
//                .expectStatus().isNoContent();
//    }
//
//    @Test
//    @WithMockUser
//    public void delete_NoApiKeyProvided_Returns415() {
//
//        webTestClient
//                .mutateWith(csrf())
//                .delete()
//                .uri(uriBuilder -> uriBuilder
//                        .path(ApiKeyRouter.BASE_URL)
//                        .queryParam("clientId", "")
//                        .queryParam("clientSecret", "")
//                        .build())
//                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED)
//                .exchange()
//                .expectStatus().is4xxClientError()
//                .expectBody(LinkedHashMap.class)
//                .value(map -> assertEquals(GenericErrorMessages.UNSUPPORTED_MEDIA_TYPE, map.get("message")));
//    }

}
