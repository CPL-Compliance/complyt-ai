package integration.services.sales_tax;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VatValidationIT extends TestContainersInitializerIT implements VatValidationITTemplate {

    @Order(0)
    @Test
    @Override
    public void upsert_ValidateExitingVatDetails_Return200() {
        // Given
        Pair<String, String> vatDetails = TestUtilities.vatDetailsToValidateExistInTheDB();

        // When + Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder.path(TestUtilities.VAT_VALIDATION_BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.getLeft())
                        .queryParam("vatNumber", vatDetails.getRight())
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody(LinkedHashMap.class)
                .value(validatedVat -> {
                    assertEquals("AT", validatedVat.get("countryCode"));
                    assertEquals("Austria", validatedVat.get("countryName"));
                    assertEquals("U28609707", validatedVat.get("vatNumber"));
                    assertTrue((Boolean) validatedVat.get("valid"));
                    assertEquals("Andritz AG", validatedVat.get("name"));
                    assertEquals("Stattegger Straße 18\nAT-8045 Graz", validatedVat.get("address"));
                });
    }

    @Order(0)
    @Test
    @Override
    public void upsert_ValidateExitingVatDetailsCountryCodeIsFullCountryNameAndCodeExistsInVatNumber_Return200() {
        // Given
        Pair<String, String> vatDetails = new MutablePair<>("Austria", "ATU28609707");

        // When + Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder.path(TestUtilities.VAT_VALIDATION_BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.getLeft())
                        .queryParam("vatNumber", vatDetails.getRight())
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody(LinkedHashMap.class)
                .value(validatedVat -> {
                    assertEquals("AT", validatedVat.get("countryCode"));
                    assertEquals("Austria", validatedVat.get("countryName"));
                    assertEquals("U28609707", validatedVat.get("vatNumber"));
                    assertTrue((Boolean) validatedVat.get("valid"));
                    assertEquals("Andritz AG", validatedVat.get("name"));
                    assertEquals("Stattegger Straße 18\nAT-8045 Graz", validatedVat.get("address"));
                });
    }

    @Order(0)
    @Test
    @Override
    public void upsert_ValidateNewVatDetails_Return201() {
        // Given
        Pair<String, String> vatDetails = TestUtilities.vatDetailsToValidateCorrectCase();

        // When + Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder.path(TestUtilities.VAT_VALIDATION_BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.getLeft())
                        .queryParam("vatNumber", vatDetails.getRight())
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LinkedHashMap.class)
                .value(validatedVat -> {
                    assertEquals("BE", validatedVat.get("countryCode"));
                    assertEquals("Belgium", validatedVat.get("countryName"));
                    assertEquals("0835221567", validatedVat.get("vatNumber"));
                    assertTrue((Boolean) validatedVat.get("valid"));
                    assertEquals("BV BE³-PROJECTS", validatedVat.get("name"));
                    assertEquals("Kasteeldreef 9\\n2940 Stabroek", validatedVat.get("address"));
                });
    }

    @Order(1)
    @Test
    @Override
    public void upsert_ValidateAlreadyExistingVatDetails_Return200() {
        // Given
        Pair<String, String> vatDetails = TestUtilities.vatDetailsToValidateCorrectCase();

        // When + Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder.path(TestUtilities.VAT_VALIDATION_BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.getLeft())
                        .queryParam("vatNumber", vatDetails.getRight())
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody(LinkedHashMap.class)
                .value(validatedVat -> {
                    assertEquals("BE", validatedVat.get("countryCode"));
                    assertEquals("Belgium", validatedVat.get("countryName"));
                    assertEquals("0835221567", validatedVat.get("vatNumber"));
                    assertTrue((Boolean) validatedVat.get("valid"));
                    assertEquals("BV BE³-PROJECTS", validatedVat.get("name"));
                    assertEquals("Kasteeldreef 9\\n2940 Stabroek", validatedVat.get("address"));
                });
    }

    @Order(0)
    @Test
    @Override
    public void upsert_ValidateVatDetailsNotValid_Return201WithSameValuesAsInputAndValidFalse() {
        // Given
        Pair<String, String> vatDetails = TestUtilities.vatDetailsToValidateBackendErrorJson();

        // When + Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder.path(TestUtilities.VAT_VALIDATION_BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.getLeft())
                        .queryParam("vatNumber", vatDetails.getRight())
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Order(0)
    @Test
    @Override
    public void upsert_CountryCodeIsBlank_Return400() {
        // Given
        Pair<String, String> vatDetails = new MutablePair<>("", "0835221567");

        // When + Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder.path(TestUtilities.VAT_VALIDATION_BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.getLeft())
                        .queryParam("vatNumber", vatDetails.getRight())
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                }).exchange()
                .expectStatus().isBadRequest();
    }

    @Order(0)
    @Test
    @Override
    public void upsert_CountryCodeIsNull_Return400() {
        // Given
        Pair<String, String> vatDetails = TestUtilities.vatDetailsToValidateCorrectCase();

        // When + Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder.path(TestUtilities.VAT_VALIDATION_BASE_URL + "/validate")
//                        .queryParam("countryCode", vatDetails.getLeft()) <- null
                        .queryParam("vatNumber", vatDetails.getRight())
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(0)
    @Test
    @Override
    public void upsert_CountryCodeIsMoreThan50Characters_Return400() {
        // Given
        Pair<String, String> vatDetails = new MutablePair<>("A".repeat(51), "0835221567");

        // When + Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder.path(TestUtilities.VAT_VALIDATION_BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.getLeft())
                        .queryParam("vatNumber", vatDetails.getRight())
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(0)
    @Test
    @Override
    public void upsert_VatNumberIsBlank_Return400() {
        // Given
        Pair<String, String> vatDetails = new MutablePair<>("BE", "");

        // When + Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder.path(TestUtilities.VAT_VALIDATION_BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.getLeft())
                        .queryParam("vatNumber", vatDetails.getRight())
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                }).exchange()
                .expectStatus().isBadRequest();
    }

    @Order(0)
    @Test
    @Override
    public void upsert_VatNumberIsNull_Return400() {
        // Given
        Pair<String, String> vatDetails = TestUtilities.vatDetailsToValidateCorrectCase();

        // When + Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder.path(TestUtilities.VAT_VALIDATION_BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.getLeft())
//                        .queryParam("vatNumber", vatDetails.getRight()) <- null
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                }).exchange()
                .expectStatus().isBadRequest();
    }

    @Order(0)
    @Test
    @Override
    public void upsert_VatNumberIsMoreThan20Characters_Return400() {
        // Given
        Pair<String, String> vatDetails = new MutablePair<>("BE", "1".repeat(21));

        // When + Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder.path(TestUtilities.VAT_VALIDATION_BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.getLeft())
                        .queryParam("vatNumber", vatDetails.getRight())
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                }).exchange()
                .expectStatus().isBadRequest();
    }

    @Order(0)
    @Test
    @Override
    public void upsert_ErrorInValidationWebClient_Return400BadCountry() {
        // Given
        Pair<String, String> vatDetails = TestUtilities.vatDetailsToValidateBackendErrorJson();

        // When + Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder.path(TestUtilities.VAT_VALIDATION_BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.getLeft())
                        .queryParam("vatNumber", vatDetails.getRight())
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                }).exchange()
                .expectStatus().is4xxClientError();
    }
}
