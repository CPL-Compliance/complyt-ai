package integration.endpoints;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.models.ClientTrackingDtoTenant;
import com.complyt.v1.routers.ClientTrackingRouter;
import integration.TestContainersInitializerIT;
import org.apache.commons.math.stat.inference.TestUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import testUtils.integration_test.ITUtilities;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientTrackingEndpointsIT extends TestContainersInitializerIT implements ClientTrackingEndpointsITTemplate{

    @MockBean
    TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;
    private UnitTestUtilities testUtilities;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        // Given
        String tenantId = "org_12345";
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getAll_Exists_Returns200() {
        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getAll_QueryParamInvalid_Returns400() {
        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL)
                        .queryParam("size", "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByAll_DoesntExists_Returns200EmptyList() {
        // Given - Doing it by setting page that does not exist
        int page = 5;

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL)
                        .queryParam("page", page)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByAll_QueryParamInvalid_Returns400() {
        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL)
                        .queryParam("page", "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getAll_GetByParamSize_ReturnsExpectedSize() {
        // Given
        int size = 1;

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ClientTrackingDtoTenant.class)
                .hasSize(size);
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getAll_GetByParamPage_ReturnsExpectedPage() {
        // Given
        int size = 1;
        int page = 2;
        String expectedTenantId = "it_tenant";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL)
                        .queryParam("size", size)
                        .queryParam("page", page)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ClientTrackingDtoTenant.class)
                .value(clientTracking -> Assertions.assertEquals(clientTracking.get(0).tenantId(), expectedTenantId));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getAll_GetByDefaultsSizeAndPage_ReturnsExpectedEntries() {
        // Given
        int size = 1;
        String expectedTenantId = "other_it_tenant";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ClientTrackingDtoTenant.class)
                .value(clientTracking -> Assertions.assertEquals(clientTracking.get(0).tenantId(), expectedTenantId));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByName_Exists_Returns200() {
        // Given
        String name = "RAZ";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/name/" + name)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ClientTrackingDtoTenant.class)
                .value(clientTracking -> Assertions.assertEquals(clientTracking.get(0).name(), name));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByName_DoesntExists_Returns404() {
        // Given
        String name = "nameNotInDB";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/name/" + name)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByName_PathVariableInvalid_Returns400() {
        // Given
        String invalidName = testUtilities.stringWithLength(257);

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/name/" + invalidName)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertTrue(map.get("message").toString().contains(GenericErrorMessages.MAX_256_ERROR)));;
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByTenantId_Exists_Returns200() {
        // Given
        String tenantId = "org_12345";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ClientTrackingDtoTenant.class)
                .value(clientTracking -> Assertions.assertEquals(clientTracking.get(0).tenantId(), tenantId));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByTenantId_NotExists_Returns404() {
        // Given
        String tenantId = "org_notFound";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByTenantId_PathVariableInvalid_Returns400() {
        // Given
        String invalidTenantId = testUtilities.stringWithLength(50);

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + invalidTenantId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertTrue(map.get("message").toString().contains(GenericErrorMessages.TENANT_ID_FORMAT)));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByTenantId_Exists_Returns200() {
        // Given
        String tenantId = "org_12345";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ClientTrackingDtoTenant.class)
                .value(clientTracking -> Assertions.assertEquals(clientTracking.get(0).tenantId(), tenantId));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByTenantId_PathVariableInvalid_Returns400() {
        // Given
        String name = "name";
        String invalidTenantId = testUtilities.stringWithLength(50);
        String tenantId = "org_12345";
        ClientTrackingDtoTenant clientTrackingDtoTenant = ITUtilities.stubClientTrackingDtoTenant(tenantId, name);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + invalidTenantId)
                        .build())
                .bodyValue(clientTrackingDtoTenant)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertTrue(map.get("message").toString().contains(GenericErrorMessages.TENANT_ID_FORMAT)));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByTenantId_ConflictedTenantId_Returns400() {
        // Given
        String name = "name";
        String tenantId = "org_12345";
        String otherTenantId = "org_55555";
        ClientTrackingDtoTenant clientTrackingDtoTenant = ITUtilities.stubClientTrackingDtoTenant(otherTenantId, name);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .bodyValue(clientTrackingDtoTenant)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertTrue(map.get("message").toString().contains(GenericErrorMessages.CONFLICTED_WITH_URL_ERROR)));
    }

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void upsertByTenantId_DoesntExists_Returns201() {
        // Given
        String tenantId = "org_12345";
        String name = "RAZ";
        ClientTrackingDtoTenant clientTrackingDtoTenant = ITUtilities.stubClientTrackingDtoTenant(tenantId, name);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .bodyValue(clientTrackingDtoTenant)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByTenantId_UnsupportedMediaType_Returns415() {
        // Given
        String name = "name";
        String tenantId = "org_12345";
        ClientTrackingDtoTenant clientTrackingDtoTenant = ITUtilities.stubClientTrackingDtoTenant(tenantId, name);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .bodyValue("Unsupported data")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.UNSUPPORTED_MEDIA_TYPE, map.get("message")));
    }
}
