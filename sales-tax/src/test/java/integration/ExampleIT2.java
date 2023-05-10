package integration;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.MandatoryAddressDto;
import com.complyt.v1.models.TransactionDto;
import com.complyt.v1.routers.TransactionRouter;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import testUtils.integration_test.ITUtilities;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = SalesTaxApplication.class
        , webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
        , properties = {"server.port=9898", "management.server.port=9898"}
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(profiles = {"integration-test", "stubFastTax"})
public class ExampleIT2 extends TestContainersInitializerIT {

    @MockBean
    TenantResolver tenantResolver;
    @MockBean
    JwtDecoder jwtDecoder;

    // Given
    private WebTestClient webTestClient;
    private UUID customerId = UUID.fromString("4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5"); // complytId of an existing customer in the database
    private MandatoryAddressDto referenceAddress = new MandatoryAddressDto("Phoenix", "US", null, "AZ", "3400 E Sky Harbor Blvd", "85034");
    private String source = "1";

    @BeforeEach
    void setup() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));
        webTestClient = WebTestClient.bindToServer().baseUrl(String.format(
                "http://%s:%d/",
                API_GATEWAY_CONTAINER.getHost(),
                API_GATEWAY_CONTAINER.getFirstMappedPort()
        )).build();
    }

    @Order(-1)
    @Test
    public void checkConnection() {
        while (isServiceRouted) {
            webTestClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/customers")
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().value(status -> isServiceRouted = status == 503);
        }
    }

    @Order(3)
    @Test
    @WithMockUser(authorities = {"SCOPE_read:transaction"})
    public void sendRequest() {
        String externalId = "10010";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transactionDto ->
                        assertEquals(transactionDto.externalId(), externalId));
    }

    @Order(2)
    @Test
    @WithMockUser(authorities = {"SCOPE_create:transaction"})
    public void sendPutRequest() {
        // Given
        String externalId = "10002";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(givenTransaction)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transactionDto ->
                        assertEquals(transactionDto.externalId(), externalId));
    }
}
