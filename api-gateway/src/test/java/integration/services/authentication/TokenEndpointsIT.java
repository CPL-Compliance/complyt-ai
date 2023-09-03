package integration.services.authentication;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static integration.test_utils.TestUtilities.API_KEY;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

public class TokenEndpointsIT extends TestContainersInitializerIT {
    String complytClientId = "e2019b6f-a8c1-415c-b8b0-3fd6725c9a67";

    @Test
    public void findByAddress_FirstAddressToInsert_InsertsNewComplytSalesTaxRatesAndReturnsIt() {
        WEB_TEST_CLIENT
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TOKEN_BASE_URL)
                        .queryParam("api_key", API_KEY)
                        .build())
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.complytClientId").isEqualTo(complytClientId);
    }
}
