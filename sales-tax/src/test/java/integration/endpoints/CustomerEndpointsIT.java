package integration.endpoints;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.routers.CustomerRouter;
import integration.TestContainersInitializerIT;
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

import java.util.LinkedHashMap;
import java.util.Set;
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
public class CustomerEndpointsIT extends TestContainersInitializerIT implements CustomerEndpointsITTemplate {

    @MockBean
    TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    // Given
    private final String source = "1";

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getAllBySource_Exists_Returns200() {
        // Given
        String differentSource = "2";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + differentSource)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list ->
                        assertEquals(1, list.size()));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getAllBySource_DoesntExists_Returns200EmptyList() {
        // Given
        String differentSource = "9";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + differentSource)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list ->
                        assertEquals(list.size(), 0));
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
                        .path(CustomerRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list ->
                        assertTrue(list.size() > 5));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByAll_DoesntExists_Returns200EmptyList() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("different_tenant"));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list ->
                        assertEquals(list.size(), 0));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByComplytId_Exists_Returns200() {
        // Given
        String complytId = "4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5"; //complytId of existing customer

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(customerDto ->
                        assertEquals(customerDto.complytId(), UUID.fromString(complytId)));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByComplytId_DoesntExists_Returns404() {
        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/complytId/" + ITUtilities.NON_EXISTING_COMPLYT_ID)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByComplytId_complytIdDoesntParse_Returns500() {
        // Given
        String invalidComplytId = "gg";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/complytId/" + invalidComplytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByExternalIdAndSource_Exists_Returns200() {
        // Given
        String externalId = "1586";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByExternalIdAndSource_DoesntExists_Returns404() {
        // Given
        String externalId = "nonExisting";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByName_Exists_Returns200() {
        // Given
        String name = "best";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/name/" + name)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list -> assertTrue(list.size() > 0));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByName_DoesntExists_Returns200EmptyList() {
        // Given
        String name = "nonExisting";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/name/" + name)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list -> assertEquals(list.size(), 0));
    }

    @Order(3)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_Exists_Returns200() {
        // Given
        String externalId = "1001";
        CustomerDto customerDto = ITUtilities.stubCustomerDto(externalId);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntExists_Returns201() {
        // Given
        String externalId = "1001";
        CustomerDto customerDto = ITUtilities.stubCustomerDto(externalId);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntExistsWithComplytId_Returns400ConflictedData() {
        // Given
        String externalId = "1002";
        CustomerDto customerDto = ITUtilities.stubCustomerDto(externalId).withComplytId(UUID.randomUUID());

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_ConflictingSource_Returns400ConflictedData() {
        // Given
        String externalId = "1002";
        String differentSource = "9";
        CustomerDto customerDto = ITUtilities.stubCustomerDto(externalId);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + differentSource + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_ConflictingExternalId_Returns400ConflictedData() {
        // Given
        String externalId = "someId";
        String differentExternalId = "differentId";
        CustomerDto customerDto = ITUtilities.stubCustomerDto(externalId);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + differentExternalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntPassValidation_Returns400CValidationError() {
        // Given
        String externalId = "1003";
        CustomerDto customerDto = ITUtilities.stubCustomerDto(externalId)
                .withCustomerType(null).withName(null);
        Set expectedErrors = Set.of(
                "name " + DtoErrorMessages.NOT_NULL_ERROR,
                "customerType " + DtoErrorMessages.NOT_NULL_ERROR
        );

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .exchange()

                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    String[] errors = message.substring(1, message.length() - 1).split(", ");
                    assertEquals(expectedErrors.size(), errors.length);
                    for (String err : errors) {
                        assertTrue(expectedErrors.contains(err));
                    }
                });
    }

    @Override
    public void upsertByExternalIdAndSource_NoBody_Returns400() {
        // Given
        String externalId = "0";

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.MISSING_BODY_ERROR, map.get("message")));
    }
}