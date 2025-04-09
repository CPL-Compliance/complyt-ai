package integration.scenarios;

import com.complyt.SalesTaxApplication;
import com.complyt.domain.State;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.transaction.ShippingAddressDto;
import com.complyt.v1.models.transaction.ShippingFeeDto;
import com.complyt.v1.models.transaction.TransactionDto;
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
import testUtils.integration_test.WithMockJwt;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {SalesTaxApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient()
public class ShippingFeesIT extends TestContainersInitializerIT implements ShippingFeesITTemplate {

    /*
     * State Rule: Indiana
     * TimeFrame: Current Calendar Year
     * Threshold: Count: 200 OR Amount 100000
     * Customers: Only RETAIL OR RESELLER
     * Items: Only TANGIBLE (Changed from TANGIBLE OR TAXABLE)
     */

    @MockBean
    private TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    private final ShippingAddressDto referenceAddress = new ShippingAddressDto("Indianapolis", "US", null, "IN", "705 Riley Hospital Dr", "", "46202", false, null);
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
    public void upsertTransaction_ShippingFeesNotPassingEconomicNexus_Returns200NoTaxes() {
        //Given (C?S1 Tangible)
        String externalId = "10071";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingFee(new ShippingFeeDto(false, BigDecimal.ZERO,
                        new BigDecimal(60000), BigDecimal.ZERO, null, null, null,
                        "C?S1", null, null))
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
                .value(transactionDto -> assertNull(transactionDto.shippingFee().salesTaxRates()));
    }

    @Override
    @WithMockJwt
    public void upsertTransaction_UpdateToHaveNoShippingFees_Returns200NoTaxes() {
        String externalId = "10071";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingFee(null)
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
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> assertNull(transactionDto.shippingFee().salesTaxRates()));
    }

    @Override
    @WithMockJwt
    public void upsertTransaction_ShippingFeesNotPassingEconomicNexusAfterPreviousSubtraction_Returns200NoTaxes() {
        //Given (C?S1 Tangible)
        String externalId = "10072";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingFee(new ShippingFeeDto(false, BigDecimal.ZERO,
                        new BigDecimal(60000), BigDecimal.ZERO, null, null, null,
                        "C?S1", null, null))
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
                .value(transactionDto -> assertNull(transactionDto.shippingFee().salesTaxRates()));
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertTransaction_ShippingFeesNotTangibleAndNotAddedToThresholdCalculation_Returns200NoTaxes() {

        //Given (C6S1 Intangible)
        String externalId = "10073";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingFee(new ShippingFeeDto(false, BigDecimal.ZERO,
                        new BigDecimal(35000), BigDecimal.ZERO, null, null, null,
                        "C6S1", null, null))
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
                .value(transactionDto -> assertNull(transactionDto.shippingFee().salesTaxRates()));
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getSalesTaxTracking_checkEconomicNexusNotPassed_Returns200() {
        String country = referenceAddress.country();
        String state = referenceAddress.state();
        webTestClient

                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", country)
                        .queryParam("state", state)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> assertFalse(salesTaxTrackingDto.economicNexusTracker().established()));

    }

    @Order(3)
    @Test
    @Override
    @WithMockJwt
    public void upsertTransaction_ShippingFeesPassingEconomicNexus_Returns200NoTaxes() {

        //Given (C?S1 Tangible)
        String externalId = "10074";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingFee(new ShippingFeeDto(false, BigDecimal.ZERO,
                        new BigDecimal(35000), BigDecimal.ZERO, null, null, null,
                        "C?S1", null, null))
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
                .value(transactionDto -> assertNull(transactionDto.shippingFee().salesTaxRates()));
    }

    @Order(4)
    @Test
    @Override
    @WithMockJwt
    public void upsertSalesTaxTracking_ApproveEconomicNexus_Returns200() {
        //Given
        State state = new State("IN", "18", "Indiana");
        String country = referenceAddress.country();
        String stateAbbreviation = state.getAbbreviation();

        // Then
        webTestClient

                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", country)
                        .queryParam("state", stateAbbreviation)
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
                                        .path(SalesTaxTrackingRouter.BASE_URL)
                                        .queryParam("country", country)
                                        .queryParam("state", stateAbbreviation)
                                        .build())
                                .bodyValue(salesTaxTrackingDto
                                        .withApproved(true))
                                .accept(MediaType.APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody(SalesTaxTrackingDto.class)
                                .value(receivedSalesTaxTrackingDto -> assertTrue(receivedSalesTaxTrackingDto.economicNexusTracker().established())));
    }

    @Order(5)
    @Test
    @Override
    @WithMockJwt
    public void upsertTransaction_ShippingFeesAfterNexusPassed_Returns200WithTaxes() {
        //Given (C?S1 Tangible)
        String externalId = "10075";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingFee(new ShippingFeeDto(false, BigDecimal.ZERO,
                        new BigDecimal(10000), BigDecimal.ZERO, null, null, null,
                        "C?S1", null, null))
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
                .value(transactionDto -> {
                    assertNotNull(transactionDto.shippingFee().salesTaxRates());
                    assertEquals(new BigDecimal("1550"), transactionDto.salesTax().amount());
                });
    }

    @Order(5)
    @Test
    @Override
    @WithMockJwt
    public void upsertTransaction_ShippingFeesNotTaxableAfterNexusPassed_Returns200NoTaxes() {
        //Given (C7S1 Nontaxable)
        String externalId = "10076";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingFee(new ShippingFeeDto(false, BigDecimal.ZERO,
                        new BigDecimal(10000), BigDecimal.ZERO, null, null, null,
                        "C7S1", null, null))
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
                .value(transactionDto -> {
                    assertEquals(BigDecimal.ZERO, transactionDto.shippingFee().salesTaxRates().taxRate());
                    assertEquals(new BigDecimal("775"), transactionDto.salesTax().amount());
                });
    }

    @Order(5)
    @Test
    @Override
    @WithMockJwt
    public void upsertTransaction_ShippingFeesWithManualSalesTaxRate_Returns200WithManualTaxes() {
        //Given (C?S1 Tangible)
        String externalId = "10077";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withUnitPrice(new BigDecimal(10)).withTotalPrice(new BigDecimal(10)))
                .withShippingFee(new ShippingFeeDto(true, new BigDecimal("0.15"),
                        new BigDecimal(10000), BigDecimal.ZERO, null, null, null,
                        "C?S1", null, null))
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
                .value(transactionDto -> {
                    assertNotNull(transactionDto.shippingFee().salesTaxRates());
                    assertEquals(new BigDecimal("1500.775"), transactionDto.salesTax().amount());
                });
    }
}
