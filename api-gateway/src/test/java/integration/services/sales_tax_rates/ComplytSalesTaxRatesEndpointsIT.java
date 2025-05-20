package integration.services.sales_tax_rates;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComplytSalesTaxRatesEndpointsIT extends TestContainersInitializerIT implements ComplytSalesTaxRatesEndpointsITTemplate {

    String requestedTime = "2021-01-01";;
    JSONObject addressCached = TestUtilities.createAddressJsonExample("Anchorage", "USA", "Anchorage", "Alaska", "751-2696 205 E Benson Blvd", "99501", false);

    @Order(1)
    @Test
    @Override
    public void findByAddress_CachedAddressByQuery_InternalRateByMaxDate_Returns200() {
        // Date after the maxEffectiveDate field
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_SALES_TAX_RATES_BASE_URL)
                        .queryParam("country", addressCached.get("country"))
                        .queryParam("state", addressCached.get("state"))
                        .queryParam("city", addressCached.get("city"))
                        .queryParam("street", addressCached.get("street"))
                        .queryParam("zip", addressCached.get("zip"))
                        .queryParam("effectiveDate", requestedTime)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.matchedAddressData.address.state").isEqualTo("Alaska")
                .jsonPath("$.matchedAddressData.scoring.score").isEqualTo("1.0")
                .jsonPath("$.salesTaxRates.taxRate").isEqualTo(0.3f);
    }

    @Order(1)
    @Test
    @Override
    public void findByAddress_CachedAddressByQuery_InternalRateBeforeMaxDate_Returns200() {
        // Date is before the maxEffectiveDate field
        requestedTime = "2000-01-01";

        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_SALES_TAX_RATES_BASE_URL)
                        .queryParam("country", addressCached.get("country"))
                        .queryParam("state", addressCached.get("state"))
                        .queryParam("city", addressCached.get("city"))
                        .queryParam("street", addressCached.get("street"))
                        .queryParam("zip", addressCached.get("zip"))
                        .queryParam("effectiveDate", requestedTime)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.matchedAddressData.address.state").isEqualTo("Alaska")
                .jsonPath("$.matchedAddressData.scoring.score").isEqualTo("1.0")
                .jsonPath("$.salesTaxRates.taxRate").isEqualTo(0.2f)
                .jsonPath("$.salesTaxRates.mtaRate").isEqualTo(0f);
    }

    @Order(1)
    @Test
    @Override
    public void findByAddress_CachedAddress_FastTax_Returns200() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_SALES_TAX_RATES_BASE_URL)
                        .queryParam("country", "US")
                        .queryParam("state", "Utah")
                        .queryParam("street", "10 5th Ave")
                        .queryParam("city", "city")
                        .queryParam("zip", "11111")
                        .queryParam("effectiveDate", requestedTime)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.matchedAddressData.address.state").isEqualTo("Utah")
                .jsonPath("$.matchedAddressData.scoring.score").isEqualTo("1.0")
                .jsonPath("$.salesTaxRates.taxRate").isEqualTo(0.08);
    }

    // PUT TESTS
    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void update_newInternalRate_Return200() {
        String newZip = "12345";

        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_SALES_TAX_RATES_BASE_URL)
                        .queryParam("status", "NEW")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.createInternalSalesTaxRatesJson("mtaName-Test", newZip))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.salesTaxRates.taxRate").isEqualTo("0.167");
    }

    @Test
    @Override
    @WithMockUser
    public void update_InsertNewInternalRate_StatusWrong_Return400() {
        String zipFound = "12345";
        String errorStatus = "error";

        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_SALES_TAX_RATES_BASE_URL)
                        .queryParam("status", errorStatus)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.createInternalSalesTaxRatesJson("", zipFound))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(3)
    @Test
    @Override
    @WithMockUser
    public void update_ArchiveInternalRate_Return200() {
        String zipFound = "12345";
        String newData = "MTA_NEW";

        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_SALES_TAX_RATES_BASE_URL)
                        .queryParam("status", "ARCHIVE")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.createInternalSalesTaxRatesJson(newData, zipFound))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(response -> {
                    String responseBody = new String(response.getResponseBody());
                    System.out.println("Response ABCD TEST Body: " + responseBody);
                })
                .jsonPath("$.internalSalesTaxRatesMetaData.mtaName").isEqualTo("updated-field-Test");
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void update_UpdateInternalRate_Return200() {
        String zipFound = "12345";

        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_SALES_TAX_RATES_BASE_URL)
                        .queryParam("status", "UPDATE")
                        .queryParam("detailed", "true")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.createInternalSalesTaxRatesJson("updated-field-Test", zipFound))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.internalSalesTaxRatesMetaData.mtaName").isEqualTo("mtaName-Test"); // old data
    }

    @Test
    @Override
    @WithMockUser
    public void update_UpdateInternalRate_NotFound_Return404() {
        String notFoundZip = "11111";

        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_SALES_TAX_RATES_BASE_URL)
                        .queryParam("status", "UPDATE")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.createInternalSalesTaxRatesJson("updated-field-Test", notFoundZip))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void update_ArchiveInternalRate_NotFound_Return404() {
        String zipNotFound = "11111";

        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_SALES_TAX_RATES_BASE_URL)
                        .queryParam("status", "ARCHIVE")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.createInternalSalesTaxRatesJson("updated-field-Test", zipNotFound))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(1)
    @Test
    @Override
    public void findByAddress_CachedAddressByQueryDetailedTrue_Returns200() {
        // Date after the maxEffectiveDate field
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_SALES_TAX_RATES_BASE_URL)
                        .queryParam("country", addressCached.get("country"))
                        .queryParam("state", addressCached.get("state"))
                        .queryParam("city", addressCached.get("city"))
                        .queryParam("street", addressCached.get("street"))
                        .queryParam("zip", addressCached.get("zip"))
                        .queryParam("effectiveDate", requestedTime)
                        .queryParam("detailed", true)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.matchedAddressData.address.state").isEqualTo("Alaska")
                .jsonPath("$.matchedAddressData.scoring.score").isEqualTo("1.0")
                .jsonPath("$.salesTaxRates.taxRate").isEqualTo(0.3f)
                .jsonPath("$.filingMetaData.fipsCounty").isEqualTo("020");
    }
}
