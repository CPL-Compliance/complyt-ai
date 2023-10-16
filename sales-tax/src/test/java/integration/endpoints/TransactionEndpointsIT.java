package integration.endpoints;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.TimestampsDto;
import com.complyt.v1.models.transaction.MandatoryAddressDto;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import testUtils.integration_test.ITUtilities;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionEndpointsIT extends TestContainersInitializerIT implements TransactionEndpointsITTemplate {

    @MockBean
    TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    // Given
    private final UUID customerId = UUID.fromString("4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5"); // complytId of an existing customer in the database
    private final MandatoryAddressDto referenceAddress = new MandatoryAddressDto("Phoenix", "US", null, "AZ", "3400 E Sky Harbor Blvd", "85034", false);
    private final String source = "1";

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
//        registry.add("spring.data.mongodb.uri", () -> "mongodb://localhost:27017/sales_tax");
    }

    @BeforeEach
    void setup() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));
    }

//    @Order(1)
//    @Test
//    @WithMockUser
//    public void refreshTest() {
//        String state = "MN";
//
//        // Then
//        webTestClient
//                .mutateWith(csrf())
//                .mutate().responseTimeout(Duration.ofMinutes(2)).build()
//                .post()
//                .uri(uriBuilder -> uriBuilder
//                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh/state/" + state + "/date/" + LocalDateTime.now().toLocalDate())
//                        .build())
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk().expectBody(SalesTaxTrackingDto.class)
//                .value(salesTaxTrackingDto -> {
//                    LOGGER.info(String.valueOf(salesTaxTrackingDto));
//                });
//
//    }

