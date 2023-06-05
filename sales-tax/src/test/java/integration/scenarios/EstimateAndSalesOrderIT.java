package integration.scenarios;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.*;
import com.complyt.v1.routers.SalesTaxTrackingRouter;
import com.complyt.v1.routers.TransactionRouter;
import integration.TestContainersInitializerIT;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import testUtils.integration_test.ITUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {SalesTaxApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient()
public class EstimateAndSalesOrderIT extends TestContainersInitializerIT implements EstimateAndSalesOrderITTemplate {

    /*
     * State Rule: Kentucky
     * TimeFrame: Current Calendar Year
     * Threshold: Count: 200 OR Amount 100000
     */

    @MockBean
    private TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    private final LocalDateTime referenceDate = LocalDateTime.parse("2020-10-01T07:00:00");
    private final MandatoryAddressDto referenceAddress = new MandatoryAddressDto("Louisville", "US", null, "KY", "2513 Preston Hwy", "40217", false);
    private final UUID customerId = UUID.fromString("4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5"); // complytId of an existing customer in the database
    private final String source = "1";

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));
    }

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void upsertTransaction_SalesOrderOverTheThreshold_Returns201() {
        //Given
        String externalId = "10091";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withTotalPrice(120000).withQuantity(2).withUnitPrice(60000))
                .withTransactionType(TransactionTypeDto.SALES_ORDER)
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> assertNull(transactionDto.salesTax()));
    }

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void upsertTransaction_EstimateOverTheThreshold_Returns201() {
        //Given
        String externalId = "10092";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withTotalPrice(120000).withQuantity(2).withUnitPrice(60000))
                .withTransactionType(TransactionTypeDto.ESTIMATE)
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> assertNull(transactionDto.salesTax()));
    }

    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void getSalesTaxTracking_CheckEconomicNexusNotPassed_Returns200() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + referenceAddress.state())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(receivedSalesTaxTracking -> {
                    assertFalse(receivedSalesTaxTracking.economicNexusTracker().established());
                    assertFalse(receivedSalesTaxTracking.approved());
                });
    }

    @Order(3)
    @Test
    @Override
    @WithMockUser
    public void upsertSalesTaxTracking_AddPhysicalNexus_Returns200() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/state/" + referenceAddress.state())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto ->

                        webTestClient
                                .mutateWith(csrf())
                                .put()
                                .uri(uriBuilder -> uriBuilder
                                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + referenceAddress.state())
                                        .build())
                                .bodyValue(salesTaxTrackingDto
                                        .withApproved(true)
                                        .withPhysicalNexusTracker(
                                                new PhysicalNexusTrackerDto(true, referenceDate))
                                        .withAppliedDate(referenceDate)
                                        .withApprovalDate(referenceDate))
                                .accept(MediaType.APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody(SalesTaxTrackingDto.class)
                                .value(receivedSalesTaxTrackingDto -> assertTrue(receivedSalesTaxTrackingDto.physicalNexusTracker().established())));
    }

    @Order(4)
    @Test
    @Override
    @WithMockUser
    public void upsertTransaction_EstimateAfterNexusApplied_Returns201WthTaxes() {
        //Given
        String externalId = "10093";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withTotalPrice(120000).withQuantity(2).withUnitPrice(60000))
                .withTransactionType(TransactionTypeDto.ESTIMATE)
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> assertEquals(9300, transactionDto.salesTax().amount()));
    }

    @Order(4)
    @Test
    @Override
    @WithMockUser
    public void upsertTransaction_SalesOrderAfterNexusApplied_Returns201WthTaxes() {
        //Given
        String externalId = "10094";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withTotalPrice(120000).withQuantity(2).withUnitPrice(60000))
                .withTransactionType(TransactionTypeDto.SALES_ORDER)
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> assertEquals(9300, transactionDto.salesTax().amount()));
    }
}
