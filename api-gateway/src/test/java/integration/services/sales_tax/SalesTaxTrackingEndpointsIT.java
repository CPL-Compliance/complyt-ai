//package integration.services.sales_tax;
//
//import integration.TestContainersInitializerIT;
//import integration.test_utils.TestUtilities;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.springframework.http.MediaType;
//
//import java.util.LinkedHashMap;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class SalesTaxTrackingEndpointsIT extends TestContainersInitializerIT implements SalesTaxTrackingEndpointsITTemplate {
//
//    @Order(2)
//    @Test
//    @Override
//    public void getAll_Exists_Returns200() {
//        WEB_TEST_CLIENT
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL)
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN);
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                })
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(LinkedHashMap.class)
//                .value(list -> assertTrue(list.size() > 4));
//    }
//
//    @Order(2)
//    @Test
//    @Override
//    public void getByAll_DoesntExists_Returns200EmptyList() {
//        // Then
//        WEB_TEST_CLIENT
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL)
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN_DIFFERENT_TENANT);
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                })
//                .exchange()
//                .expectStatus().isOk()
//                .expectBodyList(LinkedHashMap.class)
//                .value(list -> assertEquals(0, list.size()));
//    }
//
//    @Order(2)
//    @Test
//    @Override
//    public void getByComplytId_Exists_Returns200() {
//        // Given
//        String complytId = "cba95b8d-ef9b-4f4d-831d-377621556b50";
//
//        // Then
//        WEB_TEST_CLIENT
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/complytId/" + complytId)
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN);
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                })
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.complytId").isEqualTo(complytId);
//    }
//
//    @Order(2)
//    @Test
//    @Override
//    public void getByComplytId_DoesntExists_Returns404() {
//        // Then
//        WEB_TEST_CLIENT
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/complytId/" + TestUtilities.NON_EXISTING_COMPLYT_ID)
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN);
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                })
//                .exchange()
//                .expectStatus().isNotFound();
//    }
//
//    @Order(2)
//    @Test
//    @Override
//    public void getByComplytId_complytIdDoesntParse_Returns500() {
//        WEB_TEST_CLIENT
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/complytId/invalid")
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN);
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                })
//                .exchange()
//                .expectStatus().is5xxServerError();
//    }
//
//    @Order(2)
//    @Test
//    @Override
//    public void getByStateName_Exists_Returns200() {
//        // Given
//        String existingStateName = "California";
//
//        // Then
//        WEB_TEST_CLIENT
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/" + existingStateName)
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN);
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                })
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.state.name").isEqualTo(existingStateName);
//    }
//
//    @Order(2)
//    @Test
//    @Override
//    public void getByStateAbbreviation_Exists_Returns200() {
//        // Given
//        String existingStateAbbreviation = "CA";
//
//        // Then
//        WEB_TEST_CLIENT
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/" + existingStateAbbreviation)
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN);
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                })
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.state.abbreviation").isEqualTo(existingStateAbbreviation);
//    }
//
//    @Order(2)
//    @Test
//    @Override
//    public void getByStateAbbreviation_DoesntExists_Returns404() {
//        WEB_TEST_CLIENT
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/Nilfgaard")
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN);
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                })
//                .exchange()
//                .expectStatus().isNotFound();
//    }
//
//    @Order(2)
//    @Test
//    @Override
//    public void getByStateName_DoesntExists_Returns404() {
//        WEB_TEST_CLIENT
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/NLF")
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN);
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                })
//                .exchange()
//                .expectStatus().isNotFound();
//    }
//
//    @Order(3)
//    @Test
//    @Override
//    public void upsertByState_DoesntExists_Returns201() {
//        // Then
//        WEB_TEST_CLIENT
//                .put()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/NLF")
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN);
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                })
//                .bodyValue(TestUtilities.salesTaxTrackingJsonExample("Nilfgaard", "NLF", null))
//                .exchange()
//                .expectStatus().isCreated();
//    }
//
//    @Order(4)
//    @Test
//    @Override
//    public void upsertByState_Exists_Returns200() {
//        WEB_TEST_CLIENT
//                .put()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/Nilfgaard")
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN);
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                })
//                .bodyValue(TestUtilities.salesTaxTrackingJsonExample("Nilfgaard", "NLF", null))
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.state.name").isEqualTo("Nilfgaard");
//    }
//
//    @Order(1)
//    @Test
//    @Override
//    public void upsertByState_DoesntExistsWithComplytId_Returns400ConflictedData() {
//        WEB_TEST_CLIENT
//                .put()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/CA")
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN);
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                })
//                .bodyValue(TestUtilities.salesTaxTrackingJsonExample("California", "CA", TestUtilities.NON_EXISTING_COMPLYT_ID))
//                .exchange()
//                .expectStatus().isBadRequest();
//    }
//
//    @Order(1)
//    @Test
//    @Override
//    public void upsertByState_ConflictingState_Returns400ConflictedData() {
//        WEB_TEST_CLIENT
//                .put()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/dope")
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN);
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                })
//                .bodyValue(TestUtilities.salesTaxTrackingJsonExample("California", "CA", null))
//                .exchange()
//                .expectStatus().isBadRequest();
//    }
//
//    @Order(1)
//    @Test
//    @Override
//    public void upsertByState_DoesntPassValidation_Returns400CValidationError() {
//        WEB_TEST_CLIENT
//                .put()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/CA")
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN);
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                })
//                .bodyValue(TestUtilities.unvalidatedSalesTaxTrackingJsonExample("California", "CA"))
//                .exchange()
//                .expectStatus().isBadRequest()
//                .expectBody(LinkedHashMap.class)
//                .value(map -> {
//                    String message = (String) map.get("message");
//                    String[] errors = message.substring(1, message.length() - 1).split(", ");
//                    assertEquals(2, errors.length);
//                });
//        ;
//    }
//
//    @Order(1)
//    @Test
//    @Override
//    public void upsertByState_NoBody_Returns400() {
//        // Given
//        String state = "CA";
//
//        // Then
//        WEB_TEST_CLIENT
//                .put()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL + "/state/" + state)
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN);
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                })
//                .exchange()
//                .expectStatus().isBadRequest()
//                .expectBody()
//                .jsonPath("$.message").exists();
//    }
//
//    @Order(1)
//    @Test
//    @Override
//    public void get_NoAccessToken_Returns401() {
//        WEB_TEST_CLIENT
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL)
//                        .build())
//                .exchange()
//                .expectStatus().isUnauthorized();
//    }
//
//    @Order(1)
//    @Test
//    @Override
//    public void get_InsufficientScopes_Returns403() {
//        WEB_TEST_CLIENT
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL)
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN_NO_SCOPES);
//                })
//                .exchange()
//                .expectStatus().isForbidden();
//    }
//
//    @Order(1)
//    @Test
//    @Override
//    public void put_NoAccessToken_Returns401() {
//        WEB_TEST_CLIENT
//                .put()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL)
//                        .build())
//                .headers(headers -> {
//                    headers.setContentType(MediaType.APPLICATION_JSON);
//                })
//                .exchange()
//                .expectStatus().isForbidden();
//    }
//
//    @Order(1)
//    @Test
//    @Override
//    public void put_InsufficientScopes_Returns403() {
//        WEB_TEST_CLIENT
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TestUtilities.SALES_TAX_TRACKING_BASE_URL)
//                        .build())
//                .headers(headers -> {
//                    headers.setBearerAuth(TOKEN_NO_SCOPES);
//                })
//                .exchange()
//                .expectStatus().isForbidden();
//    }
//}