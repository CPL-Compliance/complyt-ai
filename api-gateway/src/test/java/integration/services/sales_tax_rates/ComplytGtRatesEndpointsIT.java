package integration.services.sales_tax_rates;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComplytGtRatesEndpointsIT extends TestContainersInitializerIT implements ComplytGtRatesEndpointsITTemplate {


    @Order(1)
    @Test
    @Override
    public void findByAddress_FindsGtAddressWithCountryAndRegion_ReturnsComplytGtRates() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_GT_RATES_BASE_URL)
                        .queryParam("country", "Canada")
                        .queryParam("region", "Quebec")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.gtAddress.country").isEqualTo("Canada")
                .jsonPath("$.gtAddress.region").isEqualTo("Quebec")
                .jsonPath("$.gtRates.taxRate").isEqualTo("0.14975")
                .jsonPath("$.gtRates.countryRate").isEqualTo("0.05")
                .jsonPath("$.gtRates.regionRate").isEqualTo("0.0975");
    }

    @Order(1)
    @Test
    @Override
    public void findByAddress_FindsGtAddressWithOnlyCountry_ReturnsComplytGtRates() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_GT_RATES_BASE_URL)
                        .queryParam("country", "Armenia")
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.gtAddress.country").isEqualTo("Armenia")
                .jsonPath("$.gtRates.taxRate").isEqualTo("0.18")
                .jsonPath("$.gtRates.countryRate").isEqualTo("0.18");
    }

}