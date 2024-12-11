package integration.services.sales_tax_rates;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import io.grpc.xds.shaded.io.envoyproxy.envoy.api.v2.core.Address;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComplytSalesTaxRatesEndpointsIT extends TestContainersInitializerIT implements ComplytSalesTaxRatesEndpointsITTemplate {

    String requestedTime = "2021-01-01";;
    JSONObject addressCached = TestUtilities.createAddressJsonExample("Anchorage", "USA", "Anchorage", "Alaska", "751-2696 205 E Benson Blvd", "99501", false);
    JSONObject  addressCachedAndFastTax = TestUtilities.createAddressJsonExample("New York", "US", null, "New York", "160 Broadway","10013", false);

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
                        .queryParam("requiredDate", requestedTime)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.address.state").isEqualTo("AK")
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
                        .queryParam("requiredDate", requestedTime)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.address.state").isEqualTo("AK")
                .jsonPath("$.salesTaxRates.taxRate").isEqualTo(0.2f)
                .jsonPath("$.salesTaxRates.mtaRate").isEqualTo(0f);
    }

    // todo
    @Order(1)
//    @Test
    @Override
    public void findByAddress_CachedAddressBySearchIndex_InternalRate_Returns200() {

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
                        .queryParam("requiredDate", requestedTime)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.source").isEqualTo("SERVICE_OBJECT")
                .jsonPath("$.salesTaxRates.taxRate").isEqualTo(0.08);
    }

    @Order(1)
//    @Test
    @Override
    public void findByAddress_CachedAddress_FastTax_Returns400() {
        String zipError = "11111";

        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_SALES_TAX_RATES_BASE_URL)
                        .queryParam("country", addressCachedAndFastTax.get("country"))
                        .queryParam("state", addressCachedAndFastTax.get("state"))
                        .queryParam("zip", zipError)
                        .queryParam("isPartial", true)
                        .queryParam("requiredDate", requestedTime)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isBadRequest();
    }
}
