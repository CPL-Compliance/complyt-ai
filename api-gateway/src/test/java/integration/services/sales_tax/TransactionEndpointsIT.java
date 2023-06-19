package integration.services.sales_tax;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import io.complyt.apigateway.ApiGatewayApplication;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.LinkedHashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@SpringBootTest(classes = ApiGatewayApplication.class
        , webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
        , properties = {"server.port=8765", "management.server.port=8765"}
)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(profiles = {"integration-test", "stubFastTax"})
public class TransactionEndpointsIT extends TestContainersInitializerIT implements TransactionEndpointsITTemplate {

    private final String source = "1";
    private final String customerId = "4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5";

    @Autowired
    private WebTestClient webTestClient;

    // Given
    @Order(-1)
    @Test
    public void checkConnection() {
        while (!IS_SALES_TAX_REGISTERED) {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(TestUtilities.TRANSACTION_BASE_URL)
                            .build())
                    .headers(headers -> headers
                            .setBearerAuth(TOKEN))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().value(status -> IS_SALES_TAX_REGISTERED = status != 503);
        }

        while (!IS_SALES_TAX_RATES_REGISTERED) {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(TestUtilities.SALES_TAX_RATES_BASE_URL)
                            .build())
                    .headers(headers -> headers
                            .setBearerAuth(TOKEN))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().value(status -> IS_SALES_TAX_RATES_REGISTERED = status != 503);
        }
    }


    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntExistsAndCustomerDoesntExists_Returns404() {
        String externalId = "10001";

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.transactionJsonExample(externalId, TestUtilities.NON_EXISTING_COMPLYT_ID))
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

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/10002")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.transactionJsonExample(externalId, TestUtilities.NON_EXISTING_COMPLYT_ID))
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
        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.transactionJsonExampleWithState(externalId, customerId, nonExistingState))
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

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.transactionJsonExampleWithState(externalId, customerId, nonExistingState))
                .exchange()
                .expectStatus().is5xxServerError();

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getAllBySource_Exists_Returns200() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/2")
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(String.class)
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
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/9")
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
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
    public void getAll_Exists_Returns200() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LinkedHashMap.class)
                .value(list -> assertTrue(list.size() > 100));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByAll_DoesntExists_Returns200EmptyList() {

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN_DIFFERENT_TENANT))
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
    public void getByExternalIdAndSource_Exists_Returns200() {
        //Given
        String externalId = "10002";
        UUID complytId = UUID.fromString("a6469aaf-e838-41df-8106-6a8927917985"); // complytId of existing transaction

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.complytId")
                .isEqualTo(complytId.toString());
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByExternalIdAndSource_DoesntExists_Returns404() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/notExisting")
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
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

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId))
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

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId))
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

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.existingTransactionJsonExample(externalId, customerId, TestUtilities.NON_EXISTING_COMPLYT_ID))
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

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + differentSource + "/externalId/" + externalId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId))
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

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + differentExternalId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId))
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

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(TestUtilities.unvalidatedTransactionJsonExample(externalId, customerId))
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    String[] errors = message.substring(1, message.length() - 1).split(", ");
                    assertEquals(2, errors.length);
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
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").exists();
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
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
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
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.transactionStatus")
                .isEqualTo("CANCELLED");
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
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/notExisting")
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
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
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/complytId/" + complytId)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
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
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/complytId/" + TestUtilities.NON_EXISTING_COMPLYT_ID)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
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
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/complytId/notExisting")
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}