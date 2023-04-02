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
import integration.MongoContainerInitializer;
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
import testUtils.it.ITUtilities;
import testUtils.it.templates.economic_nexus.EconomicNexusITTemplate;
import testUtils.it.templates.economic_nexus.EconomicNexusOnlyTangibleItemsITTemplate;

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
public class EconomicNexusByCurrentCalenderYearIT extends MongoContainerInitializer implements EconomicNexusOnlyTangibleItemsITTemplate {

    /*
     * State Rule: New York
     * TimeFrame: Current Calendar Year
     * Threshold: Amount: 0.5M AND Count: 100
     * Items: Only Tangible
     */

    @MockBean
    private StubFastTaxWebClientWrapper stubFastTaxWebClientWrapper;
    @MockBean
    private TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;


    private LocalDateTime referenceDate = LocalDateTime.parse("2021-10-10T07:00:00");
    private UUID customerId = UUID.fromString("9ff0912a-2d60-4e8a-a6ba-1a9e7385338e");

    private MandatoryAddressDto referenceAddress = new MandatoryAddressDto("New York", "US", null, "NY", "20 W 34th St.", "10001");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));
        when(stubFastTaxWebClientWrapper.findByAddress(any())).thenReturn(Mono.just(ITUtilities.stubFastTaxNewYork()));
    }


    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void upsertTransaction_NewAndDoesntPassedEconomicNexus_Returns201() {
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto("10038", customerId,
                        ITUtilities.stubItemDto().withQuantity(1).withUnitPrice(10).withTotalPrice(10))
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.toString(), LocalDateTime.now().toString()));


        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/1/externalId/10038")
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
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/NY")
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
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto("10039", customerId,
                        ITUtilities.stubItemDto().withQuantity(5).withUnitPrice(900).withTotalPrice(4500).withTaxCode("C2S1"))
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.toString(), LocalDateTime.now().toString()));


        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/1/externalId/10039")
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
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/NY")
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
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/NY")
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
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto("10032", customerId,
                        ITUtilities.stubItemDto().withQuantity(5).withUnitPrice(900).withTotalPrice(4500))
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.toString(), LocalDateTime.now().toString()));


        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/1/externalId/10032")
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
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/NY")
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
                                    .path(SalesTaxTrackingRouter.BASE_URL + "/state/NY")
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
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto("10033", customerId)
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.plusMonths(1).toString(), LocalDateTime.now().toString()));

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/1/externalId/10033")
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(receivedTransaction -> assertEquals(receivedTransaction.salesTax().amount(), 887.5));
    }

    @Order(5)
    @Test
    @Override
    @WithMockUser
    public void upsertTransaction_NewOutOfRangeOfEconomicNexus_Returns201() {
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto("10034", customerId)
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.minusMonths(1).toString(), LocalDateTime.now().toString()));

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/1/externalId/10034")
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(receivedTransaction -> assertNull(receivedTransaction.salesTax()));
    }
}
