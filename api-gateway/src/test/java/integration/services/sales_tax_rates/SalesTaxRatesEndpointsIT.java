package integration.services.sales_tax_rates;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SalesTaxRatesEndpointsIT extends TestContainersInitializerIT implements SalesTaxRatesEndpointsITTemplate {

    @Order(1)
    @Test
    @Override
    public void findByAddress_FirstAddressToInsert_InsertsNewComplytSalesTaxRatesAndReturnsIt() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_RATES_BASE_URL)
                        .queryParam("country", "US")
                        .queryParam("state", "NY")
                        .queryParam("city", "New York")
                        .queryParam("street", "160 Broadway")
                        .queryParam("zip", "10038")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.address.county").isEqualTo("Arapahoe")
                .jsonPath("$.salesTaxRates.taxRate").isEqualTo(0.04f);
    }

    @Order(2)
    @Test
    @Override
    public void findByAddress_SecondAddressToInsert_InsertsNewComplytSalesTaxRatesAndReturnsIt() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.SALES_TAX_RATES_BASE_URL)
                        .queryParam("country", "US")
                        .queryParam("state", "NY")
                        .queryParam("city", "cityOfSecondAddress")
                        .queryParam("street", "160 Broadway")
                        .queryParam("zip", "10038")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.address.county").isEqualTo("Arapahoe")
                .jsonPath("$.salesTaxRates.taxRate").isEqualTo(0.04f);
    }
}