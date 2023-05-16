//package integration.scenarios;
//
//import com.complyt.SalesTaxApplication;
//import com.complyt.domain.State;
//import com.complyt.security.TenantResolver;
//import com.complyt.v1.models.MandatoryAddressDto;
//import com.complyt.v1.models.PhysicalNexusTrackerDto;
//import com.complyt.v1.models.SalesTaxTrackingDto;
//import com.complyt.v1.models.TransactionDto;
//import com.complyt.v1.models.timestamps.TimestampsDto;
//import com.complyt.v1.routers.SalesTaxTrackingRouter;
//import com.complyt.v1.routers.TransactionRouter;
//import integration.TestContainersInitializerIT;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import reactor.core.publisher.Mono;
//import testUtils.integration_test.ITUtilities;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
//
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest(classes = {SalesTaxApplication.class})
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@AutoConfigureWebTestClient()
//public class PhysicalNexusIT extends TestContainersInitializerIT implements PhysicalNexusITTemplate {
//
//    /*
//     * State Rule: Georgia
//     * TimeFrame: Current Taxable Year
//     * Threshold: Count: 200 AND Amount: 100000
//     * Items: Only TANGIBLE
//     */
//
//    @MockBean
//    private StubFastTaxWebClientWrapper stubFastTaxWebClientWrapper;
//    @MockBean
//    private TenantResolver tenantResolver;
//    @Autowired
//    private WebTestClient webTestClient;
//
//    private LocalDateTime referenceDate = LocalDateTime.parse("2020-10-01T07:00:00");
//    private MandatoryAddressDto referenceAddress = new MandatoryAddressDto("Atlanta", "US", null, "GA", "50 Upper Alabama St", "30303");
//    private UUID customerId = UUID.fromString("4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5"); // complytId of an existing customer in the database
//    private String source = "1";
//
//
//    @DynamicPropertySource
//    static void setProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
//    }
//
//    @BeforeEach
//    void setup() {
//        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));
//        when(stubFastTaxWebClientWrapper.findByAddress(any())).thenReturn(Mono.just(ITUtilities.stubFastTaxGeorgia()));
//    }
//
//    @Order(1)
//    @Test
//    @Override
//    @WithMockUser
//    public void upsertTransaction_NewAndDoesntHavePhysicalNexus_Returns201NoTaxes() {
//        //Given
//        String externalId = "10061";
//        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
//                .withShippingAddress(referenceAddress);
//
//        // Then
//        webTestClient
//                .mutateWith(csrf())
//                .put()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
//                        .build())
//                .bodyValue(givenTransaction)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBody(TransactionDto.class)
//                .value(transactionDto -> assertNull(transactionDto.salesTax()));
//    }
//
//    @Order(2)
//    @Test
//    @Override
//    @WithMockUser
//    public void upsertSalesTaxTracking_createdPhysicalNexus_Returns200() {
//        //Given
//        State state = new State("GA", "13", "Georgia");
//
//        // Then
//        webTestClient
//                .get()
//                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state.getAbbreviation())
//                        .build())
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .expectBody(SalesTaxTrackingDto.class)
//                .value(salesTaxTrackingDto ->
//
//                        webTestClient
//                                .mutateWith(csrf())
//                                .put()
//                                .uri(uriBuilder -> uriBuilder
//                                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state.getName())
//                                        .build())
//                                .bodyValue(salesTaxTrackingDto
//                                        .withApproved(true)
//                                        .withPhysicalNexusTracker(
//                                                new PhysicalNexusTrackerDto(true, referenceDate))
//                                        .withAppliedDate(referenceDate)
//                                        .withApprovalDate(referenceDate.plusMonths(1)))
//                                .accept(MediaType.APPLICATION_JSON)
//                                .exchange()
//                                .expectStatus().isOk()
//                                .expectBody(SalesTaxTrackingDto.class)
//                                .value(receivedSalesTaxTrackingDto -> assertTrue(receivedSalesTaxTrackingDto.physicalNexusTracker().established())));
//    }
//
//    @Order(3)
//    @Test
//    @Override
//    @WithMockUser
//    public void upsertTransaction_NewAndAfterPhysicalNexus_Returns201WithTaxes() {
//        //Given
//        String externalId = "10062";
//        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
//                .withShippingAddress(referenceAddress)
//                .withExternalTimestamps(new TimestampsDto(referenceDate.plusMonths(2).toString(), referenceDate.toString()));
//
//        // Then
//        webTestClient
//                .mutateWith(csrf())
//                .put()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
//                        .build())
//                .bodyValue(givenTransaction)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBody(TransactionDto.class)
//                .value(transactionDto -> assertEquals(890, transactionDto.salesTax().amount()));
//    }
//
//    @Order(4)
//    @Test
//    @Override
//    @WithMockUser
//    public void upsertTransaction_NewAndBeforeApprovedPhysicalNexus_Returns201NoTaxes() {
//        //Given
//        String externalId = "10063";
//        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
//                .withShippingAddress(referenceAddress)
//                .withExternalTimestamps(new TimestampsDto(referenceDate.plusDays(10).toString(), referenceDate.toString()));
//
//        // Then
//        webTestClient
//                .mutateWith(csrf())
//                .put()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
//                        .build())
//                .bodyValue(givenTransaction)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBody(TransactionDto.class)
//                .value(transactionDto -> assertNull(transactionDto.salesTax()));
//    }
//
//    @Order(5)
//    @Test
//    @Override
//    @WithMockUser
//    public void upsertSalesTaxTracking_ApprovedDateBeforeAppliedDate_Returns200() {
//        //Given
//        State state = new State("GA", "13", "Georgia");
//
//        // Then
//        webTestClient
//                .get()
//                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state.getAbbreviation())
//                        .build())
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .expectBody(SalesTaxTrackingDto.class)
//                .value(salesTaxTrackingDto ->
//
//                        webTestClient
//                                .mutateWith(csrf())
//                                .put()
//                                .uri(uriBuilder -> uriBuilder
//                                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state.getName())
//                                        .build())
//                                .bodyValue(salesTaxTrackingDto
//                                        .withApprovalDate(referenceDate.minusMonths(1)))
//                                .accept(MediaType.APPLICATION_JSON)
//                                .exchange()
//                                .expectStatus().isOk()
//                                .expectBody(SalesTaxTrackingDto.class)
//                                .value(receivedSalesTaxTrackingDto -> assertTrue(receivedSalesTaxTrackingDto.appliedDate()
//                                        .isAfter(receivedSalesTaxTrackingDto.approvalDate()))));
//    }
//
//    @Order(6)
//    @Test
//    @Override
//    @WithMockUser
//    public void upsertTransaction_NewAndBeforeAppliedPhysicalNexus_Returns201NoTaxes() {
//        //Given
//        String externalId = "10064";
//        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
//                .withShippingAddress(referenceAddress)
//                .withExternalTimestamps(new TimestampsDto(referenceDate.minusDays(10).toString(), referenceDate.toString()));
//
//        // Then
//        webTestClient
//                .mutateWith(csrf())
//                .put()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
//                        .build())
//                .bodyValue(givenTransaction)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBody(TransactionDto.class)
//                .value(transactionDto -> assertNull(transactionDto.salesTax()));
//    }
//
//    @Order(7)
//    @Test
//    @Override
//    @WithMockUser
//    public void upsertSalesTaxTracking_EnforcesSalesTaxToFalse_Returns200() {
//        //Given
//        State state = new State("GA", "13", "Georgia");
//
//        // Then
//        webTestClient
//                .get()
//                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state.getAbbreviation())
//                        .build())
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .expectBody(SalesTaxTrackingDto.class)
//                .value(salesTaxTrackingDto ->
//
//                        webTestClient
//                                .mutateWith(csrf())
//                                .put()
//                                .uri(uriBuilder -> uriBuilder
//                                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state.getName())
//                                        .build())
//                                .bodyValue(salesTaxTrackingDto
//                                        .withEnforcesSalesTax(false))
//                                .accept(MediaType.APPLICATION_JSON)
//                                .exchange()
//                                .expectStatus().isOk()
//                                .expectBody(SalesTaxTrackingDto.class)
//                                .value(receivedSalesTaxTrackingDto ->
//                                        assertFalse(receivedSalesTaxTrackingDto.enforcesSalesTax())));
//    }
//
//    @Order(8)
//    @Test
//    @Override
//    @WithMockUser
//    public void upsertTransaction_WithPhysicalNexusButNoEnforcedSalesTax_Returns201NoTaxes() {
//        //Given
//        String externalId = "10065";
//        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
//                .withShippingAddress(referenceAddress)
//                .withExternalTimestamps(new TimestampsDto(referenceDate.plusMonths(1).toString(), referenceDate.toString()));
//
//        // Then
//        webTestClient
//                .mutateWith(csrf())
//                .put()
//                .uri(uriBuilder -> uriBuilder
//                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
//                        .build())
//                .bodyValue(givenTransaction)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBody(TransactionDto.class)
//                .value(transactionDto -> assertNull(transactionDto.salesTax()));
//    }
//}
