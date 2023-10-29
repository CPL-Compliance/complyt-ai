package integration.scenarios;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.EconomicNexusTrackerDto;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.TimestampsDto;
import com.complyt.v1.models.transaction.MandatoryAddressDto;
import com.complyt.v1.models.transaction.TransactionDto;
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
import testUtils.integration_test.templates.economic_nexus.EconomicNexusByAmountOrCountITTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
public class EconomicNexusByPreviousTwelveMonthsIT extends TestContainersInitializerIT implements EconomicNexusByAmountOrCountITTemplate {

    /*
     * State Rule: Minnesota
     * TimeFrame: Previous Twelve Months
     * Threshold: Amount: 0.1M OR Count: 20 (Changed from 200)
     * Items: Only RETAIL OR MARKETPLACE
     */

    @MockBean
    private TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    // Given
    private final LocalDateTime referenceDate = LocalDateTime.parse("2021-10-10T07:00:00");
    private final MandatoryAddressDto referenceAddress = new MandatoryAddressDto("Minneapolis", "US", null, "Minnesota", "4401 York Ave S", "55410", false);
    private final UUID marketplaceCustomerId = UUID.fromString("e10cd4a2-6a4e-4621-bdad-4860bfa91ecb"); // complytId of an existing customer in the database
    private final UUID retailCustomerId = UUID.fromString("0b5d4d10-a5f1-411b-b11b-6c751441d256"); // complytId of an existing customer in the database
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
    public void upsertTransaction_NewAndDoesntPassedEconomicNexus_Returns201() {
        // Given
        String externalId = "10048";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, marketplaceCustomerId,
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
                .value(receivedTransaction -> assertNull(receivedTransaction.salesTax()));
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
        String externalId = "10041";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, marketplaceCustomerId,
                        ITUtilities.stubItemDto().withQuantity(new BigDecimal(6)).withUnitPrice(new BigDecimal(10000)).withTotalPrice(new BigDecimal(60000)))
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
                .value(receivedTransaction -> assertNull(receivedTransaction.salesTax()));
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
        String externalId = "10042";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, retailCustomerId)
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
    @WithMockUser
    public void upsertTransaction_NewInRangeOfEconomicNexusButMarketplaceCustomer_Returns201WithSalesTax() {
        // Given
        String externalId = "10043";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, marketplaceCustomerId)
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
                .value(receivedTransaction -> assertNull(receivedTransaction.salesTax()));
    }

    @Order(5)
    @Test
    @Override
    @WithMockUser
    public void upsertTransaction_NewOutOfRangeOfEconomicNexus_Returns201() {
        // Given
        String externalId = "10044";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, marketplaceCustomerId)
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

    @Order(6)
    @Test
    @Override
    @WithMockUser
    public void upsertSalesTaxTracking_ResetNexusToNotEstablished_Returns200() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + referenceAddress.state())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(receivedSalesTaxTracking ->
                        webTestClient
                                .mutateWith(csrf())
                                .put()
                                .uri(uriBuilder -> uriBuilder
                                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + referenceAddress.state())
                                        .build())
                                .bodyValue(receivedSalesTaxTracking.withApproved(false).withEconomicNexusTracker(new EconomicNexusTrackerDto(false, LocalDateTime.now())))
                                .accept(MediaType.APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody(SalesTaxTrackingDto.class)
                                .value(updatedSalesTaxTracking -> {
                                    assertFalse(updatedSalesTaxTracking.approved());
                                    assertFalse(updatedSalesTaxTracking.economicNexusTracker().established());
                                }));
    }

    @Order(7)
    @Test
    @Override
    @WithMockUser
    public void upsertTransaction_NewAndPassedNexusByCount_Returns201() {
        // Given
        String externalId = "10046";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, marketplaceCustomerId,
                        ITUtilities.stubItemDto().withQuantity(new BigDecimal(2)).withUnitPrice(new BigDecimal(100)).withTotalPrice(new BigDecimal(200)))
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.toString(), LocalDateTime.now().toString()));

        // Then
        // The database contains 11 transaction created in October 2021
        for (int i = 1; i < 10; i++) {
            final int finalI = i;
            webTestClient
                    .mutateWith(csrf())
                    .put()
                    .uri(uriBuilder -> uriBuilder
                            .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId + finalI)
                            .build())
                    .bodyValue(givenTransaction.withExternalId(externalId + finalI))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(TransactionDto.class)
                    .value(receivedTransaction -> assertNull(receivedTransaction.salesTax()));
        }
    }

    @Order(8)
    @Test
    @Override
    @WithMockUser
    public void getSalesTaxTracking_CheckEconomicNexusEstablishedByCount_Returns200() {
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
                });
    }
}
