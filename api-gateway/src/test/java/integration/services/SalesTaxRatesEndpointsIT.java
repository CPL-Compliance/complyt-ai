package integration.services;

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

@SpringBootTest(classes = ApiGatewayApplication.class
        , webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
        , properties = {"server.port=8765", "management.server.port=8765"}
)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(profiles = {"integration-test", "stubFastTax"})
public class SalesTaxRatesEndpointsIT extends TestContainersInitializerIT {

    @Autowired
    WebTestClient webTestClient;

    @Order(-1)
    @Test
    public void checkConnection() {
        while (!IS_SALES_TAX_REGISTERED) {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(TestUtilities.SALES_TAX_RATES_BASE_URL)
                            .build())
                    .headers(headers -> {
                        headers.setBearerAuth(TOKEN);
                        headers.setContentType(MediaType.APPLICATION_JSON);
                    })
                    .exchange()
                    .expectStatus().value(status -> IS_SALES_TAX_REGISTERED = status != 503);
        }
    }

    @Order(1)
    @Test
    @WithMockUser
    public void findByAddress_FirstAddressToInsert_InsertsNewComplytSalesTaxRatesAndReturnsIt() {
        webTestClient
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
                .jsonPath("$.salesTaxRates.taxRate").isEqualTo(0.0775f);
    }

    @Order(2)
    @Test
    @WithMockUser
    public void findByAddress_SecondAddressToInsert_InsertsNewComplytSalesTaxRatesAndReturnsIt() {
        webTestClient
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
                .jsonPath("$.salesTaxRates.taxRate").isEqualTo(0.0775f);
    }
}