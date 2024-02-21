package integration.endpoints;

import com.complyt.SalesTaxApplication;
import com.complyt.domain.transaction.Transaction;
import com.complyt.repositories.Constants.RepositoryConstant;
import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.models.TimestampsDto;
import com.complyt.v1.models.transaction.ItemDto;
import com.complyt.v1.models.transaction.MandatoryAddressDto;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.models.transaction.TransactionStatusDto;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.function.IntUnaryOperator;

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
    }

    @BeforeEach
    void setup() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));
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
    public void getAllBySource_QueryParamInvalid_Returns400() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getAllBySource_PathVariableInvalid_Returns400() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/1")
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
                .value(list -> assertEquals(RepositoryConstant.DEFAULT_PAGE_SIZE, list.size()));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getAll_QueryParamInvalid_Returns400() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
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
    public void getByAll_QueryParamInvalid_Returns400() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
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
    public void getByExternalIdAndSource_PathVariableInvalid_Returns400() {
        String externalId = "null";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
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

    @Override
    public void upsertByExternalIdAndSource_PathVariableError_Returns400() {
        //Given
        String nullExternalId = "null";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(nullExternalId, customerId)
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + nullExternalId)
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
                "Timestamps.createdDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR,
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

    @Order(3)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_UnsupportedMediaType_Returns415() {
        // Given
        String externalId = "0";

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("Unsupported data")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.UNSUPPORTED_MEDIA_TYPE, map.get("message")));
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

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByComplytId_PathVariableInvalid_Returns400() {
        // Given
        String complytId = "null";

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
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


    @Order(0)
    @Test
    @Override
    @WithMockUser
    public void getAll_GetByParamSize_ReturnsExpectedSize() {
        int size = 1;
        String expectedComplyId = "6ee574bb-0300-4c74-9e4f-1852f234a028";
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL) // Set your API endpoint
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Transaction.class)
                .value(transactions -> Assertions.assertEquals(transactions.get(0).getComplytId().toString(), expectedComplyId))
                .hasSize(size);
    }

    @Order(0)
    @Test
    @Override
    @WithMockUser
    public void getAll_GetByParamPage_ReturnsExpectedPage() {
        int page = 2;

        String expectedComplyId = "134c9970-15f7-41e7-84a9-43073c955566";
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL) // Set your API endpoint
                        .queryParam("page", page)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Transaction.class)
                .value(transactions -> Assertions.assertEquals(transactions.get(0).getComplytId().toString(), expectedComplyId));
    }

    @Order(0)
    @Test
    @Override
    @WithMockUser
    public void getAll_GetByDefaultsSizeAndPage_ReturnsExpectedEntries() {
        String expectedComplyId = "6ee574bb-0300-4c74-9e4f-1852f234a028";

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL) // Set your API endpoint
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Transaction.class)
                .value(transactions -> Assertions.assertEquals(transactions.get(0).getComplytId().toString(), expectedComplyId))
                .value(transactions -> Assertions.assertTrue(transactions.size() <= RepositoryConstant.DEFAULT_PAGE_SIZE));
    }


    @Order(0)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_OneItemIsNegativeAmount_ReturnsTaxableTransaction() {
        // Given
        String externalId = "NonExistingIdNegativeAmount";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto(),
                        ITUtilities.stubItemDto().withUnitPrice(BigDecimal.valueOf(-100)).withTotalPrice(BigDecimal.valueOf(-100)))
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

    @Order(0)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_TransactionWithShippingFee_ReturnsTaxableTransactionWithShippingFeeAndItemsCalculatedTotal() {
        //Given
        String externalId = "NonExistingIdTransactionWithShipping";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto())
                .withShippingAddress(new MandatoryAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "99801", false))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
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
                .value(transactionDto -> {
                    assertEquals(ITUtilities.stubItemDto().totalPrice(), transactionDto.items().get(0).calculatedTotal());
                    assertEquals(ITUtilities.stubShippingFeeDto().totalPrice(), transactionDto.shippingFee().calculatedTotal());
                    assertNotNull(transactionDto.salesTax());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_ItemWithManualSalesTax_ReturnsTaxableTransactionWithCalculatedTotal() {
        //Given
        String externalId = "NonExistingIdTransactionWithManualSalesTax";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withManualSalesTax(true).withManualSalesTaxRate(new BigDecimal("0.2")))
                .withShippingAddress(new MandatoryAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "99801", false))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

        BigDecimal itemSalesTaxAmount = ITUtilities.stubItemDto().totalPrice().multiply(new BigDecimal("0.2"));

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
                .value(transactionDto -> {
                    assertEquals(ITUtilities.stubItemDto().totalPrice(), transactionDto.items().get(0).calculatedTotal());
                    assertEquals(itemSalesTaxAmount, transactionDto.salesTax().amount());
                    assertNotNull(transactionDto.salesTax());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_NoItemHasDiscount_ReturnsTaxableTransactionWithDiscountTotal0() {
        //Given
        String externalId = "NonExistingIdTransactionWithNoDiscount";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto())
                .withShippingAddress(new MandatoryAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "99801", false))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
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
                .value(transactionDto -> {
                    assertEquals(BigDecimal.ZERO, transactionDto.totalDiscount());
                    assertNotNull(transactionDto.salesTax());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_OneItemHasDiscount_ReturnsTaxableTransactionWithDiscountTotal() {
        //Given
        String externalId = "NonExistingIdTransactionOneItemHaveDiscount";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withDiscount(BigDecimal.valueOf(500)))
                .withShippingAddress(new MandatoryAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "99801", false))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
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
                .value(transactionDto -> {
                    assertEquals(BigDecimal.valueOf(500), transactionDto.totalDiscount());
                    assertNotNull(transactionDto.salesTax());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_TwoItemHaveDiscount_ReturnsTaxableTransactionWithDiscountTotal() {
        //Given
        String externalId = "NonExistingIdTransactionTwoItemsHaveDiscount";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withDiscount(BigDecimal.valueOf(500)),
                        ITUtilities.stubItemDto().withDiscount(BigDecimal.valueOf(700)))
                .withShippingAddress(new MandatoryAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "99801", false))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
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
                .value(transactionDto -> {
                    assertEquals(BigDecimal.valueOf(1200), transactionDto.totalDiscount());
                    assertNotNull(transactionDto.salesTax());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_OneItemHasDiscountOneItemIsNegative_ReturnsTaxableTransactionWithDiscount() {
        //Given
        String externalId = "NonExistingIdTransactionOneItemHaveDiscountOneNegative";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withDiscount(BigDecimal.valueOf(500)),
                        ITUtilities.stubItemDto().withUnitPrice(BigDecimal.valueOf(-800)).withTotalPrice(BigDecimal.valueOf(-800)))
                .withShippingAddress(new MandatoryAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "99801", false))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
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
                .value(transactionDto -> {
                    assertEquals(BigDecimal.valueOf(500), transactionDto.totalDiscount());
                    assertNotNull(transactionDto.salesTax());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_ItemUnitPriceAndQuantityNullAndTotalNotNull_ReturnsTaxableTransaction() {
        //Given
        String externalId = "NonExistingIdTransactionUnitPriceQuantityNull";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withQuantity(null).withUnitPrice(null))
                .withShippingAddress(new MandatoryAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "99801", false))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
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
                .value(transactionDto -> {
                    assertEquals(ITUtilities.stubItemDto().totalPrice(), transactionDto.totalItemsAmount());
                    assertNotNull(transactionDto.salesTax());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_ItemUnitPriceAndQuantityNotNullAndTotalNull_ReturnsTaxableTransaction() {
        //Given
        String externalId = "NonExistingIdTransactionItemUnitPriceAndQuantityNotNullAndTotalNull";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withTotalPrice(null))
                .withShippingAddress(new MandatoryAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "99801", false))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
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
                .value(transactionDto -> {
                    assertNotNull(transactionDto.salesTax());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_ItemHasNoUnitPriceAndQuantityAndTotal_Returns400ConflictedData() {
        //Given
        String externalId = "NonExistingIdTransactionItemHasNoUnitPriceAndQuantityAndTotal";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withTotalPrice(null).withQuantity(null).withUnitPrice(null))
                .withShippingAddress(new MandatoryAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "99801", false))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
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
                .expectStatus().isBadRequest();
    }
}