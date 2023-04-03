package integration.endpoints;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.routers.CustomerRouter;
import integration.MongoContainerInitializer;
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
import testUtils.it.ITUtilities;

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
public class CustomerEndpointsIT extends MongoContainerInitializer implements CustomerEndpointsITTemplate {

    @MockBean
    TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

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
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/2")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list ->
                        assertEquals(list.size(), 1));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getAllBySource_DoesntExists_Returns200EmptyList() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/9")
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
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/complytId/4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(customerDto ->
                        assertEquals(customerDto.complytId(), UUID.fromString("4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5")));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByComplytId_DoesntExists_Returns404() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/complytId/1111111-1111-1111-1111-111111111111")
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
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/complytId/gg")
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
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/1/externalId/1586")
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
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/1/externalId/notExisting")
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
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/name/best")
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
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/name/notExisting")
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
        CustomerDto customerDto = ITUtilities.stubCustomerDto("1001");

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/1/externalId/1001")
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
        CustomerDto customerDto = ITUtilities.stubCustomerDto("1001");

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/1/externalId/1001")
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
        CustomerDto customerDto = ITUtilities.stubCustomerDto("1002").withComplytId(UUID.randomUUID());

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/1/externalId/1002")
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
        CustomerDto customerDto = ITUtilities.stubCustomerDto("1002");

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/9/externalId/1002")
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
        CustomerDto customerDto = ITUtilities.stubCustomerDto("someId");

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/1/externalId/differentId")
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
        CustomerDto customerDto = ITUtilities.stubCustomerDto("1003")
                .withCustomerType(null).withName(null);
        Set expectedErrors = Set.of(
                "name " + DtoErrorMessages.NOT_NULL_ERROR,
                "customerType " + DtoErrorMessages.NOT_NULL_ERROR
        );

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/1/externalId/1003")
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
}
