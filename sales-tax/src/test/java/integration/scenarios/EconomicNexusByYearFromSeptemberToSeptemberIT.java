package integration.scenarios;

import com.complyt.SalesTaxApplication;
import com.complyt.business.sales_tax.sales_tax_web_clients.StubFastTaxWebClientWrapper;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.MandatoryAddressDto;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.TransactionDto;
import com.complyt.v1.models.timestamps.TimestampsDto;
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
import testUtils.integration_test.templates.economic_nexus.EconomicNexusOnlyTaxableItemsITTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
public class EconomicNexusByYearFromSeptemberToSeptemberIT extends TestContainersInitializerIT implements EconomicNexusOnlyTaxableItemsITTemplate {

    /*
     * State Rule: Connecticut
     * TimeFrame: Year From September To September
     * Threshold: Count: 20 (Changed from 200 AND Amount: 0.1M)
     * Customers: Only RETAIL OR MARKETPLACE
     * Items: Only Taxable
     */

    @MockBean
    private StubFastTaxWebClientWrapper stubFastTaxWebClientWrapper;
    @MockBean
    private TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    // Given
    private LocalDateTime referenceDate = LocalDateTime.parse("2019-10-01T07:00:00");
    private MandatoryAddressDto referenceAddress = new MandatoryAddressDto("West Haven", "US", null, "CT", "300 Boston Post Rd", "06516");
    private UUID customerId = UUID.fromString("49755739-892a-4807-882c-68b0e209a980"); // complytId of an existing customer in the database
    private String source = "1";

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));
        when(stubFastTaxWebClientWrapper.findByAddress(any())).thenReturn(Mono.just(ITUtilities.stubFastTaxConnecticut()));
    }


    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void upsertTransaction_NewAndDoesntPassedEconomicNexus_Returns201() {
        // Given
        String externalId = "10058";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withUnitPrice(4).withQuantity(15000).withTotalPrice(60000))
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

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void upsertTransaction_NewAndNotTaxableItem_Returns201() {
        // Given
        String externalId = "10059";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withQuantity(6).withUnitPrice(10000).withTotalPrice(60000).withTaxCode("C?S1")) // A tax code which in CT is not taxable
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.toString(), LocalDateTime.now().toString()));

        // Then
        // The database contains 17 transaction created in June 2020
        for (int i = 1; i < 4; i++) {
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
        String externalId = "10052";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withUnitPrice(10000).withQuantity(12).withTotalPrice(120000))
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.toString(), LocalDateTime.now().toString()));

        // Then
        for (int i = 1; i < 3; i++) {
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
                    assertEquals(receivedSalesTaxTracking.appliedDate(), LocalDateTime.parse("2020-10-01T00:00"));

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
        String externalId = "10053";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.plusYears(1).toString(), LocalDateTime.now().toString()));

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
                .value(receivedTransaction -> assertEquals(receivedTransaction.salesTax().amount(), 635));
    }

    @Order(5)
    @Test
    @Override
    @WithMockUser
    public void upsertTransaction_NewOutOfRangeOfEconomicNexus_Returns201() {
        // Given
        String externalId = "10054";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.plusMonths(7).toString(), LocalDateTime.now().toString()));

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
