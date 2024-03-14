package integration.endpoints;

import com.complyt.SalesTaxApplication;
import com.complyt.repositories.Constants.RepositoryConstant;
import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.models.PhysicalNexusTrackerDto;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SalesTaxTrackingEndpointsIT extends TestContainersInitializerIT implements SalesTaxTrackingEndpointsITemplate {

    private final StateDto existingState = new StateDto("AZ", "04", "Arizona");
    private final StateDto newState = new StateDto("AL", "01", "Alabama");
    private final StateDto stateWithNexus = new StateDto("TX", "48", "Texas");
    private final StateDto stateWithOldRule = new StateDto("HI", "101", "Hawaii");

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

    @Override
    public void upsertByState_UnsupportedMediaType_Returns415() {
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
                .bodyValue("{}")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.UNSUPPORTED_MEDIA_TYPE, map.get("message")));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void refreshByStateAndDate_Exists_Returns200WithSummaryAndNewNexusRule() {
        LocalDate localDate = LocalDate.now();
        LocalDate summaryDate = localDate.isAfter(localDate.withDayOfMonth(1).withMonth(6))
                ? LocalDate.of(localDate.getYear() + 1, 6, 1)
                : LocalDate.of(localDate.getYear(), 6, 1);

        // Then
        webTestClient
                .mutateWith(csrf())
                .mutate().responseTimeout(Duration.ofMinutes(2)).build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh/state/" + stateWithOldRule.name())
                        .queryParam("date", localDate)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> {
                    assertNotNull(salesTaxTrackingDto.nexusCalculationSummaries().get(summaryDate));
                    assertEquals(
                            LocalDateTime.of(2022, 1, 1, 0, 0, 0, 0),
                            salesTaxTrackingDto.nexusStateRule().appliedDate());
                });

    }

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void refreshByStateAndDate_ExistsAndHasNexus_Returns200NoSummary() {
        LocalDate localDate = LocalDate.now();

        // Then
        webTestClient
                .mutateWith(csrf())
                .mutate().responseTimeout(Duration.ofMinutes(2)).build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh/state/" + stateWithNexus.name())
                        .queryParam("date", localDate)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> assertNull(salesTaxTrackingDto.nexusCalculationSummaries().get(localDate)));
    }

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void refreshByStateAndDate_DoesntExists_Returns404NotFound() {
        String state = "DE";

        // Then
        webTestClient
                .mutateWith(csrf())
                .mutate().responseTimeout(Duration.ofMinutes(2)).build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh/state/" + state)
                        .queryParam("date", LocalDate.now())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void refreshByStateAndDate_DoesNotPassValidation_Returns400() {
        String state = "DE";
        String badDate = "4323/200/23";

        // Then
        webTestClient
                .mutateWith(csrf())
                .mutate().responseTimeout(Duration.ofMinutes(2)).build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh/state/" + state)
                        .queryParam("date", badDate)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals("[date " + DtoErrorMessages.LOCALDATE_FORMAT_ERROR + "]", map.get("message")));
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
    public void getAll_QueryParamInvalid_Returns400() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
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
    public void getByAll_QueryParamInvalid_Returns400() {
        // Given
        when(tenantResolver.resolve()).thenReturn(Mono.just("different_tenant"));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
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
    public void getByComplytId_Exists_Returns200() {
        // Given
        String complytId = "6eaa133c-df9c-4f88-bba9-6dd3845c803a";

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
    public void getByComplytId_PathVariableInvalid_Returns400() {
        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/complytId/" + "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
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
    public void getByStateName_PathVariableInvalid_Returns400() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
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
                                .withNexusCalculationSummaries(Map.of(LocalDate.now(), new NexusCalculationSummaryDto(0, BigDecimal.ZERO)))
                                .withNexusStateRule(ITUtilities.stubAlabamaNexusStateRuleDto())
                        , resultSalesTaxTrackingDto)
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
                                .withNexusCalculationSummaries(Map.of(LocalDate.now(), new NexusCalculationSummaryDto(0, BigDecimal.ZERO)))
                                .withNexusStateRule(ITUtilities.stubAlabamaNexusStateRuleDto())
                        ,
                        resultSalesTaxTrackingDto)
                );
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByState_PathVariableInvalid_Returns400() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(newState).withComment("a new comment");

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/null")
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
        Set<String> expectedErrors = Set.of(
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

    @Order(0)
    @Test
    @WithMockUser
    @Override
    public void getAll_GetByParamSize_ReturnsExpectedSize() {
        int size = 1;
        String expectedComplyId = "6eaa133c-df9c-4f88-bba9-6dd3845c803a";

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL) // Set your API endpoint
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> Assertions.assertEquals(salesTaxTrackingDto.get(0).complytId().toString(), expectedComplyId))
                .hasSize(size);
    }

    @Order(0)
    @Test
    @WithMockUser
    @Override
    public void getAll_GetByParamPage_ReturnsExpectedPage() {
        int page = 2;
        int size = 1;
        String expectedComplyId = "42b6d733-decc-4608-bfd3-d45bf868827c";

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL) // Set your API endpoint
                        .queryParam("size", size)
                        .queryParam("page", page)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> {
                    Assertions.assertEquals(salesTaxTrackingDto.get(0).complytId().toString(), expectedComplyId);
                });
    }

    @Order(0)
    @Test
    @WithMockUser
    @Override
    public void getAll_GetByDefaultsSizeAndPage_ReturnsExpectedEntries() {
        String expectedComplyId = "6eaa133c-df9c-4f88-bba9-6dd3845c803a";

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL) // Set your API endpoint
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> assertEquals(salesTaxTrackingDto.get(0).complytId().toString(), expectedComplyId))
                .value(salesTaxTrackingDto -> assertTrue(salesTaxTrackingDto.size() <= RepositoryConstant.DEFAULT_PAGE_SIZE));
    }

    @Order(1)
    @Test
    @WithMockUser
    public void patch_PatchesOneField_ReturnsPatchedResource() {
        String state = "NJ";
        LocalDateTime establishedDate = LocalDateTime.now();
        PhysicalNexusTrackerDto physicalNexusTrackerToPatch = new PhysicalNexusTrackerDto(true, establishedDate);

        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("physicalNexusTracker", physicalNexusTrackerToPatch);
        }};

        webTestClient
                .mutateWith(csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state) // Set your API endpoint
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(map)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(returnedSalesTaxTrackingDto -> {
                    assertTrue(returnedSalesTaxTrackingDto.physicalNexusTracker().established());
                    assertEquals(returnedSalesTaxTrackingDto.physicalNexusTracker().establishedDate(), establishedDate);
                });
    }

    @Order(2)
    @Test
    @WithMockUser
    public void patch_PatchesTwoFields_ReturnsPatchedResource() {
        String state = "NJ";
        LocalDateTime date = LocalDateTime.now();
        PhysicalNexusTrackerDto physicalNexusTrackerToPatch = new PhysicalNexusTrackerDto(false, date);

        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("physicalNexusTracker", physicalNexusTrackerToPatch);
            put("appliedDate", date);
        }};

        webTestClient
                .mutateWith(csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(map)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(returnedSalesTaxTrackingDto -> {
                    assertFalse(returnedSalesTaxTrackingDto.physicalNexusTracker().established());
                    assertEquals(returnedSalesTaxTrackingDto.physicalNexusTracker().establishedDate(), date);
                    assertEquals(returnedSalesTaxTrackingDto.appliedDate(), date);
                });
    }

}