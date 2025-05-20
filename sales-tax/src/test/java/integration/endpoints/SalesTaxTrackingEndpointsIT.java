package integration.endpoints;

import com.complyt.SalesTaxApplication;
import com.complyt.business.pagination.PaginationConstants;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.sales_tax.RegisteredType;
import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.models.*;
import com.complyt.v1.models.nexus.NexusCalculationSummaryDto;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.models.transaction.TransactionStatusDto;
import com.complyt.v1.routers.SalesTaxTrackingRouter;
import com.complyt.v1.routers.TransactionRouter;
import integration.TestContainersInitializerIT;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import testUtils.integration_test.ITUtilities;
import testUtils.annotations.WithMockJwt;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SalesTaxTrackingEndpointsIT extends TestContainersInitializerIT implements SalesTaxTrackingEndpointsITemplate {

    private final String usaCountry = "USA";
    private final String nonUsaCountry = "Brazil";
    private final StateDto existingState = new StateDto("AZ", "04", "Arizona");
    private final StateDto newState = new StateDto("AL", "01", "Alabama");
    private final StateDto stateWithNexus = new StateDto("TX", "48", "Texas");
    private final StateDto stateWithOldRule = new StateDto("HI", "101", "Hawaii");
    private final UUID customerId = UUID.fromString("4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5"); // complytId of an existing customer in the database


    @MockBean
    TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @Test
    @Override
    @WithMockJwt
    public void upsertByState_NoBody_Returns400() {
        // Given
        String state = "CA";

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", state)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.MISSING_BODY_ERROR, map.get("message")));
    }

    @Test
    @Override
    @WithMockJwt
    public void upsertByState_UnsupportedMediaType_Returns415() {
        // Given
        String state = "CA";

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", state)
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
    @WithMockJwt
    public void refreshByStateAndDate_Exists_Returns200WithSummaryAndNewNexusRule() {
        LocalDate localDate = LocalDate.now();
        LocalDate summaryDate = localDate.isAfter(localDate.withDayOfMonth(1).withMonth(6))
                ? LocalDate.of(localDate.getYear() + 1, 6, 1)
                : LocalDate.of(localDate.getYear(), 6, 1);

        // Then
        webTestClient
                .mutateWith(csrf()).mutate().responseTimeout(Duration.ofMinutes(2)).build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh")
                        .queryParam("country", usaCountry)
                        .queryParam("state", stateWithOldRule.name())
                        .queryParam("date", localDate)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> {
                    assertNull(salesTaxTrackingDto.nexusCalculationSummaries().get(summaryDate));
                    assertEquals(
                            LocalDateTime.of(2022, 1, 1, 0, 0, 0, 0),
                            salesTaxTrackingDto.nexusStateRule().appliedDate());
                });

    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void refreshByStateAndDate_ExistsAndHasNexus_Returns200NoSummary() {
        LocalDate localDate = LocalDate.now();

        // Then
        webTestClient
                .mutateWith(csrf()).mutate().responseTimeout(Duration.ofMinutes(2)).build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh")
                        .queryParam("country", usaCountry)
                        .queryParam("state", stateWithNexus.name())
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
    @WithMockJwt
    public void refreshByStateAndDate_DoesntExists_Returns404NotFound() {
        String state = "OR"; // Oregon

        // Then
        webTestClient
                .mutateWith(csrf()).mutate().responseTimeout(Duration.ofMinutes(2)).build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh")
                        .queryParam("country", usaCountry)
                        .queryParam("state", state)
                        .queryParam("date", LocalDate.now())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void refreshByStateAndDate_DoesNotPassValidation_Returns400() {
        String state = "DE";
        String badDate = "4323/200/23";

        // Then
        webTestClient
                .mutateWith(csrf()).mutate().responseTimeout(Duration.ofMinutes(2)).build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh")
                        .queryParam("country", usaCountry)
                        .queryParam("state", state)
                        .queryParam("date", badDate)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals("[date " + DtoErrorMessages.LOCALDATE_FORMAT_ERROR + "]", map.get("message")));
    }

    /**
     * SalesTaxTracking economicTracking.established; False
     * There is transaction with item with calculatedTotal = 100 (NexusThreshold == 100,000) from 2021
     */
    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void refreshByStateAndDate_DoesNotPassThreshold_returnsEconomicTrackerFalse() {
        String refreshState = "Virginia";
        String date = "2021-01-01";

        webTestClient
                .mutateWith(csrf()).mutate()
                .build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh")
                        .queryParam("country", usaCountry)
                        .queryParam("state", refreshState)
                        .queryParam("date", date)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingResult -> {
                    LocalDate summeryDateKey = LocalDate.of(2021, 12, 31);
                    assertTrue(salesTaxTrackingResult.nexusCalculationSummaries().containsKey(summeryDateKey));
                    NexusCalculationSummaryDto nexusCalculationSummaryDto = salesTaxTrackingResult.nexusCalculationSummaries().get(summeryDateKey);
                    assertEquals(
                            nexusCalculationSummaryDto.count(), 1);
                    assertEquals(nexusCalculationSummaryDto.amount(), BigDecimal.valueOf(100));
                    // False - Did not pass threshold
                    assertFalse(salesTaxTrackingResult.economicNexusTracker().established());
                });
    }

    /**
     * SalesTaxTracking economicTracking.established; False
     * There is transaction with item with calculatedTotal = 100,000 (NexusThreshold == 100,000) from 2022
     */
    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void refreshByStateAndDate_PassedNexus_returnsEconomicTrackerTrue() {
        String refreshState = "Virginia";
        String date = "2022-01-01";

        webTestClient
                .mutateWith(csrf()).mutate()
                .build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh")
                        .queryParam("country", usaCountry)
                        .queryParam("state", refreshState)
                        .queryParam("date", date)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingResult -> {
                    LocalDate summeryDateKey = LocalDate.of(2022, 12, 31);
                    assertTrue(salesTaxTrackingResult.nexusCalculationSummaries().containsKey(summeryDateKey));
                    NexusCalculationSummaryDto nexusCalculationSummaryDto = salesTaxTrackingResult.nexusCalculationSummaries().get(summeryDateKey);
                    assertEquals(
                            nexusCalculationSummaryDto.count(), 1);
                    assertEquals(nexusCalculationSummaryDto.amount(), BigDecimal.valueOf(100000));
                    //updated From false -> True
                    assertTrue(salesTaxTrackingResult.economicNexusTracker().established());
                    assertEquals(LocalDate.of(2022, 10, 10), salesTaxTrackingResult.economicNexusTracker().establishedDate().toLocalDate());
                });
    }

    /**
     * Utah SalesTaxTracking economicTracker, physicalNexus established sets to True
     * But no transactions for this state -> Should be sets to False
     */
    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void refreshByStateNoRefDate_DoesNotPassThreshold_returnsEconomicTrackerFalse() {
        String refreshState = "Utah";

        webTestClient
                .mutateWith(csrf()).mutate()
                .build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh")
                        .queryParam("country", usaCountry)
                        .queryParam("state", refreshState)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingResult -> {
                    // Updated from True -> False
                    assertFalse(salesTaxTrackingResult.economicNexusTracker().established());
                });
    }

    /**
     * SalesTaxTracking economicTracking.established; False
     * There are transactions which passed threshold (NexusThreshold == 100,000) from 2022
     */
    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void refreshByStateNoRefDate_PassedNexus_returnsEconomicTrackerTrue() {
        String refreshState = "Virginia";

        webTestClient
                .mutateWith(csrf()).mutate()
                .build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh")
                        .queryParam("country", usaCountry)
                        .queryParam("state", refreshState)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingResult -> {
                    LocalDate summeryDateKey = LocalDate.of(2022, 12, 31);
                    assertTrue(salesTaxTrackingResult.nexusCalculationSummaries().containsKey(summeryDateKey));
                    NexusCalculationSummaryDto nexusCalculationSummaryDto = salesTaxTrackingResult.nexusCalculationSummaries().get(summeryDateKey);
                    assertEquals(
                            nexusCalculationSummaryDto.count(), 1);
                    assertEquals(nexusCalculationSummaryDto.amount(), BigDecimal.valueOf(100000));
                    //updated From false -> True
                    assertTrue(salesTaxTrackingResult.economicNexusTracker().established());
                    assertEquals(LocalDate.of(2022, 10, 10), salesTaxTrackingResult.economicNexusTracker().establishedDate().toLocalDate());
                });
    }


    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByCountryAndState_NonUsaCountryDoesntExists_Returns201() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingNonUsaDto(nonUsaCountry);

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", nonUsaCountry)
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
                                .withNexusStateRule(ITUtilities.stubBrazilNexusStateRuleDto())
                                .withCountry(nonUsaCountry)
                        , resultSalesTaxTrackingDto)
                );
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByCountryAndState_NonUsaCountryAbbreviation_Returns200() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingNonUsaDto("BR");

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", nonUsaCountry)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(resultSalesTaxTrackingDto -> assertEquals(
                        salesTaxTrackingDto
                                .withComplytId(resultSalesTaxTrackingDto.complytId())
                                .withNexusCalculationSummaries(Map.of(LocalDate.now(), new NexusCalculationSummaryDto(0, BigDecimal.ZERO)))
                                .withNexusStateRule(ITUtilities.stubBrazilNexusStateRuleDto())
                                .withCountry(nonUsaCountry)
                        , resultSalesTaxTrackingDto)
                );
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getByCountry_Exists_Returns200() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", nonUsaCountry)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> assertEquals(salesTaxTrackingDto.country(), nonUsaCountry));
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getByCountryAbbreviation_Exists_Returns200() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", "BR")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> assertEquals(salesTaxTrackingDto.country(), nonUsaCountry));
    }


    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void refreshByCountryAndDate_NonUsaCountryExistsAndHasNexus_Returns200NoSummary() {
        LocalDate localDate = LocalDate.now();
        LocalDate summaryDate = localDate.isAfter(localDate.withDayOfMonth(1).withMonth(6))
                ? LocalDate.of(localDate.getYear() + 1, 6, 1)
                : LocalDate.of(localDate.getYear(), 6, 1);

        // Then
        webTestClient
                .mutateWith(csrf()).mutate().responseTimeout(Duration.ofMinutes(2)).build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh")
                        .queryParam("country", nonUsaCountry)
                        .queryParam("date", localDate)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> {
                    assertNull(salesTaxTrackingDto.nexusCalculationSummaries().get(summaryDate));
                    assertNull(salesTaxTrackingDto.nexusCalculationSummaries().get(localDate));
                    assertEquals(
                            LocalDateTime.of(2022, 1, 1, 0, 0, 0, 0),
                            salesTaxTrackingDto.nexusStateRule().appliedDate());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByCountryAndState_StateIsNull_Returns400() {
        // Given
        StateDto nullState = null;
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(usaCountry, null);

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", nonUsaCountry)
                        .queryParam("state", "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByCountryAndState_CountryIsNull_Returns400() {
        // Given
        String nullCountry = "null";
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(nullCountry, stateWithNexus);

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", nullCountry)
                        .queryParam("state", stateWithNexus.abbreviation())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByCountryAndState_UsaAndStateIsDifferentInBody_Returns400ConflictedData() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(usaCountry, stateWithNexus);

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", stateWithOldRule)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByCountryAndState_CountryInQueryAndBodyAreDifferent_Returns400ConflictedData() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(usaCountry, stateWithNexus);

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", nonUsaCountry)
                        .queryParam("state", stateWithOldRule)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getAll_Exists_Returns200() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/all")
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
    @WithMockJwt
    public void getAll_QueryParamInvalid_Returns400() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/all")
                        .queryParam("page", "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt(tenantId = "different_tenant")
    public void getByAll_DoesntExists_Returns200EmptyList() {

        // Given + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/all")
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
    @WithMockJwt(tenantId = "different_tenant")
    public void getByAll_QueryParamInvalid_Returns400() {

        // Given + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/all")
                        .queryParam("size", "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getByComplytId_Exists_Returns200() {
        // Given
        String complytId = "6eaa133c-df9c-4f88-bba9-6dd3845c803a";

        // Then
        webTestClient.get()
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
    @WithMockJwt
    public void getByComplytId_PathVariableInvalid_Returns400() {
        // Then
        webTestClient.get()
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
    @WithMockJwt
    public void getByComplytId_DoesntExists_Returns404() {
        // Then
        webTestClient.get()
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
    @WithMockJwt
    public void getByStateName_Exists_Returns200() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", existingState.name())
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
    @WithMockJwt
    public void getByStateName_PathVariableInvalid_Returns400() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getByStateAbbreviation_Exists_Returns200() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", existingState.abbreviation())
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
    @WithMockJwt
    public void getByStateAbbreviation_DoesntExists_Returns404() {
        String nonExistState = "IA";
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", nonExistState)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void getByCountryStateAndSubsidiary_DoesntExists_Returns404() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", "ID")
                        .queryParam("subsidiary", "D")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Override
    @Order(0)
    @Test
    @WithMockJwt
    public void getByCountryStateAndSubsidiary_Exists_Returns200() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", "ID")
                        .queryParam("subsidiary", "B")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> Assertions.assertEquals(salesTaxTrackingDto.subsidiary(), "B"));
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void path_AppliedDateIsWrongFormat_Returns400() {
        String invalidFormatAppliedDate = "T00:00:00";
        String state = "CA";
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("appliedDate", invalidFormatAppliedDate);
        }};

        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Error parsing field: " + "appliedDate" + ". " + GenericErrorMessages.INVALID_DATE_TIME_FORMAT_EXCEPTION
        ));

        webTestClient
                .mutateWith(csrf()).patch()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", state)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(map)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(errorMap -> ITUtilities.checkErrorMessages(errorMap, expectedErrors));
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void path_ApprovalDateIsWrongFormat_Returns400() {
        String invalidFormatApprovalDate = "T00:00:00";
        String state = "CA";
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("approvalDate", invalidFormatApprovalDate);
        }};

        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Error parsing field: " + "approvalDate" + ". " + GenericErrorMessages.INVALID_DATE_TIME_FORMAT_EXCEPTION
        ));

        webTestClient
                .mutateWith(csrf()).patch()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", state)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(map)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(errorMap -> ITUtilities.checkErrorMessages(errorMap, expectedErrors));
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void path_RegistrationDateIsWrongFormat_Returns400() {
        String invalidFormatRegistrationDate = "T00:00:00";
        String state = "CA";
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("registrationDate", invalidFormatRegistrationDate);
        }};

        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Error parsing field: " + "registrationDate" + ". " + GenericErrorMessages.INVALID_DATE_TIME_FORMAT_EXCEPTION
        ));

        webTestClient
                .mutateWith(csrf()).patch()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", state)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(map)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(errorMap -> ITUtilities.checkErrorMessages(errorMap, expectedErrors));
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getByStateName_DoesntExists_Returns404() {
        String NonExitsName = "Iowa";
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", NonExitsName)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(3)
    @Test
    @Override
    @WithMockJwt
    public void upsertByState_DoesntExists_Returns201() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(usaCountry, newState)
                .withSubsidiary("subsidiary");

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", salesTaxTrackingDto.state().name())
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
    @WithMockJwt
    public void upsertByState_Exists_Returns200() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(usaCountry, newState).withComment("a new comment")
                .withSubsidiary("subsidiary");

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", salesTaxTrackingDto.state().abbreviation())
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
    @WithMockJwt
    public void upsertByCountryAndState_StateQueryParamInvalid_Returns400() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(usaCountry, newState).withComment("a new comment");

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByState_DoesntExistsWithComplytId_Returns400ConflictedData() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(usaCountry, newState)
                .withComplytId(UUID.randomUUID());

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", salesTaxTrackingDto.country())
                        .queryParam("state", salesTaxTrackingDto.state().name())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByState_ConflictingState_Returns400ConflictedData() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(usaCountry, newState)
                .withComplytId(UUID.randomUUID());

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", salesTaxTrackingDto.country())
                        .queryParam("state", "dope")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByState_DoesntPassValidation_Returns400CValidationError() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(usaCountry, new StateDto("AL", "01", null))
                .withEconomicNexusTracker(null);
        Set<String> expectedErrors = Set.of(
                "economicNexusTracker " + DtoErrorMessages.NOT_NULL_ERROR,
                "State.name " + DtoErrorMessages.NOT_NULL_ERROR);

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", salesTaxTrackingDto.country())
                        .queryParam("state", salesTaxTrackingDto.state().abbreviation())
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
    @Override
    @WithMockJwt
    public void getAll_GetByParamSize_ReturnsExpectedSize() {
        int size = 1;
        String expectedComplyId = "6eaa133c-df9c-4f88-bba9-6dd3845c803a";

        webTestClient
                .mutateWith(csrf()).get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/all") // Set your API endpoint
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
    @Override
    @WithMockJwt
    public void getAll_GetByParamPage_ReturnsExpectedPage() {
        int page = 2;
        int size = 1;
        String expectedComplyId = "42b6d733-decc-4608-bfd3-d45bf868827c";

        webTestClient
                .mutateWith(csrf()).get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/all") // Set your API endpoint
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
    @Override
    @WithMockJwt
    public void getAll_GetByDefaultsSizeAndPage_ReturnsExpectedEntries() {
        String expectedComplyId = "6eaa133c-df9c-4f88-bba9-6dd3845c803a";

        webTestClient
                .mutateWith(csrf()).get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/all") // Set your API endpoint
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> assertEquals(salesTaxTrackingDto.get(0).complytId().toString(), expectedComplyId))
                .value(salesTaxTrackingDto -> assertTrue(salesTaxTrackingDto.size() <= PaginationConstants.DEFAULT_PAGE_SIZE));
    }

    @Order(1)
    @Test
    @WithMockJwt
    public void patch_PatchesOneField_ReturnsPatchedResource() {
        String country = "USA";
        String state = "NJ";
        LocalDateTime establishedDate = LocalDateTime.now();
        PhysicalNexusTrackerDto physicalNexusTrackerToPatch = new PhysicalNexusTrackerDto(true, establishedDate);
        LocalDateTime now = LocalDateTime.now();

        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("physicalNexusTracker", physicalNexusTrackerToPatch);
        }};

        webTestClient
                .mutateWith(csrf()).patch()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL) // Set your API endpoint
                        .queryParam("country", country)
                        .queryParam("state", state)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(map)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(returnedSalesTaxTrackingDto -> {
                    assertTrue(returnedSalesTaxTrackingDto.physicalNexusTracker().established());
                    assertEquals(establishedDate.getDayOfMonth(), now.getDayOfMonth(), "Day mismatch");
                    assertEquals(establishedDate.getMonth(), now.getMonth(), "Month mismatch");
                    assertEquals(establishedDate.getYear(), now.getYear(), "Year mismatch");
                });
    }

    @Order(2)
    @Test
    @WithMockJwt
    public void patch_PatchesTwoFields_ReturnsPatchedResource() {
        String country = "USA";
        String state = "NJ";
        LocalDateTime date = LocalDateTime.now();
        PhysicalNexusTrackerDto physicalNexusTrackerToPatch = new PhysicalNexusTrackerDto(false, date);

        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("physicalNexusTracker", physicalNexusTrackerToPatch);
            put("appliedDate", date);
        }};

        webTestClient
                .mutateWith(csrf()).patch()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", country)
                        .queryParam("state", state)
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

    @Order(4)
    @Test
    @Override
    @WithMockJwt
    public void upsertByState_RegisteredAndDateNull_ReturnsSalesTaxTrackingWithDate() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto("USA", newState).withRegistered(RegisteredTypeDto.REGISTERED)
                .withRegistrationDate(null);

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", salesTaxTrackingDto.country())
                        .queryParam("state", salesTaxTrackingDto.state().abbreviation())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectBody(SalesTaxTrackingDto.class)
                .value(resultSalesTaxTrackingDto -> assertNotNull(resultSalesTaxTrackingDto.registrationDate()));
    }

    @Order(4)
    @Test
    @Override
    @WithMockJwt
    public void upsertByState_RegisteredAndDate_ReturnsSalesTaxTrackingWithGivenDate() {
        // Given
        LocalDateTime registrationDate = LocalDateTime.now();
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto("USA", newState).withRegistered(RegisteredTypeDto.REGISTERED)
                .withRegistrationDate(registrationDate);

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", salesTaxTrackingDto.country())
                        .queryParam("state", salesTaxTrackingDto.state().abbreviation())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectBody(SalesTaxTrackingDto.class)
                .value(resultSalesTaxTrackingDto -> {
                    // Verify day, month, and year of registration date
                    assertEquals(resultSalesTaxTrackingDto.registrationDate().getDayOfMonth(), registrationDate.getDayOfMonth());
                    assertEquals(resultSalesTaxTrackingDto.registrationDate().getMonth(), registrationDate.getMonth());
                    assertEquals(resultSalesTaxTrackingDto.registrationDate().getYear(), registrationDate.getYear());
                });
    }

    @Order(4)
    @Test
    @Override
    @WithMockJwt
    public void upsertByState_NonRegisteredAndDateNull_ReturnsSalesTaxTracking() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto("USA", newState).withRegistered(null)
                .withRegistrationDate(null);

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", salesTaxTrackingDto.country())
                        .queryParam("state", salesTaxTrackingDto.state().abbreviation())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectBody(SalesTaxTrackingDto.class)
                .value(resultSalesTaxTrackingDto -> assertNull(resultSalesTaxTrackingDto.registrationDate()));
    }

    @Order(4)
    @Test
    @Override
    @WithMockJwt
    public void upsertByState_NonRegisteredAndDate_Returns400() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto("USA", newState).withRegistered(null)
                .withRegistrationDate(LocalDateTime.now());

        // Then
        webTestClient
                .mutateWith(csrf())

                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", salesTaxTrackingDto.country())
                        .queryParam("state", salesTaxTrackingDto.state().abbreviation())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertTrue(map.get("message").toString().contains(GenericErrorMessages.CONFLICTED_REGISTERED_ERROR)));
    }

    @Order(4)
    @Test
    @WithMockJwt
    public void patch_PatchesRegistered_ReturnsPatchedResource() {
        RegisteredType registered = RegisteredType.REGISTERED;
        String state = "NJ";
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("registered", registered);
        }};

        webTestClient
                .mutateWith(csrf())

                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", state)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(map)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(returnedSalesTaxTrackingDto -> {
                    assertEquals(returnedSalesTaxTrackingDto.registered(), RegisteredTypeDto.REGISTERED);
                    assertNotNull(returnedSalesTaxTrackingDto.registrationDate());
                });
    }

    @Order(4)
    @Test
    @WithMockJwt
    public void patch_PatchesFilingFrequency_ReturnsPatchedResource() {
        FilingFrequencyDto filingFrequencyDto = FilingFrequencyDto.MONTHLY;
        String state = "NJ";
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("filingFrequency", filingFrequencyDto);
        }};

        webTestClient
                .mutateWith(csrf())

                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", state)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(map)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(returnedSalesTaxTrackingDto -> {
                    assertEquals(returnedSalesTaxTrackingDto.filingFrequency(), FilingFrequencyDto.MONTHLY);
                    assertNotNull(returnedSalesTaxTrackingDto.filingFrequency());
                });
    }

    @Order(4)
    @Test
    public void patch_PatchesFilingFrequency_ReturnsError() {
        String invalidFilingFrequency = "invalidFilingFrequency";
        String state = "NJ";
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("filingFrequency", invalidFilingFrequency);
        }};

        webTestClient
                .mutateWith(csrf())

                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .queryParam("state", state)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(map)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Order(7)
    @Test
    @Override
    @WithMockJwt
    public void upsertByState_WithDefaultAppliedDateAndPhysicalTrue_ShouldUpdateAppliedDate() {
        LocalDateTime appliedDate = EconomicNexusTracker.DEFAULT_ESTABLISHED_DATE;
        LocalDateTime physicalEstablishedDate = LocalDateTime.now().minusYears(1);

        PhysicalNexusTrackerDto physicalNexusTrackerDto = new PhysicalNexusTrackerDto(true, physicalEstablishedDate);

        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto("USA", new StateDto("AL", "11", "AL"))
                .withPhysicalNexusTracker(physicalNexusTrackerDto)
                .withAppliedDate(appliedDate); // AppliedDate is default, should be updated

        // Then
        webTestClient
                .mutateWith(csrf())

                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", "AL")
                        .queryParam("country", usaCountry)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectBody(SalesTaxTrackingDto.class)
                .value(resultSalesTaxTrackingDto -> {
                    assertTrue(resultSalesTaxTrackingDto.physicalNexusTracker().established());
                    LocalDateTime physicalEstablishedDateResult = resultSalesTaxTrackingDto.physicalNexusTracker().establishedDate();
                    assertEquals(physicalEstablishedDate, physicalEstablishedDateResult);
                    assertEquals(physicalEstablishedDate, resultSalesTaxTrackingDto.appliedDate(), "AppliedDate should match the physicalDate");

                });
    }

    @Order(7)
    @Test
    @Override
    @WithMockJwt
    public void upsertByState_WithFutureAppliedDateAndPhysicalTrue_ShouldUpdateAppliedDate() {
        LocalDateTime appliedDate = LocalDateTime.now().plusYears(1);
        LocalDateTime physicalEstablishedDate = LocalDateTime.now().plusDays(1);

        PhysicalNexusTrackerDto physicalNexusTrackerDto = new PhysicalNexusTrackerDto(true, physicalEstablishedDate);

        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto("USA", new StateDto("AL", "11", "AL"))
                .withPhysicalNexusTracker(physicalNexusTrackerDto)
                .withAppliedDate(appliedDate); // Not Default in the future, will be updated

        // Then
        webTestClient
                .mutateWith(csrf())

                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", "AL")
                        .queryParam("country", usaCountry)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectBody(SalesTaxTrackingDto.class)
                .value(resultSalesTaxTrackingDto -> {
                    assertTrue(resultSalesTaxTrackingDto.physicalNexusTracker().established());

                    LocalDateTime physicalEstablishedDateResult = resultSalesTaxTrackingDto.physicalNexusTracker().establishedDate();
                    assertEquals(physicalEstablishedDate, physicalEstablishedDateResult);
                    assertEquals(physicalEstablishedDate, resultSalesTaxTrackingDto.appliedDate(), "AppliedDate should match the physicalDate");
                });
    }

    @Order(7)
    @Test
    @Override
    @WithMockJwt
    public void upsertByState_WithPastAppliedDateAndPhysicalTrue_ShouldNotUpdateAppliedDate() {
        LocalDateTime appliedDate = LocalDateTime.now().minusYears(2).minusMonths(2);
        LocalDateTime physicalEstablishedDate = LocalDateTime.now().plusDays(1);

        PhysicalNexusTrackerDto physicalNexusTrackerDto = new PhysicalNexusTrackerDto(true, physicalEstablishedDate);

        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto("USA", new StateDto("AL", "11", "AL"))
                .withPhysicalNexusTracker(physicalNexusTrackerDto)
                .withAppliedDate(appliedDate); // Not default in the past, should NOT be updated

        // Then
        webTestClient
                .mutateWith(csrf())

                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", "AL")
                        .queryParam("country", usaCountry)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectBody(SalesTaxTrackingDto.class)
                .value(resultSalesTaxTrackingDto -> {
                    assertTrue(resultSalesTaxTrackingDto.physicalNexusTracker().established());

                    LocalDateTime physicalEstablishedDateResult = resultSalesTaxTrackingDto.physicalNexusTracker().establishedDate();
                    assertEquals(physicalEstablishedDate, physicalEstablishedDateResult);
                    assertEquals(appliedDate, resultSalesTaxTrackingDto.appliedDate(), "AppliedDate shouldn't be changed");
                });
    }

    @Order(8)
    @Test
    @Override
    @WithMockJwt
    public void upsertByState_WithPhysicalTrueAndEconomicNexusInFuture_ShouldNotUpdateAppliedDate() {
        LocalDateTime appliedDate = LocalDateTime.now().minusYears(5);
        LocalDateTime physicalEstablishedDate = LocalDateTime.now().plusDays(1);

        PhysicalNexusTrackerDto physicalNexusTrackerDto = new PhysicalNexusTrackerDto(true, physicalEstablishedDate);

        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto("USA", new StateDto("AL", "11", "Alabama"))
                .withPhysicalNexusTracker(physicalNexusTrackerDto)
                .withAppliedDate(appliedDate); // in the past of both economic, physical

        // put Physical True SalesTaxTracking
        webTestClient
                .mutateWith(csrf())

                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", "AL")
                        .queryParam("country", usaCountry)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange();

        String externalId = "newNonExistingTransactionID2";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true);

        givenTransaction = givenTransaction.withShippingAddress(givenTransaction.shippingAddress().withState("AL")) // salesTaxTracking is approved and physical
                .withItems(List.of(givenTransaction.items().get(0).withTotalPrice(BigDecimal.valueOf(1000000))))
                .withExternalTimestamps(givenTransaction.externalTimestamps().withCreatedDate(LocalDateTime.now().toString()));  // Should passedNexus

        // Upsert Transaction Which PassedNexus
        webTestClient
                .mutateWith(csrf())

                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + "1" + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(TransactionDto.class);

        // Economic, Physical True, AppliedDate shouldn't be updated
        webTestClient
                .mutateWith(csrf())

                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", "AL")
                        .queryParam("country", usaCountry)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(SalesTaxTrackingDto.class)
                .value(resultSalesTaxTrackingDto -> {
                    assertTrue(resultSalesTaxTrackingDto.physicalNexusTracker().established());
                    assertTrue(resultSalesTaxTrackingDto.economicNexusTracker().established());

                    LocalDateTime physicalEstablishedDateResult = resultSalesTaxTrackingDto.physicalNexusTracker().establishedDate();
                    assertEquals(physicalEstablishedDate.toLocalDate(), physicalEstablishedDateResult.toLocalDate());
                    assertEquals(appliedDate.toLocalDate(), resultSalesTaxTrackingDto.appliedDate().toLocalDate(), "AppliedDate should not be changed"); // Economic Didn't update the appliedDate
                });
    }

    @Order(8)
    @Test
    @Override
    @WithMockJwt
    public void upsertByState_WithPhysicalTrueAndPassedNexusByEconomic_ShouldUpdateAppliedDate() {
        LocalDateTime appliedDate = EconomicNexusTracker.DEFAULT_ESTABLISHED_DATE;
        LocalDateTime physicalEstablishedDate = LocalDateTime.now().plusYears(10); // In the future

        PhysicalNexusTrackerDto physicalNexusTrackerDto = new PhysicalNexusTrackerDto(true, physicalEstablishedDate);

        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto("USA", new StateDto("AL", "11", "Alabama"))
                .withPhysicalNexusTracker(physicalNexusTrackerDto)
                .withAppliedDate(appliedDate); // Default Should be updated, physical True in the future

        // Then
        webTestClient
                .mutateWith(csrf())

                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", "AL")
                        .queryParam("country", usaCountry)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectBody(SalesTaxTrackingDto.class)
                .value(resultSalesTaxTrackingDto -> {
                    assertFalse(resultSalesTaxTrackingDto.economicNexusTracker().established()); // Not EconomicNexus
                    assertTrue(resultSalesTaxTrackingDto.physicalNexusTracker().established());
                });


        // Upsert Transaction Which PassedNexus
        String externalId = "newNonExistingTransactionID3";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true);

        givenTransaction = givenTransaction.withShippingAddress(givenTransaction.shippingAddress().withState("AL"))
                .withItems(List.of(givenTransaction.items().get(0).withTotalPrice(BigDecimal.valueOf(1000000))))
                .withExternalTimestamps(givenTransaction.externalTimestamps().withCreatedDate(LocalDateTime.now().toString()));  // Should passedNexus

        webTestClient
                .mutateWith(csrf())

                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + "1" + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(TransactionDto.class);

        webTestClient
                .mutateWith(csrf())

                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", "AL")
                        .queryParam("country", usaCountry)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(SalesTaxTrackingDto.class)
                .value(resultSalesTaxTrackingDto -> {
                    assertTrue(resultSalesTaxTrackingDto.economicNexusTracker().established());
                    // Shouldn't Be physical, should be  matching the economicNexus date
                    assertEquals(resultSalesTaxTrackingDto.economicNexusTracker().establishedDate(), resultSalesTaxTrackingDto.appliedDate(), "appliedDate should match the economicEstablishedDate");
                });
    }

    @Order(0)
    @Test
    @WithMockJwt
    public void refreshByState_WithCancelledTransaction_ShouldDoNothing() {
        // Given
        String state = "AL";

        // Setup SalesTaxTracking with economicNexus false
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto("USA", new StateDto("AL", "11", "Alabama"));

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", state)
                        .queryParam("country", usaCountry)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange();

        // Upsert CANCELLED transaction
        String externalId = "cancelledTransaction123";

        TransactionDto cancelledTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true)
                .withTransactionStatus(TransactionStatusDto.CANCELLED); // CANCELLED

        cancelledTransaction = cancelledTransaction.withShippingAddress(cancelledTransaction.shippingAddress().withState("AL"))
                .withItems(List.of(cancelledTransaction.items().get(0).withTotalPrice(BigDecimal.valueOf(1000000))))
                .withExternalTimestamps(cancelledTransaction.externalTimestamps().withCreatedDate(LocalDateTime.now().toString()));  // Should passedNexus

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/1/externalId/" + externalId)
                        .build())
                .bodyValue(cancelledTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();

        // When: Refresh is triggered
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh")
                        .queryParam("country", usaCountry)
                        .queryParam("state", state)
                        .queryParam("date", LocalDate.now())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(result -> {
                    System.out.println("RESULT");
                    System.out.println(salesTaxTrackingDto);
                    assertFalse(result.economicNexusTracker().established(), "Economic nexus should remain false");
                    assertNull(result.nexusCalculationSummaries().get(LocalDate.now()), "No summary should be added for cancelled transaction");
                });
    }
}