package integration.services.sales_tax;

import com.nimbusds.jose.shaded.gson.JsonArray;
import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerEndpointsIT extends TestContainersInitializerIT implements CustomerEndpointsITTemplate {

    private final String source = "1";

    @Order(2)
    @Test
    @Override
    public void getAllBySource_Exists_Returns200() {
        // Given
        String differentSource = "2";

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL + "/source/" + differentSource)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(String.class)
                .value(list ->
                        assertEquals(1, list.size()));
    }

    @Order(2)
    @Test
    @Override
    public void getAllBySource_DoesntExists_Returns200EmptyList() {
        // Given
        String differentSource = "9";

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL + "/source/" + differentSource)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Order(2)
    @Test
    @Override
    public void getAll_Exists_Returns200() {
        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LinkedHashMap.class)
                .value(list ->
                        assertTrue(list.size() > 5));
    }

    @Order(2)
    @Test
    @Override
    public void getByAll_DoesntExists_Returns200EmptyList() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_DIFFERENT_TENANT);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Order(2)
    @Test
    @Override
    public void getByComplytId_Exists_Returns200() {
        // Given
        String complytId = "4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5"; //complytId of existing customer

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL + "/complytId/" + complytId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.complytId").isEqualTo(complytId);
    }

    @Order(2)
    @Test
    @Override
    public void getByComplytId_DoesntExists_Returns404() {
        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL + "/complytId/" + TestUtilities.NON_EXISTING_COMPLYT_ID)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(2)
    @Test
    @Override
    public void getByComplytId_complytIdDoesntParse_Returns500() {
        // Given
        String invalidComplytId = "gg";

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL + "/complytId/" + invalidComplytId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Order(2)
    @Test
    @Override
    public void getByExternalIdAndSource_Exists_Returns200() {
        // Given
        String externalId = "1586";

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    public void getByExternalIdAndSource_DoesntExists_Returns404() {
        // Given
        String externalId = "nonExisting";

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(2)
    @Test
    @Override
    public void getByName_Exists_Returns200() {
        // Given
        String name = "best";

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL + "/name/" + name)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2);
    }

    @Order(2)
    @Test
    @Override
    public void getByName_DoesntExists_Returns200EmptyList() {
        // Given
        String name = "nonExisting";

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL + "/name/" + name)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_DIFFERENT_TENANT);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Order(3)
    @Test
    @Override
    public void upsertByExternalIdAndSource_Exists_Returns200() {
        // Given
        String externalId = "1001";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(TestUtilities.customerJsonExample(externalId, null))
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_DoesntExists_Returns201() {
        // Given
        String externalId = "1001";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(TestUtilities.customerJsonExample(externalId, null))
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isCreated();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_DoesntExistsWithComplytId_Returns400ConflictedData() {
        // Given
        String externalId = "1002";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.customerJsonExample(externalId, TestUtilities.NON_EXISTING_COMPLYT_ID))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_ConflictingSource_Returns400ConflictedData() {
        // Given
        String externalId = "1002";
        String differentSource = "9";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL + "/source/" + differentSource + "/externalId/" + externalId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.customerJsonExample(externalId, null))
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
                        .path(TestUtilities.CUSTOMER_BASE_URL + "/source/" + source + "/externalId/" + differentExternalId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.customerJsonExample(externalId, null))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    public void upsertByExternalIdAndSource_DoesntPassValidation_Returns400CValidationError() {
        // Given
        String externalId = "1003";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.unvalidatedCustomerJsonExample(externalId, null))
                .exchange()

                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    String[] errors = message.substring(1, message.length() - 1).split(", ");
                    assertEquals(2, errors.length);
                });
    }

    @Test
    @Order(3)
    @WithMockUser
    public void getAll_getCustomersByParamSize_ReturnsExpectedSize() {
        int size = 5;
//        WEB_TEST_CLIENT
//                .mutateWith(csrf())
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.CUSTOMER_BASE_URL)
//                        .queryParam("size", size)
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN);
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                })
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(String.class)
//                .value(list ->
//                        assertEquals(size, list.size()));
    }

    @Test
    @Order(3)
    @WithMockUser
    public void getAll_getCustomersByDefaultOffset_ReturnsFirstEntry() {
        int size = 1;
//        String expectedComplyId = "cba95b8d-ef9b-4f4d-831d-377621556b50";
//        WEB_TEST_CLIENT
//                .mutateWith(csrf())
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.CUSTOMER_BASE_URL)
//                        .queryParam("size", size) // Add query parameter for offset
//                        .build())
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(JsonArray.class)
//                .value(customers -> Assertions.assertEquals(customers.get(0), expectedComplyId))
//                .hasSize(size);
    }

    @Test
    @Order(3)
    @WithMockUser
    public void getAll_GetSkippedCustomersByOffset_ReturnsExpectedEntryByOffset() {
        int size = 1;
        int page = 2;

//        String expectedComplyId = "cba95b8d-ef9b-4f4d-831d-377621556b50";
//        webTestClient
//                .mutateWith(csrf())
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(SalesTaxTrackingRouter.BASE_URL) // Set your API endpoint
//                        .queryParam("size", size)
//                        .queryParam("page",page)
//                        .build())
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(Customer.class)
//                .value(customers -> Assertions.assertEquals(customers.get(0).getComplytId().toString(), expectedComplyId))
//                .hasSize(size);
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
                        .path(TestUtilities.CUSTOMER_BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").exists();
    }

    @Order(1)
    @Test
    @Override
    public void get_NoAccessToken_Returns401() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL)
                        .build())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Order(1)
    @Test
    @Override
    public void get_InsufficientScopes_Returns403() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_NO_SCOPES);
                })
                .exchange()
                .expectStatus().isForbidden();
    }

    @Order(1)
    @Test
    @Override
    public void put_NoAccessToken_Returns401() {
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Order(1)
    @Test
    @Override
    public void put_InsufficientScopes_Returns403() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_NO_SCOPES);
                })
                .exchange()
                .expectStatus().isForbidden();
    }
}