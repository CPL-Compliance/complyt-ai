package integration.services.sales_tax;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import integration.test_utils.templates.endpoints.RepositoryConstant;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionEndpointsIT extends TestContainersInitializerIT implements TransactionEndpointsITTemplate {

    private final String source = "1";
    private final String state = "CA";
    private final String createdDate = "2023-02-05";
    private final String exemptedState = "PA";
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
                .bodyValue(TestUtilities.transactionJsonExample(externalId, TestUtilities.NON_EXISTING_COMPLYT_ID, state, createdDate))
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
                .bodyValue(TestUtilities.transactionJsonExample(externalId, TestUtilities.NON_EXISTING_COMPLYT_ID, state, createdDate))
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
                .bodyValue(TestUtilities.transactionJsonExampleWithState(externalId, customerId, nonExistingState, createdDate))
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
                .bodyValue(TestUtilities.transactionJsonExampleWithState(externalId, customerId, nonExistingState, createdDate ))
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

    @Order(2)
    @Test
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

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_TransactionWithShippingFee_Returns200() {
        //Given
        String externalId = "10005";
        String item = TestUtilities.customItem(BigDecimal.valueOf(8000), BigDecimal.valueOf(2), BigDecimal.valueOf(4000), null);

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
                .bodyValue(TestUtilities.transactionWithCustomItems(externalId, customerId, null, true, null, item))
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_ItemWithManualSalesTax_Returns200() {
        //Given
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
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId, "CA", createdDate))
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_NoItemHasDiscount_Returns200() {
        //Given
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
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId, "CA", createdDate))
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_OneItemHasDiscount_Returns200() {
        //Given
        String externalId = "10005";
        String item = TestUtilities.customItem(BigDecimal.valueOf(8000), BigDecimal.valueOf(2), BigDecimal.valueOf(4000), BigDecimal.valueOf(500));

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
                .bodyValue(TestUtilities.transactionWithCustomItems(externalId, customerId, null, true, null, item))
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_TwoItemHaveDiscount_Returns200() {
        //Given
        String externalId = "10005";
        String item1 = TestUtilities.customItem(BigDecimal.valueOf(8000), BigDecimal.valueOf(2), BigDecimal.valueOf(4000), BigDecimal.valueOf(500));
        String item2 = TestUtilities.customItem(BigDecimal.valueOf(8000), BigDecimal.valueOf(2), BigDecimal.valueOf(4000), BigDecimal.valueOf(600));

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
                .bodyValue(TestUtilities.transactionWithCustomItems(externalId, customerId, null, true, null, item1, item2))
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_OneItemHasDiscountOneItemIsNegative_Returns200() {
        //Given
        String externalId = "10005";
        String item1 = TestUtilities.customItem(BigDecimal.valueOf(-800), BigDecimal.valueOf(2), BigDecimal.valueOf(-400), null);
        String item2 = TestUtilities.customItem(BigDecimal.valueOf(8000), BigDecimal.valueOf(2), BigDecimal.valueOf(4000), BigDecimal.valueOf(600));

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
                .bodyValue(TestUtilities.transactionWithCustomItems(externalId, customerId, null, true, null, item1, item2))
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_ItemUnitPriceAndQuantityNullAndTotalNotNull_Returns200() {
        //Given
        String externalId = "10005";
        String item= TestUtilities.customItem(BigDecimal.valueOf(8000), null, null, null);

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
                .bodyValue(TestUtilities.transactionWithCustomItems(externalId, customerId, null, true, null, item))
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_ItemUnitPriceAndQuantityNotNullAndTotalNull_Returns200() {
        //Given
        String externalId = "10005";
        String item= TestUtilities.customItem(null, BigDecimal.valueOf(2), BigDecimal.valueOf(4000), null);

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
                .bodyValue(TestUtilities.transactionWithCustomItems(externalId, customerId, null, true, null, item))
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_ItemDiscountIsEqualsToTotal_Returns200() {
        //Given
        String externalId = "10005";
        String item= TestUtilities.customItem(BigDecimal.valueOf(8000), null, null, BigDecimal.valueOf(8000));

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
                .bodyValue(TestUtilities.transactionWithCustomItems(externalId, customerId, null, true, null, item))
                .exchange()
                .expectStatus().isOk();
    }

    @Order(1)
    @Test
    @Override
    public void upsertByExternalIdAndSource_ItemDiscountIsEqualsToUnitPriceMultiplyByQuantity_Returns200() {
        //Given
        String externalId = "10005";
        String item= TestUtilities.customItem(null, BigDecimal.valueOf(2), BigDecimal.valueOf(4000), BigDecimal.valueOf(8000));

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
                .bodyValue(TestUtilities.transactionWithCustomItems(externalId, customerId, null, true, null, item))
                .exchange()
                .expectStatus().isCreated();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_ItemHasNoUnitPriceAndQuantityAndTotal_Returns400ConflictedData() {
        // Given
        String externalId = "10005";
        String item = TestUtilities.customItem(null, null, null, BigDecimal.ZERO);

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
                .bodyValue(TestUtilities.transactionWithCustomItems(externalId, customerId, null, true, null, item))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_ConflictingItemHasNegativeTotalAndDiscount_Returns400ConflictedData() {
        // Given
        String externalId = "10005";
        String item = TestUtilities.customItem(BigDecimal.valueOf(-500), null, null, BigDecimal.valueOf(-1));

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
                .bodyValue(TestUtilities.transactionWithCustomItems(externalId, customerId, null, true, null, item))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_ConflictingItemHasNegativeUnitPriceAndQuantityAndDiscount_Returns400ConflictedData() {
        // Given
        String externalId = "10005";
        String item = TestUtilities.customItem(null, BigDecimal.valueOf(-500), BigDecimal.valueOf(1), BigDecimal.valueOf(-1));

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
                .bodyValue(TestUtilities.transactionWithCustomItems(externalId, customerId, null, true, null, item))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_ConflictingItemHasNegativeDiscount_Returns400ConflictedData() {
        // Given
        String externalId = "10005";
        String item = TestUtilities.customItem(null, BigDecimal.valueOf(500), BigDecimal.valueOf(1), BigDecimal.valueOf(-1));

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
                .bodyValue(TestUtilities.transactionWithCustomItems(externalId, customerId, null, true, null, item))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
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

    @Order(2)
    @Test
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

    @Order(2)
    @Test
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

//    @Order(2)
//    @Test
    //todo: fix this test
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

//    @Order(2)
//    @Test
    //todo: fix this test
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

//    @Order(2)
//    @Test
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
    public void getAllBySource_QueryParamInvalid_Returns400() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/1")
                        .queryParam("page","null")
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    public void getAllBySource_PathVariableInvalid_Returns400() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/null")
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isBadRequest();
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
                .value(list -> assertEquals(RepositoryConstant.DEFAULT_SIZE, list.size()));
    }

    @Order(2)
    @Test
    @Override
    public void getAll_QueryParamInvalid_Returns400() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL)
                        .queryParam("size","null")
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isBadRequest();
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
    public void getByExternalIdAndSource_PathVariableInvalid_Returns400() {
        //Given
        String externalId = "null";
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
                .expectStatus().isBadRequest();
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
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId, state, createdDate))
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_PathVariableInvalid_Returns400() {
        //Given
        String externalId = "undefined";

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
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId, state, createdDate))
                .exchange()
                .expectStatus().isBadRequest();
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
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId, state, createdDate))
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
                .bodyValue(TestUtilities.existingTransactionJsonExample(externalId, customerId, TestUtilities.NON_EXISTING_COMPLYT_ID, createdDate))
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
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId, state, createdDate))
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
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId, state, createdDate))
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
                .bodyValue(TestUtilities.unvalidatedTransactionJsonExample(externalId, customerId, createdDate))
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

    @Order(2)
    @Test
    @Override
    public void getByComplytId_PathVariableInvalid_Returns400() {
        // Given
        String complytId = "null";

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/complytId/" + complytId)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isBadRequest();
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

    @Order(0)
    @Test
    @Override
    public void getAll_GetByParamSize_ReturnsExpectedSize() {
        int size = 1;

        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL)
                        .queryParam("size", size)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LinkedHashMap.class)
                .hasSize(size);
    }

    @Order(0)
    @Test
    @Override
    public void getAll_GetByParamPage_ReturnsExpectedPage() {
        int page = 2;
        int size = 1;
        String expectedComplyId = "607f3926-61d3-40a4-9b3a-a6bf7c3a1d95";

        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL)
                        .queryParam("size", size)
                        .queryParam("page", page)
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LinkedHashMap.class)
                .value(transaction -> assertEquals(transaction.get(0).get("complytId"), expectedComplyId));
    }

    @Order(0)
    @Test
    @Override
    public void getAll_GetByDefaultsSizeAndPage_ReturnsExpectedEntries() {
        String expectedComplyId = "6ee574bb-0300-4c74-9e4f-1852f234a028";

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
                .value(transaction -> assertEquals(transaction.get(0).get("complytId"), expectedComplyId))
                .value(transactionLst -> assertTrue(transactionLst.size() <= RepositoryConstant.DEFAULT_SIZE));
    }

    @Order(0)
    @Test
    @Override
    /*
     This transaction's customer has an exemption in state PA with validation dates of:
     fromDate: 2025-01-01, toDate: 26-01-01, therefore transaction is sales-tax exempt
    */
    public void upsertByExternalIdAndSource_CustomerIsExemptByStateAndDate_ReturnsNonTaxableTransaction() {
        String externalId = "nonExistingTransactionID";
        String createdDate = "2025-01-02";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId, exemptedState, createdDate))
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertNull(map.get("salesTax"));
                });
    }

    @Order(0)
    @Test
    @Override
    /*
     This transaction's customer has an exemption in state PA with validation dates of:
     fromDate: 2025-01-01, toDate: 26-01-01, therefore transaction is NOT sales-tax exempt
    */
    public void upsertByExternalIdAndSource_CustomerIsNotExemptByStateAndDate_ReturnsTaxableTransaction() {
        String externalId = "anotherNonExistingTransactionID";
        String createdDate = "2024-01-02";
        String exemptedState = "PA";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId, exemptedState, createdDate))
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertNotNull(map.get("salesTax"));
                });
    }

    @Order(0)
    @Test
    @Override
    /*
     This transaction's customer has an exemption in state FL with Exemption Status - CANCELLED,
     therefore transaction is NOT sales-tax exempt
    */
    public void upsertByExternalIdAndSource_CustomerIsNotExemptBecauseExemptionIsCancelled_ReturnsTaxableTransaction() {
        String externalId = "ThirdNonExistingIdForExemptionChecks";
        String createdDate = "2024-01-02";
        String stateCancelled = "AK";

        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId, stateCancelled, createdDate))
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertNotNull(map.get("salesTax"));
                });
    }

    /*
    This transaction's customer has two exemptions in state PA by this schema in this timeframe
                 |-----Fully-Exemption-----------|
                                 |---------Partially-Exemption------------|
     time:  2025-01-01      2025-12-01        2026-01-01             2026-05-01
     transactionCreatedDate:               (T)
     */
    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_CustomerIsFullyExemptAndPartiallyExemption_Exempt() {
        String externalId = "nonExistingTransactionID_A";
        String createdDate = "2025-12-02";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId, exemptedState, createdDate))
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String salesTax = (String) map.get("salesTax");
                    assertNull(salesTax);
                });
    }

    /*
    This transaction's customer has two exemptions in state PA by this schema in this timeframe
                 |-----Fully-Exemption-----------|
                                 |---------Partially-Exemption------------|
     time:  2025-01-01      2025-12-01        2026-01-01             2026-05-01
     transactionCreatedDate:                                     (T)
     */
    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_CustomerIsPartiallyExempt_NotExempted() {
        String externalId = "nonExistingTransactionID_B";
        String createdDate = "2026-02-01";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId, exemptedState, createdDate))
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertNotNull(map.get("salesTax"));
                });
    }

    /*
    This transaction's customer has two exemptions in state PA by this schema in this timeframe
                 |-----Fully-Exemption-----------|
                                 |---------Partially-Exemption------------|
     time:  2025-01-01      2025-12-01        2026-01-01          2026-05-01
     transactionCreatedDate:                                                         (T)
     */
    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_CustomerIsNotNoExempt_NoExempted() {
        String externalId = "nonExistingTransactionID_C";
        String createdDate = "2026-05-02";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId, exemptedState, createdDate))
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertNotNull(map.get("salesTax"));
                });
    }

    /*
    This transaction's customer has two exemptions in state PA by this schema in this timeframe
              |---------Not-Active-Exemption------------|
                                |----------Fully-Exemption-----------|
     time:  2027-01-01      2027-12-01             2028-01-01          2028-05-01
     transactionCreatedDate:               (T)
     */
    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_NotActiveExemptionAndFullyExempt_Exempted() {
        String externalId = "nonExistingTransactionID_D";
        String createdDate = "2027-12-02";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId, exemptedState, createdDate))
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String salesTax = (String) map.get("salesTax");
                    assertNull(salesTax);
                });
    }

    /*
    This transaction's customer has two exemptions in state PA by this schema in this timeframe
              |---------Not-Active-Exemption------------|
                                        |----------Fully-Exemption-----------|
     time:  2027-01-01              2027-12-01             2028-01-01          2028-05-01
     transactionCreatedDate:  (T)
     */
    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_NotActiveExemption_NoExempted() {
        String externalId = "nonExistingTransactionID_E";
        String createdDate = "2027-01-02";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(TestUtilities.transactionJsonExample(externalId, customerId, exemptedState, createdDate))
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertNotNull(map.get("salesTax"));
                });
    }

}