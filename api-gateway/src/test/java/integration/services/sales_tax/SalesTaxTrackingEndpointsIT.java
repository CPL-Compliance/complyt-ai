package integration.services.sales_tax;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import integration.test_utils.templates.endpoints.RepositoryConstant;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SalesTaxTrackingEndpointsIT extends TestContainersInitializerIT implements SalesTaxTrackingEndpointsITTemplate {

    @Order(2)
    @Test
    @Override
    public void getAll_Exists_Returns200() {
        WEB_TEST_CLIENT
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
    public void getAll_QueryParamInvalid_Returns400() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL)
                        .queryParam("page", "null")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
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
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_DIFFERENT_TENANT);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LinkedHashMap.class)
                .value(list -> assertEquals(0, list.size()));
    }

    @Order(2)
    @Test
    @Override
    public void getByComplytId_Exists_Returns200() {
        // Given
        String complytId = "6eaa133c-df9c-4f88-bba9-6dd3845c803a";

        // Then
        WEB_TEST_CLIENT
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
    public void getByComplytId_PathVariableInvalid_Returns400() {
        // Given
        String nullComplytId = "null";

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/complytId/" + nullComplytId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    public void getByComplytId_DoesntExists_Returns404() {
        // Then
        WEB_TEST_CLIENT
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
    public void getByStateName_Exists_Returns200() {
        // Given
        String existingStateName = "California";

        // Then
        WEB_TEST_CLIENT
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
    public void getByStateName_PathVariableInvalid_Returns400() {
        // Given
        String StateName = "null";

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/" + StateName)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    public void getByStateAbbreviation_Exists_Returns200() {
        // Given
        String existingStateAbbreviation = "CA";

        // Then
        WEB_TEST_CLIENT
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
    public void getByStateAbbreviation_DoesntExists_Returns404() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/NV")
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
    public void getByStateName_DoesntExists_Returns404() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/Nevada")
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
    public void upsertByState_DoesntExists_Returns201() {
        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/Nevada")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.salesTaxTrackingJsonExample("Nevada", "NV", null))
                .exchange()
                .expectStatus().isCreated();
    }

    @Order(4)
    @Test
    @Override
    public void upsertByState_Exists_Returns200() {
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/Hawaii")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.salesTaxTrackingJsonExample("Hawaii", "HI", null))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state.name").isEqualTo("Hawaii");
    }

    @Order(2)
    @Test
    @Override
    public void upsertByState_PathVariableInvalid_Returns400() {
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/null")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.salesTaxTrackingJsonExample("Hawaii", "HI", null))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(1)
    @Test
    @Override
    public void upsertByState_DoesntExistsWithComplytId_Returns400ConflictedData() {
        WEB_TEST_CLIENT
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
    public void upsertByState_ConflictingState_Returns400ConflictedData() {
        WEB_TEST_CLIENT
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
    public void upsertByState_DoesntPassValidation_Returns400CValidationError() {
        WEB_TEST_CLIENT
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

    @Order(1)
    @Test
    @Override
    public void upsertByState_NoBody_Returns400() {
        // Given
        String state = "CA";

        // Then
        WEB_TEST_CLIENT
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

    @Order(1)
    @Test
    @Override
    public void get_NoAccessToken_Returns401() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL)
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
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL)
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
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL)
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
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/dummy")
                        .build())
                .headers(headers -> headers.setBearerAuth(TOKEN_NO_SCOPES))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Order(4)
    @Test
    @Override
    public void refresh_EverythingExists_Returns200WithSummary() {
        // Given
        String existingStateAbbreviation = "CA";
        LocalDate now = LocalDate.now();
        LocalDate summaryDate = LocalDate.parse("2023-12-21");

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/" + existingStateAbbreviation)
                        .queryParam("date", now)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.state.abbreviation").isEqualTo(existingStateAbbreviation)
                .jsonPath("$.nexusCalculationSummaries['%s'].amount".formatted(summaryDate)).isEqualTo(0)
                .jsonPath("$.nexusCalculationSummaries['%s'].count".formatted(summaryDate)).isEqualTo(0);
    }

    @Order(1)
    @Test
    @Override
    public void post_NoAccessToken_Returns401() {
        WEB_TEST_CLIENT
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/refresh/state/dummy")
                        .build())
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Order(1)
    @Test
    @Override
    public void post_InsufficientScopes_Returns403() {
        WEB_TEST_CLIENT
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/refresh/state/dummy")
                        .build())
                .headers(headers -> headers.setBearerAuth(TOKEN_NO_SCOPES))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Order(0)
    @Test
    @Override
    public void getAll_GetByParamSize_ReturnsExpectedSize() {
        int size = 1;

        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL)
                        .queryParam("size", size)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
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
        String expectedComplyId = "42b6d733-decc-4608-bfd3-d45bf868827c";

        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LinkedHashMap.class)
                .value(salesTaxTracking -> assertEquals(salesTaxTracking.get(0).get("complytId"), expectedComplyId));
    }

    @Order(0)
    @Test
    @Override
    public void getAll_GetByDefaultsSizeAndPage_ReturnsExpectedEntries() {
        String expectedComplyId = "6eaa133c-df9c-4f88-bba9-6dd3845c803a";

        WEB_TEST_CLIENT
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
                .value(salesTaxTracking -> assertEquals(salesTaxTracking.get(0).get("complytId"), expectedComplyId))
                .value(customerLst -> assertTrue(customerLst.size() <= RepositoryConstant.DEFAULT_SIZE));
    }

    @Order(2)
    @Test
    public void getByStateNameToPatch_Exists_Returns200() {
        // Given
        String existingStateName = "Virginia";

        // Then
        WEB_TEST_CLIENT
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

    @Order(0)
    @Override
    @Test
    public void patch_PatchesOneField_ReturnsPatchedResource() {
        String state = "VA";
        String map = """
                {
                    "enforcesSalesTax": false
                }
                """;

        WEB_TEST_CLIENT
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/" + state)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(map)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LinkedHashMap.class)
                .value(salesTaxTracking -> assertFalse((Boolean) salesTaxTracking.get(0).get("enforcesSalesTax")));
    }

    @Order(1)
    @Override
    @Test
    public void patch_PatchesTwoFields_ReturnsPatchedResource() {
        String state = "VA";

        WEB_TEST_CLIENT
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/" + state)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.salesTaxTrackingPatchTwoFieldsJsonExample())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LinkedHashMap.class)
                .value(salesTaxTracking -> {
                    assertTrue((Boolean) salesTaxTracking.get(0).get("enforcesSalesTax"));
                    assertTrue(salesTaxTracking.get(0).get("economicNexusTracker").toString().contains("2024-08-02T19:12:00"));
                });
    }

}