package integration.services.sales_tax;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;

import java.util.LinkedHashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionEndpointsIT extends TestContainersInitializerIT implements TransactionEndpointsITTemplate {

    private final String source = "1";
    private final String customerId = "4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5";


    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_DoesntExistsAndCustomerDoesntExists_Returns404() {
        String externalId = "10001";

        // Then
        WEB_TEST_CLIENT
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
    public void upsertByExternalIdAndSource_ExistsAndCustomerDoesntExists_Returns404() {
        // Given
        String externalId = "10002";

        // Then
        WEB_TEST_CLIENT
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

    public void upsertByExternalIdAndSource_DoesntExistsAndSaleTaxTrackingDoesntExists_Returns500() {
        // Given
        String externalId = "10003";
        String nonExistingState = "Nilfgaard";
        // Then
        WEB_TEST_CLIENT
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
    public void upsertByExternalIdAndSource_ExistsAndSaleTaxTrackingDoesntExists_Returns500() {
        // Given
        String externalId = "10002";
        String nonExistingState = "Nilfgaard";

        // Then
        WEB_TEST_CLIENT
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

        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_ConflictingTransactionAmountIsNegative_Returns400ConflictedData() {
        // Given
        String externalId = "10005";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.transactionItemIsNotAligned(externalId, customerId, null, true, null))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Override
    public void upsertByExternalIdAndSource_ConflictingTransactionItemTotalIsNotAligned_Returns400ConflictedData() {
        // Given
        String externalId = "10005";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.transactionTotalIsNegative(externalId, customerId, null, true, null))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Override
    public void put_NoAccessToken_Returns401() {
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Override
    public void get_NoAccessToken_Returns401() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL)
                        .build())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Override
    public void delete_NoAccessToken_Returns401() {
        WEB_TEST_CLIENT
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL)
                        .build())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Override
    public void put_InsufficientScopes_Returns403() {
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_NO_SCOPES);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isForbidden();
    }

    @Override
    public void get_InsufficientScopes_Returns403() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_NO_SCOPES);
                })
                .exchange()
                .expectStatus().isForbidden();
    }

    @Override
    public void delete_InsufficientScopes_Returns403() {
        WEB_TEST_CLIENT
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_NO_SCOPES);
                })
                .exchange()
                .expectStatus().isForbidden();
    }

    @Order(2)
    @Test
    @Override
    public void getAllBySource_Exists_Returns200() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/2")
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(String.class)
                .value(list -> assertEquals(list.size(), 1));
    }

    @Order(2)
    @Test
    @Override
    public void getAllBySource_DoesntExists_Returns200EmptyList() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/9")
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Order(2)
    @Test
    @Override
    public void getAll_Exists_Returns200() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LinkedHashMap.class)
                .value(list -> assertTrue(list.size() > 100));
    }

    @Order(2)
    @Test
    @Override
    public void getByAll_DoesntExists_Returns200EmptyList() {

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN_DIFFERENT_TENANT))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Order(2)
    @Test
    @Override
    public void getByExternalIdAndSource_Exists_Returns200() {
        //Given
        String externalId = "10002";
        UUID complytId = UUID.fromString("a6469aaf-e838-41df-8106-6a8927917985"); // complytId of existing transaction

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.complytId")
                .isEqualTo(complytId.toString());
    }

    @Order(2)
    @Test
    @Override
    public void getByExternalIdAndSource_DoesntExists_Returns404() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/notExisting")
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(3)
    @Test
    @Override
    public void upsertByExternalIdAndSource_Exists_Returns200() {
        //Given
        String externalId = "10004";

        // Then
        WEB_TEST_CLIENT
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
    public void upsertByExternalIdAndSource_DoesntExists_Returns201() {
        //Given
        String externalId = "10004";

        // Then
        WEB_TEST_CLIENT
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
    public void upsertByExternalIdAndSource_DoesntExistsWithComplytId_Returns400ConflictedData() {
        // Given
        String externalId = "10005";

        // Then
        WEB_TEST_CLIENT
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
    public void upsertByExternalIdAndSource_ConflictingSource_Returns400ConflictedData() {
        // Given
        String externalId = "10005";
        String differentSource = "2";

        // Then
        WEB_TEST_CLIENT
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
    public void upsertByExternalIdAndSource_ConflictingExternalId_Returns400ConflictedData() {
        // Given
        String externalId = "someId";
        String differentExternalId = "differentId";

        // Then
        WEB_TEST_CLIENT
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
    public void upsertByExternalIdAndSource_DoesntPassValidation_Returns400CValidationError() {
        // Given
        String externalId = "someId";
        String differentExternalId = "differentId";

        // Then
        WEB_TEST_CLIENT
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
    public void upsertByExternalIdAndSource_NoBody_Returns400() {
        // Given
        String externalId = "0";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").exists();
    }

    @Order(4)
    @Test
    @Override
    public void deleteByExternalIdAndSource_Exists_Returns204() {
        // Given
        String externalId = "10004";

        // Then
        WEB_TEST_CLIENT
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Order(5)
    @Test
    @Override
    public void get_checkDeletion_Returns200() {
        // Given
        String externalId = "10004";

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.transactionStatus")
                .isEqualTo("CANCELLED");
    }

    @Order(2)
    @Test
    @Override
    public void deleteByExternalIdAndSource_DoesntExists_Returns404() {
        WEB_TEST_CLIENT
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/notExisting")
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(2)
    @Test
    @Override
    public void getByComplytId_Exists_Returns200() {
        // Given
        String complytId = "88d951b8-4804-4bef-929a-cfd3670a82fa"; // complytId of existing transaction

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/complytId/" + complytId)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isOk();
    }

    @Order(3)
    @Test
    @Override
    public void getByComplytId_DoesntExists_Returns404() {
        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/complytId/" + TestUtilities.NON_EXISTING_COMPLYT_ID)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(2)
    @Test
    @Override
    public void getByComplytId_complytIdDoesntParse_Returns500() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/complytId/notExisting")
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().is5xxServerError();
    }
}