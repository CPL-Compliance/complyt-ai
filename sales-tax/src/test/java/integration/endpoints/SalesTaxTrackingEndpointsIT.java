package integration.endpoints;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.nexus.NexusCalculationSummaryDto;
import com.complyt.v1.routers.SalesTaxTrackingRouter;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
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
public class SalesTaxTrackingEndpointsIT extends TestContainersInitializerIT implements SalesTaxTrackingEndpointsITTemplate {

    private final StateDto existingState = new StateDto("AZ", "04", "Arizona");
    private final StateDto newState = new StateDto("AL", "01", "Alabama");
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
        // Given
        when(tenantResolver.resolve()).thenReturn(Mono.just("different_tenant"));

        // Then
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
        // Given
        String complytId = "cba95b8d-ef9b-4f4d-831d-377621556b50";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> assertEquals(salesTaxTrackingDto.complytId(), UUID.fromString(complytId)));
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
                        .path(SalesTaxTrackingRouter.BASE_URL + "/complytId/" + ITUtilities.NON_EXISTING_COMPLYT_ID)
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
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + existingState.name())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> assertEquals(salesTaxTrackingDto.state().name(), existingState.name()));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByStateAbbreviation_Exists_Returns200() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + existingState.abbreviation())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> assertEquals(salesTaxTrackingDto.state().name(), existingState.name()));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByStateAbbreviation_DoesntExists_Returns404() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + newState.abbreviation())
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
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + newState.name())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(3)
    @Test
    @Override
    @WithMockUser
    public void upsertByState_DoesntExists_Returns201() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(newState);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + salesTaxTrackingDto.state().name())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SalesTaxTrackingDto.class)
                .value(resultSalesTaxTrackingDto -> assertEquals(
                        salesTaxTrackingDto
                                .withComplytId(resultSalesTaxTrackingDto.complytId())
                                .withTransactionNexusSummaries(Map.of())
                                .withNexusCalculationSummaries(Map.of(LocalDate.now(), new NexusCalculationSummaryDto(0, BigDecimal.ZERO)))
                                .withClientTracking(ITUtilities.stubClientTrackingDto())
                                .withNexusStateRule(ITUtilities.stubAlabamaNexusStateRuleDto()),
                        resultSalesTaxTrackingDto)
                );
    }

    @Order(4)
    @Test
    @Override
    @WithMockUser
    public void upsertByState_Exists_Returns200() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(newState).withComment("a new comment");

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + salesTaxTrackingDto.state().abbreviation())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(resultSalesTaxTrackingDto -> assertEquals(
                        salesTaxTrackingDto.
                                withComplytId(resultSalesTaxTrackingDto.complytId())
                                .withComment("a new comment")
                                .withTransactionNexusSummaries(Map.of())
                                .withNexusCalculationSummaries(Map.of(LocalDate.now(), new NexusCalculationSummaryDto(0, BigDecimal.ZERO)))
                                .withClientTracking(ITUtilities.stubClientTrackingDto())
                                .withNexusStateRule(ITUtilities.stubAlabamaNexusStateRuleDto()),
                        resultSalesTaxTrackingDto)
                );
    }

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void upsertByState_DoesntExistsWithComplytId_Returns400ConflictedData() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(newState)
                .withComplytId(UUID.randomUUID());

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + salesTaxTrackingDto.state().name())
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
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(newState)
                .withComplytId(UUID.randomUUID());

        // Then
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
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(new StateDto("AL", "01", null))
                .withEconomicNexusTracker(null);
        Set expectedErrors = Set.of(
                "economicNexusTracker " + DtoErrorMessages.NOT_NULL_ERROR,
                "State.name " + DtoErrorMessages.NOT_NULL_ERROR);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + salesTaxTrackingDto.state().abbreviation())
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
    }

    @Override
    public void upsertByState_NoBody_Returns400() {
        // Given
        String state = "CA";

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.MISSING_BODY_ERROR, map.get("message")));
    }
}