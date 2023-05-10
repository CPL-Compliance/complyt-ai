package integration.endpoints;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.models.MandatoryAddressDto;
import com.complyt.v1.models.TransactionDto;
import com.complyt.v1.models.TransactionStatusDto;
import com.complyt.v1.models.timestamps.TimestampsDto;
import com.complyt.v1.routers.TransactionRouter;
import integration.TestContainersInitializerIT;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import testUtils.integration_test.ITUtilities;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = SalesTaxApplication.class
        , webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
        , properties = {"server.port=9898", "management.server.port=9898"}
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(profiles = {"integration-test", "stubFastTax"})
public class TransactionEndpointsIT extends TestContainersInitializerIT implements TransactionEndpointsITTemplate {

    @MockBean
    TenantResolver tenantResolver;
    @MockBean
    JwtDecoder jwtDecoder;

    // Given

    private WebTestClient webTestClient;
    private UUID customerId = UUID.fromString("4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5"); // complytId of an existing customer in the database
    private MandatoryAddressDto referenceAddress = new MandatoryAddressDto("Phoenix", "US", null, "AZ", "3400 E Sky Harbor Blvd", "85034");
    private String source = "1";

    @BeforeEach
    void setup() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));
        webTestClient = WebTestClient.bindToServer().baseUrl(String.format(
                "http://%s:%d/",
                API_GATEWAY_CONTAINER.getHost(),
                API_GATEWAY_CONTAINER.getFirstMappedPort()
        )).build();
    }

    @Order(-1)
    @Test
    public void checkConnection() {
        while (isServiceRouted) {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/customers")
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().value(status -> isServiceRouted = status == 503);
        }
    }


    @Order(2)
    @Test
    @Override
    @WithMockUser(authorities = {"SCOPE_create:transaction"})
    public void upsertByExternalIdAndSource_DoesntExistsAndCustomerDoesntExists_Returns404() {
        String externalId = "10001";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, UUID.fromString(ITUtilities.NON_EXISTING_COMPLYT_ID))
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
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
    @WithMockUser(authorities = {"SCOPE_create:transaction"})
    public void upsertByExternalIdAndSource_ExistsAndCustomerDoesntExists_Returns404() {
        // Given
        String externalId = "10002";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, UUID.fromString(ITUtilities.NON_EXISTING_COMPLYT_ID))
                .withShippingAddress(referenceAddress);
        // Then
        webTestClient
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
    @WithMockUser(authorities = {"SCOPE_create:transaction"})
    public void upsertByExternalIdAndSource_DoesntExistsAndSaleTaxTrackingDoesntExists_Returns500() {
        // Given
        String externalId = "10003";
        String nonExistingState = "Nilfgaard";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(referenceAddress.withState(nonExistingState));
        // Then
        webTestClient
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
    @WithMockUser(authorities = {"SCOPE_create:transaction", "SCOPE_read:transaction"})
    public void upsertByExternalIdAndSource_ExistsAndSaleTaxTrackingDoesntExists_Returns500() {
        // Given
        String externalId = "10002";
        String nonExistingState = "Nilfgaard";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(referenceAddress.withState(nonExistingState));
        // Then
        webTestClient
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
    @WithMockUser(authorities = {"SCOPE_read:transaction"})
    public void getAllBySource_Exists_Returns200() {
        Integer temp = API_GATEWAY_CONTAINER.getMappedPort(8765);
        String temp2 = API_GATEWAY_CONTAINER.getHost();
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/2")
                        .build())
//                .uri(uriBuilder -> uriBuilder
//                        .host("localhost").port(API_GATEWAY_CONTAINER.getMappedPort(8765))
//                        .host("localdfgshost").port(12344)
//                        .path(CustomerRouter.BASE_URL + "/source/2")
//                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> assertEquals(list.size(), 1));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser(authorities = {"SCOPE_read:transaction"})
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
    @WithMockUser(authorities = {"SCOPE_read:transaction"})
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
    @WithMockUser(authorities = {"SCOPE_read:transaction"})
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
    @WithMockUser(authorities = {"SCOPE_read:transaction"})
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
                .value(transaction -> {
                    assertEquals(transaction.complytId(), complytId);
                    assertEquals(transaction.complytId(), complytId);
                });
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser(authorities = {"SCOPE_create:transaction", "SCOPE_read:transaction"})
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
    @WithMockUser(authorities = {"SCOPE_create:transaction", "SCOPE_read:transaction"})
    public void upsertByExternalIdAndSource_Exists_Returns200() {
        //Given
        String externalId = "10004";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
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
    @WithMockUser(authorities = {"SCOPE_create:transaction", "SCOPE_read:transaction"})
    public void upsertByExternalIdAndSource_DoesntExists_Returns201() {
        //Given
        String externalId = "10004";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
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
    @WithMockUser(authorities = {"SCOPE_create:transaction", "SCOPE_read:transaction"})
    public void upsertByExternalIdAndSource_DoesntExistsWithComplytId_Returns400ConflictedData() {
        // Given
        String externalId = "10005";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withComplytId(UUID.randomUUID())
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
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
    @WithMockUser(authorities = {"SCOPE_create:transaction", "SCOPE_read:transaction"})
    public void upsertByExternalIdAndSource_ConflictingSource_Returns400ConflictedData() {
        // Given
        String externalId = "10005";
        String differentSource = "2";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto("10005", customerId)
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
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
    @WithMockUser(authorities = {"SCOPE_update:transaction", "SCOPE_read:transaction"})
    public void upsertByExternalIdAndSource_ConflictingExternalId_Returns400ConflictedData() {
        // Given
        String externalId = "someId";
        String differentExternalId = "differentId";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withComplytId(UUID.randomUUID())
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
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
    @WithMockUser(authorities = {"SCOPE_create:transaction","SCOPE_update:transaction", "SCOPE_read:transaction"})
    public void upsertByExternalIdAndSource_DoesntPassValidation_Returns400CValidationError() {
        // Given
        String externalId = "someId";
        String differentExternalId = "differentId";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withInternalTimestamps(new TimestampsDto("", "2021-10-10T07:00:00"))
                .withSource("")
                .withShippingAddress(referenceAddress.withCity(""));
        Set expectedErrors = new HashSet<>(List.of(
                "Timestamps.createdDate " + DtoErrorMessages.DATE_FORMAT_ERROR,
                "source " + StringErrorMessages.SINGLE_DIGIT_ERROR,
                "Address.city " + StringErrorMessages.MINMAX_100_ERROR));

        // Then
        webTestClient
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
    @WithMockUser(authorities = {"SCOPE_create:transaction", "SCOPE_read:transaction"})
    public void upsertByExternalIdAndSource_NoBody_Returns400() {
        // Given
        String externalId = "0";

        // Then
        webTestClient
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
    @WithMockUser(authorities = {"SCOPE_delete:transaction"})
    public void deleteByExternalIdAndSource_Exists_Returns204() {
        // Given
        String externalId = "10004";

        // Then
        webTestClient
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
    @WithMockUser(authorities = {"SCOPE_create:transaction", "SCOPE_read:transaction"})
    public void get_checkDeletion_Returns200() {
        // Given
        String externalId = "10004";

        // Then
        webTestClient
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
    @WithMockUser(authorities = {"SCOPE_delete:transaction", "SCOPE_read:transaction"})
    public void deleteByExternalIdAndSource_DoesntExists_Returns404() {
        webTestClient
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
    @WithMockUser(authorities = {"SCOPE_create:transaction", "SCOPE_read:transaction"})
    public void getByComplytId_Exists_Returns200() {
        // Given
        String complytId = "88d951b8-4804-4bef-929a-cfd3670a82fa"; // complytId of existing transaction

        // Then
        webTestClient
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
    @WithMockUser(authorities = {"SCOPE_create:transaction", "SCOPE_read:transaction"})
    public void getByComplytId_DoesntExists_Returns404() {
        // Then
        webTestClient
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
    @WithMockUser(authorities = {"SCOPE_create:transaction", "SCOPE_read:transaction"})
    public void getByComplytId_complytIdDoesntParse_Returns500() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/complytId/notExisting")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
