package integration;

import io.complyt.authentication.AuthenticationApplication;
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
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import testUtils.TestUtilities;

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

        EntityExchangeResult<ApiKeyDto> apiKeyDtoEntityExchangeResult = webTestClient
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
}
