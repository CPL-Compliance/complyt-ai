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
import integration.MongoContainerInitializerIT;
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
import testUtils.integration_test.templates.economic_nexus.EconomicNexusBySpecificCustomersITTemplate;

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
public class EconomicNexusByPreviousCalenderYearIT extends MongoContainerInitializerIT implements EconomicNexusBySpecificCustomersITTemplate {

    /*
     * State Rule: Florida
     * TimeFrame: Previous Calendar Year
     * Threshold: Amount: 0.1M
     * Customers: Only Retail
     */

    @MockBean
    private StubFastTaxWebClientWrapper stubFastTaxWebClientWrapper;
    @MockBean
    private TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    //Given
    private LocalDateTime referenceDate = LocalDateTime.parse("2021-10-10T07:00:00");
    private MandatoryAddressDto referenceAddress = new MandatoryAddressDto("Miami", "US", null, "FL", "2100 NW 42nd Ave", "33142");
    private UUID customerId = UUID.fromString("59e5b878-3f3d-42f9-a639-e3bbe5665148"); // complytId of an existing customer in the database
    private UUID customerIdOfReseller = UUID.fromString("b351f97d-d605-4eaa-bf69-b246865b0ca3"); // complytId of an existing customer in the database
    private String source = "1";

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));
        when(stubFastTaxWebClientWrapper.findByAddress(any())).thenReturn(Mono.just(ITUtilities.stubFastTaxFlorida()));
    }


    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void upsertTransaction_NewAndDoesntPassedEconomicNexus_Returns201() {
        //Given
        String externalId = "10028";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withUnitPrice(1).withQuantity(10).withTotalPrice(10))
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
    public void upsertTransaction_NewAndCustomerNotIncludedInNexusCalculation_Returns201() {
        // Given
        String externalId = "10029";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerIdOfReseller,
                        ITUtilities.stubItemDto().withUnitPrice(10000).withQuantity(12).withTotalPrice(120000))
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.toString(), LocalDateTime.now().toString()));

        // THen
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
        String externalId = "10021";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withUnitPrice(10000).withQuantity(12).withTotalPrice(120000))
                .withShippingAddress(referenceAddress.withState("Florida")) // Try both state abbreviation and name for passing economic nexus threshold
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
                    assertEquals(receivedSalesTaxTracking.appliedDate(), LocalDateTime.parse("2022-01-01T00:00"));

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
        String externalId = "10022";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.plusMonths(5).toString(), LocalDateTime.now().toString()));

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
                .value(receivedTransaction -> assertEquals(receivedTransaction.salesTax().amount(), 700));
    }

    @Order(5)
    @Test
    @Override
    @WithMockUser
    public void upsertTransaction_NewOutOfRangeOfEconomicNexus_Returns201() {
        // Given
        String externalId = "10023";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.plusMonths(1).toString(), LocalDateTime.now().toString()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source  + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(receivedTransaction -> assertNull(receivedTransaction.salesTax()));
    }

}
