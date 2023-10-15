package integration.scenarios;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.transaction.MandatoryAddressDto;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.models.TimestampsDto;
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
import testUtils.integration_test.templates.economic_nexus.EconomicNexusOnlyTangibleItemsITTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
public class EconomicNexusByCurrentCalenderYearIT extends TestContainersInitializerIT implements EconomicNexusOnlyTangibleItemsITTemplate {

    /*
     * State Rule: New York
     * TimeFrame: Current Calendar Year
     * Threshold: Amount: 0.5M AND Count: 100
     * Items: Only Tangible
     */

    @MockBean
    private TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    // Given
    private LocalDateTime referenceDate = LocalDateTime.parse("2021-10-10T07:00:00");
    private UUID customerId = UUID.fromString("9ff0912a-2d60-4e8a-a6ba-1a9e7385338e"); // complytId of an existing customer in the database
    private MandatoryAddressDto referenceAddress = new MandatoryAddressDto("New York", "US", null, "NY", "20 W 34th St.", "10001", false);
    private String source = "1";

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
//        registry.add("spring.data.mongodb.uri", () -> "mongodb://localhost:27017/sales_tax");
    }

    @BeforeEach
    void setup() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));
    }

    @Order(0)
    @Test
    @WithMockUser
    public void refreshSalesTaxTrackingByStateAndDate_CheckEconomicNexusNotPassed_Returns200() {
        String state = "NY";

        // Then
        webTestClient
                .mutateWith(csrf())
                .mutate().responseTimeout(Duration.ofMinutes(2)).build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh/state/" + state + "/date/" + referenceDate.toLocalDate())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk().expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> LOGGER.info(String.valueOf(salesTaxTrackingDto)));


    }

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void upsertTransaction_NewAndDoesntPassedEconomicNexus_Returns201() {
        // Given
        String externalId = "10038";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withQuantity(new BigDecimal(1)).withUnitPrice(new BigDecimal(10)).withTotalPrice(new BigDecimal(10)))
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.toString(), LocalDateTime.now().toString()));


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
                .value(receivedTransaction ->
                        assertNull(receivedTransaction.salesTax()));

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

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void upsertTransaction_NewAndNotTangibleItem_Returns201() {
        // Given
        String externalId = "10039";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withQuantity(new BigDecimal(5)).withUnitPrice(new BigDecimal(900)).withTotalPrice(new BigDecimal(4500)).withTaxCode("C2S1"))
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.toString(), LocalDateTime.now().toString()));

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
                .value(receivedTransaction ->
                        assertNull(receivedTransaction.salesTax()));

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
    public void upsertTransaction_NewAndPassedEconomicNexus_Returns201() {
        // Given
        String externalId = "10032";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withQuantity(new BigDecimal(5)).withUnitPrice(new BigDecimal(900)).withTotalPrice(new BigDecimal(4500)))
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.toString(), LocalDateTime.now().toString()));


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
                .value(receivedTransaction ->
                        assertNull(receivedTransaction.salesTax()));

    }

    @Order(4)
    @Test
    @Override
    @WithMockUser
    public void upsertSalesTaxTracking_ApproveEconomicNexus_Returns200() {
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
                    assertTrue(receivedSalesTaxTracking.economicNexusTracker().established());
                    assertEquals(receivedSalesTaxTracking.economicNexusTracker().establishedDate(), LocalDateTime.parse(referenceDate.toString()));
                    assertEquals(receivedSalesTaxTracking.appliedDate(), LocalDateTime.parse(referenceDate.toString()));

                    webTestClient
                            .mutateWith(csrf())
                            .put()
                            .uri(uriBuilder -> uriBuilder
                                    .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + referenceAddress.state())
                                    .build())
                            .bodyValue(receivedSalesTaxTracking.withApproved(true))
                            .accept(MediaType.APPLICATION_JSON)
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody(SalesTaxTrackingDto.class)
                            .value(updatedSalesTaxTracking -> assertTrue(updatedSalesTaxTracking.approved()));
                });
    }

    @Order(5)
    @Test
    @Override
    @WithMockUser
    public void upsertTransaction_NewInRangeOfEconomicNexus_Returns201WithSalesTax() {
        // Given
        String externalId = "10033";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.plusMonths(1).toString(), LocalDateTime.now().toString()));

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
                .value(receivedTransaction -> assertEquals(new BigDecimal("775.0000"), receivedTransaction.salesTax().amount()));
    }

    @Order(5)
    @Test
    @Override
    @WithMockUser
    public void upsertTransaction_NewOutOfRangeOfEconomicNexus_Returns201() {
        // Given
        String externalId = "10034";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.minusMonths(1).toString(), LocalDateTime.now().toString()));

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
                .value(receivedTransaction -> assertNull(receivedTransaction.salesTax()));
    }
}
