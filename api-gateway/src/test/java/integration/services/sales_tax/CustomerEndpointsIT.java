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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = ApiGatewayApplication.class
        , webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
        , properties = {"server.port=8765", "management.server.port=8765"}
)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(profiles = {"integration-test", "stubFastTax"})
public class CustomerEndpointsIT extends TestContainersInitializerIT implements CustomerEndpointsITTemplate {

    @Autowired
    private WebTestClient webTestClient;

    // Given
    private String source = "1";

    @Order(-1)
    @Test
    @Override
    public void checkConnection() {
        while (!IS_SALES_TAX_REGISTERED) {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(TestUtilities.CUSTOMER_BASE_URL)
                            .build())
                    .headers(headers -> headers
                            .setBearerAuth(TOKEN))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().value(status -> IS_SALES_TAX_REGISTERED = status != 503);
        }
    }

    @Order(2)
    @Test
    @Override
    public void getAllBySource_Exists_Returns200() {
        // Given
        String differentSource = "2";

        // Then
        webTestClient
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
        webTestClient
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
        webTestClient
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
        webTestClient
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
        webTestClient
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
        webTestClient
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
        webTestClient
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
        webTestClient
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
        webTestClient
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
        webTestClient
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
        webTestClient
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
        webTestClient
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
        webTestClient
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
        webTestClient
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
        webTestClient
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
        webTestClient
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
        webTestClient
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

    @Order(1)
    @Test
    @Override
    public void upsertByExternalIdAndSource_NoBody_Returns400() {
        // Given
        String externalId = "0";

        // Then
        webTestClient
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
        webTestClient
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
        webTestClient
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
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CUSTOMER_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isForbidden();
    }

    @Order(1)
    @Test
    @Override
    public void put_InsufficientScopes_Returns403() {
        webTestClient
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