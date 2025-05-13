package integration.scenarios;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.PhysicalNexusTrackerDto;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.TimestampsDto;
import com.complyt.v1.models.transaction.ShippingAddressDto;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.models.transaction.TransactionTypeDto;
import com.complyt.v1.routers.SalesTaxTrackingRouter;
import com.complyt.v1.routers.TransactionRouter;
import integration.TestContainersInitializerIT;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import testUtils.integration_test.ITUtilities;
import testUtils.annotations.WithMockJwt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {SalesTaxApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient()
public class RefundIT extends TestContainersInitializerIT implements RefundITTemplate {


    /*
     * State Rule: Maine
     * TimeFrame: Current Calendar Year
     * Threshold: Count: 200 OR Amount 100000
     * Customers: Only RETAIL OR RESELLER
     */

    @MockBean
    private TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    private final LocalDateTime referenceDate = LocalDateTime.parse("2020-10-01T07:00:00");
    private final ShippingAddressDto referenceAddress = new ShippingAddressDto("Cape Elizabeth", "US", null, "ME", "12 Captain Strout Cir", "", "04107", false, null);
    private final UUID customerId = UUID.fromString("4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5"); // complytId of an existing customer in the database
    private final String source = "1";

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }


    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertTransaction_NotPassingEconomicNexus_Returns201() {
        // Given
        String externalId = "10081";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withTotalPrice(new BigDecimal(30000)).withQuantity(new BigDecimal(2)).withUnitPrice(new BigDecimal(15000)))
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.toString(), LocalDateTime.now().toString()));

        // Then
        webTestClient
                .mutateWith(csrf()).put()
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
    @WithMockJwt
    public void upsertTransaction_RefundBeforeEconomicNexusPassed_Returns201() {
        // Given
        String externalIdOfOriginal = "10081";
        String externalIdOfRefund = "10082";

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalIdOfOriginal)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transactionDto ->

                        webTestClient
                                .mutateWith(csrf())

                                .put()
                                .uri(uriBuilder -> uriBuilder
                                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalIdOfRefund)
                                        .build())
                                .bodyValue(transactionDto
                                        .withComplytId(null)
                                        .withExternalId(externalIdOfRefund)
                                        .withTransactionType(TransactionTypeDto.REFUND)
                                        .withCreatedFrom(externalIdOfOriginal)
                                )
                                .accept(MediaType.APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectBody(TransactionDto.class)
                                .value(receivedTransaction -> assertNull(receivedTransaction.salesTax())));
    }

    @Order(3)
    @Test
    @Override
    @WithMockJwt
    public void upsertTransaction_WouldHavePassedWithoutTheRefund_Returns201() {
        // Given
        String externalId = "10083";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withTotalPrice(new BigDecimal(80000)).withQuantity(new BigDecimal(2)).withUnitPrice(new BigDecimal(40000)))
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.toString(), LocalDateTime.now().toString()));

        // Then
        webTestClient
                .mutateWith(csrf()).put()
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
    @WithMockJwt
    public void getSalesTaxTracking_checkEconomicNexusNotPassed_Returns200() {
        // Given
        StateDto state = new StateDto("ME", "23", "Maine");

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", referenceAddress.country())
                        .queryParam("state", state.name())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(receivedSalesTaxTracking -> {
                    receivedSalesTaxTracking.nexusCalculationSummaries().values().forEach(nexusCalculationSummaryDto ->
                            assertEquals(BigDecimal.valueOf(80000), nexusCalculationSummaryDto.amount()));
                    assertFalse(receivedSalesTaxTracking.economicNexusTracker().established());
                });

    }

    @Order(5)
    @Test
    @Override
    @WithMockJwt
    public void upsertSalesTaxTracking_AddPhysicalNexus_Returns200() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", referenceAddress.country())
                        .queryParam("state", referenceAddress.state())
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
                                        .path(SalesTaxTrackingRouter.BASE_URL)
                                        .queryParam("country", referenceAddress.country())
                                        .queryParam("state", referenceAddress.state())
                                        .build())
                                .bodyValue(receivedSalesTaxTracking
                                        .withApproved(true)
                                        .withPhysicalNexusTracker(
                                                new PhysicalNexusTrackerDto(true, referenceDate))
                                        .withAppliedDate(referenceDate)
                                        .withApprovalDate(referenceDate))
                                .accept(MediaType.APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody(SalesTaxTrackingDto.class)
                                .value(updatedSalesTaxTracking -> assertTrue(updatedSalesTaxTracking.approved())));
    }

    @Order(6)
    @Test
    @Override
    @WithMockJwt
    public void upsertTransaction_NewAfterPhysicalNexus_Returns201WithTaxes() {
        // Given
        String externalId = "10084";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withTotalPrice(new BigDecimal(80000)).withQuantity(new BigDecimal(2)).withUnitPrice(new BigDecimal(40000)))
                .withShippingAddress(referenceAddress)
                .withExternalTimestamps(new TimestampsDto(referenceDate.toString(), LocalDateTime.now().toString()));

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(receivedTransaction -> assertEquals(new BigDecimal("6200"), receivedTransaction.salesTax().amount()));
    }

    @Order(7)
    @Test
    @Override
    @WithMockJwt
    public void upsertTransaction_RefundOfHalfTheAmount_Returns201() {
        // Given
        String externalIdOfOriginal = "10084";
        String externalIdOfRefund = "10085";

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalIdOfOriginal)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transactionDto ->

                        webTestClient
                                .mutateWith(csrf())

                                .put()
                                .uri(uriBuilder -> uriBuilder
                                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalIdOfRefund)
                                        .build())
                                .bodyValue(transactionDto
                                        .withComplytId(null)
                                        .withCustomer(null)
                                        .withExternalId(externalIdOfRefund)
                                        .withTransactionType(TransactionTypeDto.REFUND)
                                        .withCreatedFrom(externalIdOfOriginal)
                                        .withItems(List.of(ITUtilities.stubItemDto().withTotalPrice(new BigDecimal(40000)).withUnitPrice(new BigDecimal(40000))))
                                        .withSalesTax(transactionDto.salesTax().withAmount(transactionDto.salesTax().amount().divide(new BigDecimal(2), RoundingMode.DOWN)))
                                )
                                .accept(MediaType.APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectBody(TransactionDto.class)
                                .value(receivedTransaction -> assertEquals(new BigDecimal("3100"), receivedTransaction.salesTax().amount())));
    }
}
