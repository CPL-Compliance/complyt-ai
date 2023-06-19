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
import org.springframework.web.reactive.function.BodyInserters;

import java.util.LinkedHashMap;

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
public class SalesTaxTrackingEndpointsIT extends TestContainersInitializerIT implements SalesTaxTrackingEndpointsITTemplate {

    @Autowired
    private WebTestClient webTestClient;

    @Order(-1)
    @Test
    public void checkConnection() {
        while (!IS_SALES_TAX_REGISTERED) {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL)
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
    @WithMockUser
    public void getAll_Exists_Returns200() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LinkedHashMap.class)
                .value(list -> assertTrue(list.size() > 4));
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
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_DIFFERENT_TENANT);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LinkedHashMap.class)
                .value(list -> assertEquals(0,list.size()));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByComplytId_Exists_Returns200() {
        // Given
        String complytId = "cba95b8d-ef9b-4f4d-831d-377621556b50";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/complytId/" + complytId)
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
    @WithMockUser
    public void getByComplytId_DoesntExists_Returns404() {
        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/complytId/" + TestUtilities.NON_EXISTING_COMPLYT_ID)
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
    @WithMockUser
    public void getByComplytId_complytIdDoesntParse_Returns500() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/complytId/invalid")
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
    @WithMockUser
    public void getByStateName_Exists_Returns200() {
        // Given
        String existingStateName = "California";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/" + existingStateName)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state.name").isEqualTo(existingStateName);
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByStateAbbreviation_Exists_Returns200() {
        // Given
        String existingStateAbbreviation = "CA";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/" + existingStateAbbreviation)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state.abbreviation").isEqualTo(existingStateAbbreviation);
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getByStateAbbreviation_DoesntExists_Returns404() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/Nilfgaard")
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
    @WithMockUser
    public void getByStateName_DoesntExists_Returns404() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/NLF")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(3)
    @Test
    @Override
    @WithMockUser
    public void upsertByState_DoesntExists_Returns201() {
        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/NLF")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.salesTaxTrackingJsonExample("Nilfgaard", "NLF", null))
                .exchange()
                .expectStatus().isCreated();
    }

    @Order(4)
    @Test
    @Override
    @WithMockUser
    public void upsertByState_Exists_Returns200() {
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/CA")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.salesTaxTrackingJsonExample("California", "CA", null))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state.name").isEqualTo("California");
    }

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void upsertByState_DoesntExistsWithComplytId_Returns400ConflictedData() {
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/CA")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.salesTaxTrackingJsonExample("California", "CA", TestUtilities.NON_EXISTING_COMPLYT_ID))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void upsertByState_ConflictingState_Returns400ConflictedData() {
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/dope")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.salesTaxTrackingJsonExample("California", "CA", null))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void upsertByState_DoesntPassValidation_Returns400CValidationError() {
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/CA")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.unvalidatedSalesTaxTrackingJsonExample("California", "CA"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    String[] errors = message.substring(1, message.length() - 1).split(", ");
                    assertEquals(2, errors.length);
                });
        ;
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
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/" + state)
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
}