//    @Order(1)
//    @Test
//    @WithMockUser
//    public void refreshTest2() {
//
//        webTestClient
//                .mutate().responseTimeout(Duration.ofMinutes(2)).build()
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(SalesTaxTrackingRouter.BASE_URL)
//                        .build())
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk().expectBodyList(SalesTaxTrackingDto.class)
//                .value(salesTaxTrackingDtoList -> {
//                    List<LocalDate> localDateList = List.of(
//                            LocalDate.of(2020, 9, 30),
//                            LocalDate.of(2021, 9, 30),
//                            LocalDate.of(2022, 9, 30),
//                            LocalDate.of(2023, 9, 30));
//                    for (LocalDate localDate : localDateList) {
//                        for (SalesTaxTrackingDto salesTaxTrackingDto : salesTaxTrackingDtoList) {
//                            String state = salesTaxTrackingDto.state().abbreviation();
//
//                            webTestClient
//                                    .mutateWith(csrf())
//                                    .mutate().responseTimeout(Duration.ofMinutes(2)).build()
//                                    .post()
//                                    .uri(uriBuilder -> uriBuilder
//                                            .path(SalesTaxTrackingRouter.BASE_URL + "/refresh/state/" + state + "/date/" + localDate)
//                                            .build())
//                                    .accept(MediaType.APPLICATION_JSON)
//                                    .exchange()
//                                    .expectStatus().isOk().expectBody(SalesTaxTrackingDto.class)
//                                    .value(givebSalesTaxTrackingDto -> {
//                                        LOGGER.info(String.valueOf(givebSalesTaxTrackingDto));
//                                    });
//                        }
//                    }
//                });
//
//        while (true) ;
//    }


    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntExistsAndCustomerDoesntExists_Returns404() {
        String externalId = "10001";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, UUID.fromString(ITUtilities.NON_EXISTING_COMPLYT_ID))
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_ExistsAndCustomerDoesntExists_Returns404() {
        // Given
        String externalId = "10002";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, UUID.fromString(ITUtilities.NON_EXISTING_COMPLYT_ID))
                .withShippingAddress(referenceAddress);
        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/10002")
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntExistsAndSaleTaxTrackingDoesntExists_Returns500() {
        // Given
        String externalId = "10003";
        String nonExistingState = "Nilfgaard";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(referenceAddress.withState(nonExistingState));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_ExistsAndSaleTaxTrackingDoesntExists_Returns500() {
        // Given
        String externalId = "10002";
        String nonExistingState = "Nilfgaard";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(referenceAddress.withState(nonExistingState));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> assertEquals(transactionDto.shippingAddress().state(), "AZ"));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getAllBySource_Exists_Returns200() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/2")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> assertEquals(list.size(), 1));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getAllBySource_DoesntExists_Returns200EmptyList() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/9")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> assertEquals(list.size(), 0));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getAll_Exists_Returns200() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> assertTrue(list.size() > 100));
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
                        .path(TransactionRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> assertEquals(list.size(), 0));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByExternalIdAndSource_Exists_Returns200() {
        //Given
        String externalId = "10002";
        UUID complytId = UUID.fromString("a6469aaf-e838-41df-8106-6a8927917985"); // complytId of existing transaction

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transaction -> assertEquals(transaction.complytId(), complytId));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByExternalIdAndSource_DoesntExists_Returns404() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/notExisting")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(3)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_Exists_Returns200() {
        //Given
        String externalId = "10004";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntExists_Returns201() {
        //Given
        String externalId = "10004";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
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
        String externalId = "10005";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withComplytId(UUID.randomUUID())
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_ConflictingSource_Returns400ConflictedData() {
        // Given
        String externalId = "10005";
        String differentSource = "2";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto("10005", customerId)
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + differentSource + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
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
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withComplytId(UUID.randomUUID())
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + differentExternalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntPassValidation_Returns400CValidationError() {
        // Given
        String externalId = "someId";
        String differentExternalId = "differentId";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withInternalTimestamps(new TimestampsDto("", "2021-10-10T07:00:00"))
                .withSource("");
        Set expectedErrors = new HashSet<>(List.of(
                "Timestamps.createdDate " + DtoErrorMessages.DATE_FORMAT_ERROR,
                "source " + StringErrorMessages.SINGLE_DIGIT_ERROR));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
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

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_NoBody_Returns400() {
        // Given
        String externalId = "0";

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.MISSING_BODY_ERROR, map.get("message")));
    }

    @Order(4)
    @Test
    @Override
    @WithMockUser
    public void deleteByExternalIdAndSource_Exists_Returns204() {
        // Given
        String externalId = "10004";

        // Then
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Order(5)
    @Test
    @Override
    @WithMockUser
    public void get_checkDeletion_Returns200() {
        // Given
        String externalId = "10004";

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class).value(transactionDto ->
                        assertEquals(transactionDto.transactionStatus(), TransactionStatusDto.CANCELLED));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void deleteByExternalIdAndSource_DoesntExists_Returns404() {
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/notExisting")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByComplytId_Exists_Returns200() {
        // Given
        String complytId = "88d951b8-4804-4bef-929a-cfd3670a82fa"; // complytId of existing transaction

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Order(3)
    @Test
    @Override
    @WithMockUser
    public void getByComplytId_DoesntExists_Returns404() {
        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/complytId/" + ITUtilities.NON_EXISTING_COMPLYT_ID)
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
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/complytId/notExisting")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Order(0)
    @Test
    @Override
    @WithMockUser
    /*
     This transaction's customer has an exemption in state PA with validation dates of:
     fromDate: 2025-01-01, toDate: 26-01-01, therefore transaction is sales-tax exempt
    */
    public void upsertByExternalIdAndSource_CustomerIsExemptByStateAndDate_ReturnsNonTaxableTransaction() {
        String externalId = "nonExistingTransactionID";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new MandatoryAddressDto("fresno", "US", null, "PA", "st", "12345", false))
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> assertNull(transactionDto.salesTax()));
    }

    @Order(0)
    @Test
    @Override
    @WithMockUser
    /*
     This transaction's customer has an exemption in state PA with validation dates of:
     fromDate: 2025-01-01, toDate: 26-01-01, therefore transaction is NOT sales-tax exempt
    */
    public void upsertByExternalIdAndSource_CustomerIsNotExemptByStateAndDate_ReturnsTaxableTransaction() {
        String externalId = "anotherNonExistingTransactionID";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new MandatoryAddressDto("fresno", "US", null, "PA", "st", "12345", false))
                .withExternalTimestamps(new TimestampsDto("2024-01-02", "2025-01-02"));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> assertNotNull(transactionDto.salesTax()));
    }

    @Order(0)
    @Test
    @Override
    @WithMockUser
    /*
     This transaction's customer has an exemption in state FL with Exemption Status - CANCELLED,
     therefore transaction is NOT sales-tax exempt
    */
    public void upsertByExternalIdAndSource_CustomerIsNotExemptBecauseExemptionIsCancelled_ReturnsTaxableTransaction() {
        String externalId = "ThirdNonExistingIdForExemptionChecks";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new MandatoryAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "99801", false))
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> assertNotNull(transactionDto.salesTax()));
    }

}