package integration;


import io.complyt.apigateway.ApiGatewayApplication;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = ApiGatewayApplication.class
        , webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
        , properties = {"server.port=8765", "management.server.port=8765"}
)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(profiles = {"integration-test", "stubFastTax"})
public class ExampleIT extends TestContainersInitializerIT {

    @MockBean
    JwtDecoder jwtDecoder;

    @Autowired
    WebTestClient webTestClient;

    @Order(-1)
    @Test
    public void checkConnection() {
        while (!isSalesTaxRegistered) {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/customers")
                            .build())
                    .headers(headers -> headers
                            .setBearerAuth(token))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().value(status -> isSalesTaxRegistered = status != 503);
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
                        .setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(string -> System.out.println(string.toString()));

    }

    @Test
    @Order(4)
    public void hold() {
        while (true) ;
    }
}
