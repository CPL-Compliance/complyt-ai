package integration;


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

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@SpringBootTest(classes = ApiGatewayApplication.class
        , webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
        , properties = {"server.port=8765", "management.server.port=8765"}
)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(profiles = {"integration-test", "stubFastTax"})
public class ExampleIT extends TestContainersInitializerIT {

    @Autowired
    WebTestClient webTestClient;

    @Order(-1)
    @Test
    public void checkConnection() {
        while (!IS_SALES_TAX_REGISTERED) {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/customers")
                            .build())
                    .headers(headers -> headers
                            .setBearerAuth(TOKEN))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().value(status -> IS_SALES_TAX_REGISTERED = status != 503);
        }
    }

    @Order(1)
    @Test
    public void test1() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/customers")
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(string -> System.out.println(string.toString()));

    }

    @Order(1)
    @Test
    public void test2() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/customers")
                        .build())
                .headers(headers -> headers
                        .setBearerAuth(TOKEN_NO_SCOPES))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();

    }

    @Test
    @WithMockUser
    @Order(1)
    public void upsertByExternalIdAndSource_DoesntExistsAndCustomerDoesntExists_Returns404() {
        String externalId = "10001";

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.TRANSACTION_BASE_URL + "/source/" + "1" + "/externalId/" + externalId)
                        .build())
                .headers(headers -> {
                            headers.setBearerAuth(TOKEN);
                            headers.setContentType(MediaType.APPLICATION_JSON);
                        })
                .bodyValue(TestUtilities.transactionJsonExample("10001", TestUtilities.NON_EXISTING_COMPLYT_ID))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(4)
    public void hold() {
        while (true) ;
    }
}
