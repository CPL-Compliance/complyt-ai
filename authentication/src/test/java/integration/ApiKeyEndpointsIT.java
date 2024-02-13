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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriUtils;
import test_utils.unit_tests.TestUtilities;

import java.nio.charset.StandardCharsets;
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

    @Test
    @WithMockUser
    public void delete_SentAsFormURLEncoded_Exists_Returns204() {
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();

        String body = "clientId=" + apiKeyDto.clientId() +
                "&clientSecret=" + apiKeyDto.clientSecret();

        webTestClient
                .mutateWith(csrf())
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path(ApiKeyRouter.BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @WithMockUser
    public void delete_SentAsJson_Exists_Returns204() {
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();

        webTestClient
                .mutateWith(csrf())
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path(ApiKeyRouter.BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(apiKeyDto)
                .exchange()
                .expectStatus().isNoContent();
    }

    /*
    Checks the length of the expected massage because the order of the Sub-sentences might change from time to time.
    Some Examples:
    1. [ApiKey.clientId may not be blank, ApiKey.clientId may not be null, ApiKey.clientSecret may not be null, ApiKey.clientSecret may not be blank]
    2. [ApiKey.clientSecret may not be blank, ApiKey.clientId may not be blank, ApiKey.clientId may not be null, ApiKey.clientSecret may not be null]
    3. [ApiKey.clientSecret may not be null, ApiKey.clientSecret may not be blank, ApiKey.clientId may not be blank, ApiKey.clientId may not be null]
     */
    @Test
    @WithMockUser
    public void delete_SentAsFormURLEncoded_HasContentTypeHeaderButNoApiKeyProvided_Returns415() {
        String expectedMassage = "[ApiKey.clientId may not be blank, ApiKey.clientId may not be null, ApiKey.clientSecret may not be null, ApiKey.clientSecret may not be blank]";

        webTestClient
                .mutateWith(csrf())
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path(ApiKeyRouter.BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("{}")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(expectedMassage.length(), ((String)map.get("message")).length()));
    }

    @Test
    @WithMockUser
    public void delete_SentAsFormURLEncoded_NoContentTypeHeaderButValidApiKeyProvided_Returns415() {
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();

        String body = "clientId=" + apiKeyDto.clientId() +
                "&clientSecret=" + apiKeyDto.clientSecret();

        webTestClient
                .mutateWith(csrf())
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path(ApiKeyRouter.BASE_URL)
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body) // text:plain content type
                .exchange()
                .expectStatus().is4xxClientError();
    }

    /*
Checks the length of the expected massage because the order of the Sub-sentences might change from time to time.
Some Examples:
1. [ApiKey.clientId may not be blank, ApiKey.clientId may not be null, ApiKey.clientSecret may not be null, ApiKey.clientSecret may not be blank]
2. [ApiKey.clientSecret may not be blank, ApiKey.clientId may not be blank, ApiKey.clientId may not be null, ApiKey.clientSecret may not be null]
3. [ApiKey.clientSecret may not be null, ApiKey.clientSecret may not be blank, ApiKey.clientId may not be blank, ApiKey.clientId may not be null]
 */
    @Test
    @WithMockUser
    public void delete_SentAsJson_HasContentTypeHeaderButNoApiKeyProvided_Returns415() {
        String expectedMassage = "[ApiKey.clientId may not be blank, ApiKey.clientId may not be null, ApiKey.clientSecret may not be null, ApiKey.clientSecret may not be blank]";

        webTestClient
                .mutateWith(csrf())
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path(ApiKeyRouter.BASE_URL)
                        .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("{}")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(expectedMassage.length(), ((String)map.get("message")).length()));
    }

    @Test
    @WithMockUser
    public void delete_SentAsJson_NoContentTypeHeaderButValidApiKeyProvided_Returns204() {
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();

        webTestClient
                .mutateWith(csrf())
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path(ApiKeyRouter.BASE_URL)
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(apiKeyDto)
                .exchange()
                .expectStatus().isNoContent();
    }
}
