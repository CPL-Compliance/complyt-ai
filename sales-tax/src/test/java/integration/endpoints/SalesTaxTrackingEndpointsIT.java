package integration.endpoints;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.StateDto;
import com.complyt.v1.routers.SalesTaxTrackingRouter;
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
public class SalesTaxTrackingEndpointsIT extends MongoContainerInitializer implements SalesTaxTrackingEndpointsITTemplate {

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
    public void getAll_Exists_Returns200() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SalesTaxTrackingDto.class)
                .value(list -> assertTrue(list.size() > 4));
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
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SalesTaxTrackingDto.class)
                .value(list -> assertEquals(list.size(), 0));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByComplytId_Exists_Returns200() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/complytId/cba95b8d-ef9b-4f4d-831d-377621556b50")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> assertEquals(salesTaxTrackingDto.complytId(), UUID.fromString("cba95b8d-ef9b-4f4d-831d-377621556b50")));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByComplytId_DoesntExists_Returns404() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/complytId/1111111-1111-1111-1111-111111111111")
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
                        .path(SalesTaxTrackingRouter.BASE_URL + "/complytId/invalid")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByStateName_Exists_Returns200() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/Arizona")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> assertEquals(salesTaxTrackingDto.state().name(), "Arizona"));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByStateAbbreviation_Exists_Returns200() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/AZ")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> assertEquals(salesTaxTrackingDto.state().name(), "Arizona"));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByStateAbbreviation_DoesntExists_Returns404() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/AL")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByStateName_DoesntExists_Returns404() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/Alabama")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(4)
    @Test
    @Override
    @WithMockUser
    public void upsertByState_Exists_Returns200() {
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(new StateDto("AL", "01", "Alabama"));

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/Alabama")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(resultSalesTaxTrackingDto -> assertEquals(
                        salesTaxTrackingDto.withComplytId(resultSalesTaxTrackingDto.complytId()),
                        resultSalesTaxTrackingDto)
                );
    }

    @Order(3)
    @Test
    @Override
    @WithMockUser
    public void upsertByState_DoesntExists_Returns201() {
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(new StateDto("AL", "01", "Alabama"));

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/Alabama")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SalesTaxTrackingDto.class)
                .value(resultSalesTaxTrackingDto -> assertEquals(
                        salesTaxTrackingDto.withComplytId(resultSalesTaxTrackingDto.complytId()),
                        resultSalesTaxTrackingDto)
                );
    }

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void upsertByState_DoesntExistsWithComplytId_Returns400ConflictedData() {
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(new StateDto("AL", "01", "Alabama"))
                .withComplytId(UUID.randomUUID());

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/Alabama")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void upsertByState_ConflictingState_Returns400ConflictedData() {
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(new StateDto("AL", "01", "Alabama"))
                .withComplytId(UUID.randomUUID());

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/dope")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void upsertByState_DoesntPassValidation_Returns400CValidationError() {
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(new StateDto("AL", "01", null))
                .withEconomicNexusTracker(null);

        Set expectedErrors = Set.of(
                "economicNexusTracker " + DtoErrorMessages.NOT_NULL_ERROR,
                "State.name " + DtoErrorMessages.NOT_NULL_ERROR);

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/Alabama")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
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
        ;
    }
}