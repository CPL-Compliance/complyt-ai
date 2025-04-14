package integration.endpoints;

import com.complyt.SalesTaxApplication;
import com.complyt.business.pagination.PaginationConstants;
import com.complyt.business.transaction.BigDecimalProcessor;
import com.complyt.domain.currency.CurrencySource;
import com.complyt.domain.transaction.Transaction;
import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.config.error_messages.NumericErrorMessages;
import com.complyt.v1.exceptions.types.TaxCodeNotValidException;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.TimestampsDto;
import com.complyt.v1.models.matched_address.ScoringDto;
import com.complyt.v1.models.nexus.NexusCalculationSummaryDto;
import com.complyt.v1.models.tax.global_tax.GtRatesDto;
import com.complyt.v1.models.tax.sales_tax.SalesTaxDto;
import com.complyt.v1.models.tax.sales_tax.SalesTaxRatesDto;
import com.complyt.v1.models.transaction.*;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import testUtils.integration_test.ITUtilities;
import testUtils.integration_test.WithMockJwt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;


@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionEndpointsIT extends TestContainersInitializerIT implements TransactionEndpointsITTemplate {

    @MockBean
    TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    // Given
    private final UUID customerId = UUID.fromString("4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5"); // complytId of an existing customer in the database
    private final ShippingAddressDto referenceAddress = new ShippingAddressDto("Phoenix", "US", null, "AZ", "3400 E Sky Harbor Blvd", "", "85034", false, null);
    private final String source = "1";
    private final SalesTaxRatesDto salesTaxRatesDto = ITUtilities.createSalesTaxRatesDto();
    private SalesTaxDto expectedSt;
    private ScoringDto scoringDto = ITUtilities.createScoringDto();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        expectedSt = new SalesTaxDto(null, new BigDecimal("775"), new BigDecimal("0.07750"), salesTaxRatesDto, null);
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    /*
     This transaction's customer has an exemption in state PA with validation dates of:
     fromDate: 2025-01-01, toDate: 26-01-01, therefore transaction is sales-tax exempt
    */
    public void upsertByExternalIdAndSource_CustomerIsExemptByStateAndDate_ReturnsNonTaxableTransaction() {
        String externalId = "nonExistingTransactionID";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("fresno", "US", null, "PA", "st", "", "12345", false, null))
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

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

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_NonUsaCountry_ReturnsTaxableTransaction() {
        String externalId = "newNonExistingTransactionID";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto(null, "Canada", null, null, "", "", "12345", false, null));

        SalesTaxDto expectedSalesTax = new SalesTaxDto(null, new BigDecimal("1497.5"), BigDecimal.valueOf(0.14975), null, new GtRatesDto(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.0975), BigDecimal.valueOf(0.14975)));

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
                .value(transactionDto -> assertEquals(expectedSalesTax, transactionDto.salesTax()));
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaShippingAddressWithEurCurrency_ReturnsTransactionWithExchangeRateInfo() {
        String externalId = "newNonExistingTransactionWithEurCurrency";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("Phoenix", "US", null, "AZ", "3400 E Sky Harbor Blvd", "", "85034", false, null))
                .withCurrency("EUR");
        ExchangeRateInfoDto exchangeRateInfoDto = ITUtilities.createExchangeRateInfoDto(BigDecimal.valueOf(11076.1), BigDecimal.valueOf(858.39775), BigDecimal.valueOf(11934.49775), "EUR", "USD", BigDecimal.valueOf(1.10761), CurrencySource.COMPLYT, false, LocalDateTime.parse(givenTransaction.externalTimestamps().createdDate()));


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
                    assertEquals(expectedSt, transactionDto.salesTax());
                    assertEquals("EUR", transactionDto.currency());
                    assertEquals(exchangeRateInfoDto, transactionDto.exchangeRateInfo());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaShippingAddressWithEuroCurrency_ReturnsTransactionWithExchangeRateInfo() {
        String externalId = "newNonExistingTransactionWithEuroCurrency";

        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("Phoenix", "US", null, "AZ", "3400 E Sky Harbor Blvd", "", "85034", false, null))
                .withCurrency("EURO");
        ExchangeRateInfoDto exchangeRateInfoDto = ITUtilities.createExchangeRateInfoDto(BigDecimal.valueOf(11076.1), BigDecimal.valueOf(858.39775), BigDecimal.valueOf(11934.49775), "EUR", "USD", BigDecimal.valueOf(1.10761), CurrencySource.COMPLYT, false, LocalDateTime.parse(givenTransaction.externalTimestamps().createdDate()));

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
                    assertEquals(expectedSt, transactionDto.salesTax());
                    assertEquals("EUR", transactionDto.currency());
                    assertEquals(exchangeRateInfoDto, transactionDto.exchangeRateInfo());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaShippingAddressWithEuroCurrencyAndFutureCreatedDate_ReturnsTransactionWithExchangeRateInfo() { // Future createdDate = Future trandate
        String externalId = "newNonExistingFutureTransactionWithEurCurrency";

        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("Phoenix", "US", null, "AZ", "3400 E Sky Harbor Blvd", "", "85034", false, null))
                .withCurrency("EUR")
                .withExternalTimestamps(new TimestampsDto(LocalDateTime.now().plusDays(1).toString(), LocalDateTime.now().plusDays(1).toString()));
        ExchangeRateInfoDto exchangeRateInfoDto = ITUtilities.createExchangeRateInfoDto(BigDecimal.valueOf(11076.1), BigDecimal.valueOf(858.39775), BigDecimal.valueOf(11934.49775), "EUR", "USD", BigDecimal.valueOf(1.10761), CurrencySource.COMPLYT, true, LocalDate.now().atStartOfDay());

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
                    assertEquals(expectedSt, transactionDto.salesTax());
                    assertEquals("EUR", transactionDto.currency());
                    assertEquals(exchangeRateInfoDto, transactionDto.exchangeRateInfo());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaShippingAddressWithEuroCurrencyAndRefRate_ReturnsTransactionWithExchangeRateInfo() { // refRate is a manually entered rate
        String externalId = "newNonExistingTransactionWithEurCurrencyAndRefRate";

        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("Phoenix", "US", null, "AZ", "3400 E Sky Harbor Blvd", "", "85034", false, null))
                .withCurrency("EUR")
                .withRefRate(BigDecimal.valueOf(2));
        ExchangeRateInfoDto exchangeRateInfoDto = ITUtilities.createExchangeRateInfoDto(BigDecimal.valueOf(20000), BigDecimal.valueOf(1550), BigDecimal.valueOf(21550), "EUR", "USD", BigDecimal.valueOf(2), CurrencySource.CLIENT, false, LocalDateTime.parse(givenTransaction.externalTimestamps().createdDate()));


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
                    assertEquals(expectedSt, transactionDto.salesTax());
                    assertEquals("EUR", transactionDto.currency());
                    assertEquals(exchangeRateInfoDto, transactionDto.exchangeRateInfo());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaShippingAddressWithUsdCurrency_ReturnsTransactionWithoutExchangeRateInfo() {
        String externalId = "newNonExistingTransactionWithUsdCurrency";

        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("Phoenix", "US", null, "AZ", "3400 E Sky Harbor Blvd", "", "85034", false, null))
                .withCurrency("USD");

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
                    assertEquals(expectedSt, transactionDto.salesTax());
                    assertEquals("USD", transactionDto.currency());
                    assertNull(transactionDto.exchangeRateInfo());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaShippingAddressWithUsDollarCurrency_ReturnsTransactionWithoutExchangeRateInfo() {
        String externalId = "newNonExistingTransactionWithUsDollarCurrency";

        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("Phoenix", "US", null, "AZ", "3400 E Sky Harbor Blvd", "", "85034", false, null))
                .withCurrency("US Dollar");

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
                    assertEquals(expectedSt, transactionDto.salesTax());
                    assertEquals("USD", transactionDto.currency());
                    assertNull(transactionDto.exchangeRateInfo());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaShippingAddressWithNullCurrency_ReturnsTransactionWithoutExchangeRateInfo() {
        String externalId = "newNonExistingTransactionWithNullCurrency";

        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("Phoenix", "US", null, "AZ", "3400 E Sky Harbor Blvd", "", "85034", false, null))
                .withCurrency(null);

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
                    assertEquals(expectedSt, transactionDto.salesTax());
                    assertNull(transactionDto.currency());
                    assertNull(transactionDto.exchangeRateInfo());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaShippingAddressWithNullCurrencyAndRefRate_ReturnsTransactionWithoutExchangeRateInfo() {
        String externalId = "newNonExistingTransactionWithNullCurrencyAndRefRate";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("Phoenix", "US", null, "AZ", "3400 E Sky Harbor Blvd", "", "85034", false, null))
                .withCurrency(null)
                .withRefRate(BigDecimal.valueOf(2));

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
                    assertEquals(expectedSt, transactionDto.salesTax());
                    assertNull(transactionDto.currency());
                    assertNull(transactionDto.exchangeRateInfo());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_NonUsaCountryAndRegion_ReturnsTaxableTransaction() {
        String externalId = "newNonUsaWithShippingExistingTransactionID";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDtoNonUsaCountry(externalId, customerId)
                .withShippingFee(ITUtilities.stubShippingFeeDto());

        SalesTaxDto expectedSt = new SalesTaxDto(null, new BigDecimal("1548.75"), new BigDecimal("0.14750"), null, new GtRatesDto(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.0975), BigDecimal.valueOf(0.14975)));
        GtRatesDto shippingGtRates = new GtRatesDto(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.0975), BigDecimal.valueOf(0.1475));

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
                    assertEquals(expectedSt, transactionDto.salesTax());
                    assertNotNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().regions());
                    assertNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().cities());
                    assertEquals(shippingGtRates, transactionDto.shippingFee().gtRates());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaCountryTaxInclusive_Returns200() {
        String externalId = "newNonExistingTransactionID";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true);

        givenTransaction = givenTransaction.withShippingAddress(givenTransaction.shippingAddress().withState("CO").withCity("Arvada")); //salestaxtracking is approved and physical

        SalesTaxDto expectedSalesTax = expectedSt.withAmount(new BigDecimal("719.257541"));

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
                .value(transactionDto -> {
                    assertEquals(expectedSalesTax, transactionDto.salesTax());
                    assertNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().regions());
                    assertNotNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().cities());
                    assertEquals(transactionDto.items().get(0).jurisdictionalSalesTaxRules().cities().size(), 1);
                    assertNotNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().cities().get("Arvada"));
                    assertTrue(transactionDto.isTaxInclusive());
                    assertEquals(0, transactionDto.finalTransactionAmount().compareTo(new BigDecimal("10000")));
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaShippingWithSpacesInTheAddress_Returns200() { // Same as the test above just changed the state.abbreviation to check the spaces trimming
        String externalId = "newNonExistingTransactionID";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true);

        givenTransaction = givenTransaction.withShippingAddress(givenTransaction.shippingAddress().withState(" CO ").withCity("Arvada")); //salestaxtracking is approved and physical

        SalesTaxDto expectedSalesTax = expectedSt.withAmount(new BigDecimal("719.257541"));

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
                .value(transactionDto -> {
                    assertEquals(expectedSalesTax, transactionDto.salesTax());
                    assertNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().regions());
                    assertNotNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().cities());
                    assertEquals(transactionDto.items().get(0).jurisdictionalSalesTaxRules().cities().size(), 1);
                    assertNotNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().cities().get("Arvada"));
                    assertTrue(transactionDto.isTaxInclusive());
                    assertEquals(0, transactionDto.finalTransactionAmount().compareTo(new BigDecimal("10000")));
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaTransactionWithNonExistingTaxCode_Returns400BadRequest() { // Same as the test above just changed the state.abbreviation to check the spaces trimming
        String externalId = "newNonExistingTransactionID";
        TransactionDto transactionDto = ITUtilities.stubTransactionDto(externalId, customerId);
        TransactionDto givenTransaction = transactionDto
                .withItems(transactionDto.items().stream().map(itemDto -> itemDto.withTaxCode("Non-existing TaxCode")).collect(Collectors.toList()));

        givenTransaction = givenTransaction.withShippingAddress(givenTransaction.shippingAddress().withState("CO").withCity("Arvada")); //salestaxtracking is approved and physical

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
                .expectStatus().isBadRequest()
                .expectBody(TaxCodeNotValidException.class)
                .value(e ->
                        assertEquals(e.getReason(), "The tax code entered is not recognized"));
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaCountryTaxInclusiveTransactionTypeTaxableRefund_Returns200() {
        String externalId = "newNonExistingTransactionID_TAXABLE_REFUND1";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true)
                .withTransactionType(TransactionTypeDto.TAXABLE_REFUND);

        givenTransaction = givenTransaction.withShippingAddress(givenTransaction.shippingAddress().withState("CO").withCity("Arvada")); //salestaxtracking is approved and physical


        SalesTaxDto expectedSalesTax = expectedSt.withAmount(new BigDecimal("719.257541"))
                .withRate(new BigDecimal("0.07750"));

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
                    assertEquals(expectedSalesTax, transactionDto.salesTax());
                    assertNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().regions());
                    assertNotNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().cities());
                    assertEquals(transactionDto.items().get(0).jurisdictionalSalesTaxRules().cities().size(), 1);
                    assertNotNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().cities().get("Arvada"));
                    assertTrue(transactionDto.isTaxInclusive());
                });
    }


    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaCountryTransactionTypeTaxableRefundDidNotPassNexus_Returns200TransactionWithoutSalesTax() {
        String externalId = "newNonExistingTransactionID_TAXABLE_REFUND2";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true)
                .withTransactionType(TransactionTypeDto.TAXABLE_REFUND);

        givenTransaction = givenTransaction.withShippingAddress(givenTransaction.shippingAddress().withState("DE").withCity("Dover")); //salestaxtracking is approved and physical

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
                    assertNull(transactionDto.salesTax());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaCountryTransactionTypeTaxableRefundDidNotPassNexus_Returns200TransactionAmountShouldBeSubtractedFromNexusSummaryAmount() {
        String externalId = "newNonExistingTransactionID_TAXABLE_REFUND3";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true)
                .withTransactionType(TransactionTypeDto.TAXABLE_REFUND);

        givenTransaction = givenTransaction.withShippingAddress(givenTransaction.shippingAddress().withState("DE").withCity("Dover")); // salestaxtracking is approved and physical

        // Get initial Sales Tax Tracking information
        TransactionDto finalGivenTransaction = givenTransaction;
        SalesTaxTrackingDto initialSalesTaxTrackingDto = webTestClient
                .mutateWith(csrf())

                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", finalGivenTransaction.shippingAddress().state())
                        .queryParam("country", "US")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .returnResult()
                .getResponseBody();

        assert initialSalesTaxTrackingDto != null;
        Map<LocalDate, NexusCalculationSummaryDto> nexusCalculationSummaries = initialSalesTaxTrackingDto.nexusCalculationSummaries();
        NexusCalculationSummaryDto firstDateSummary = nexusCalculationSummaries.values().stream().findFirst().orElse(null);
        BigDecimal amountBeforeTaxableRefund = firstDateSummary != null ? firstDateSummary.amount() : BigDecimal.ZERO;


        // Upsert the transaction
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(finalGivenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> {
                    assertNull(transactionDto.salesTax());

                    // Get Sales Tax Tracking information after taxable refund transaction
                    SalesTaxTrackingDto salesTaxTrackingDtoAfterTaxableRefund = webTestClient
                            .mutateWith(csrf())


                            .get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(SalesTaxTrackingRouter.BASE_URL)
                                    .queryParam("state", finalGivenTransaction.shippingAddress().state())
                                    .queryParam("country", "US")
                                    .build())
                            .accept(MediaType.APPLICATION_JSON)
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody(SalesTaxTrackingDto.class)
                            .returnResult()
                            .getResponseBody();

                    assert salesTaxTrackingDtoAfterTaxableRefund != null;
                    Map<LocalDate, NexusCalculationSummaryDto> nexusCalculationSummariesAfterTaxableRefund = salesTaxTrackingDtoAfterTaxableRefund.nexusCalculationSummaries();
                    NexusCalculationSummaryDto firstDateSummaryAfterTaxableRefund = nexusCalculationSummariesAfterTaxableRefund.values().stream().findFirst().orElse(null);
                    assert firstDateSummaryAfterTaxableRefund != null;
                    BigDecimal amountAfterTaxableRefund = firstDateSummaryAfterTaxableRefund.amount();

                    assertEquals(amountBeforeTaxableRefund.subtract(transactionDto.finalTransactionAmount()), amountAfterTaxableRefund);
                });
    }


    /**
     * Utah salesTaxTracking: New Transaction
     * PhysicalNexusTracker: True
     * EconomicNexusTracker: False
     * Upsert transaction & checks if nexus tracks this transaction
     */
    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_NewTransaction_PhysicalNexusTrackingTrue_salesTaxTrackingGotUpdated() {
        String state = "Utah";
        String country = "USA";
        String externalId = "newTransactionID";
        BigDecimal itemTotalPrice = BigDecimal.valueOf(10000);
        ShippingAddressDto shippingAddress = new ShippingAddressDto(null, country, null, state, null, null, "11111", true, null);
        SalesTaxRatesDto salesTaxRatesDto = ITUtilities.createSalesTaxRatesDto(BigDecimal.valueOf(0.1));
        TransactionDto originalTransaction = ITUtilities.stubTransactionDto(externalId, customerId);
        ItemDto itemDto = originalTransaction.items().get(0).withSalesTaxRates(salesTaxRatesDto);
        TransactionDto givenTransaction = originalTransaction.withItems(Collections.singletonList(itemDto))
                .withTaxInclusive(true).withShippingAddress(shippingAddress);

        // upsertTransaction
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class);

        // Checks salesTaxTracking economicNexus got updated
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", country)
                        .queryParam("state", state)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> {
                    int year = LocalDateTime.parse(givenTransaction.externalTimestamps().createdDate()).getYear();
                    LocalDate sumDate = LocalDate.of(year, 12, 31);
                    assertEquals(salesTaxTrackingDto.nexusCalculationSummaries().get(sumDate).amount(), itemTotalPrice);
                });
    }


    /**
     * Utah salesTaxTracking: Existing Transaction
     * PhysicalNexusTracker: True
     * EconomicNexusTracker: False
     * Upsert transaction & checks if nexus tracks this transaction
     */
    @Order(3)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_ExistingTransaction_PhysicalNexusTrackingTrue_salesTaxTrackingGotUpdated() {
        String state = "Utah";
        String country = "USA";
        String externalId = "newTransactionID"; // already in DB
        BigDecimal passedItemPrice = BigDecimal.valueOf(200);
        ShippingAddressDto shippingAddress = new ShippingAddressDto(null, country, null, state, null, null, "11111", true, null);
        SalesTaxRatesDto salesTaxRatesDto = ITUtilities.createSalesTaxRatesDto(BigDecimal.valueOf(0.1));
        ItemDto itemDto = ITUtilities.stubItemDto().withUnitPrice(passedItemPrice).withTotalPrice(passedItemPrice).withSalesTaxRates(salesTaxRatesDto);
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId, itemDto)
                .withTaxInclusive(true).withShippingAddress(shippingAddress);

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
                .expectBody(TransactionDto.class);

        // Checks salesTaxTracking economicNexus got updated
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", country)
                        .queryParam("state", state)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> {
                    int year = LocalDateTime.parse(givenTransaction.externalTimestamps().createdDate()).getYear();
                    LocalDate sumDate = LocalDate.of(year, 12, 31);
                    assertEquals(salesTaxTrackingDto.nexusCalculationSummaries().get(sumDate).amount(), passedItemPrice);
                });
    }

    @Order(4)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaShippingAddressPassedNexus_Returns200AddressWithCityCounty() {
        String externalId = "newNonExistingTransactionForCityCounty";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("Phoenix", "US", null, "AZ", "3400 E Sky Harbor Blvd", "", "85034", false, null));

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
                .value(transactionDto -> {
                    assertNotNull(transactionDto.salesTax(), "salesTax should not null");
                    assertNotNull(transactionDto.shippingAddress().matchedAddressData().address().county(), "county is missing");
                    assertEquals(scoringDto.withScore(0.95), transactionDto.shippingAddress().matchedAddressData().scoring());
                });
    }

    @Order(3)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaShippingAddressPassedNexus_Returns201AddressWithCityCounty() {
        String externalId = "newNonExistingTransactionForCityCounty";

        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("Phoenix", "US", null, "AZ", "3400 E Sky Harbor Blvd", "", "85034", false, null));

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
                    assertNotNull(transactionDto.salesTax().salesTaxRates(), "salesTaxRates isn't null");
                    assertNotNull(transactionDto.shippingAddress().matchedAddressData().address().county(), "county is missing");
                    assertEquals(scoringDto.withScore(0.95), transactionDto.shippingAddress().matchedAddressData().scoring());
                });
    }

    @Order(3)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaShippingAddressDidNotPassedNexus_Returns201AddressWithCityCounty() {
        String externalId = "newNonExistingTransactionID_TAXABLE_REFUND_FOR_CITY";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true)
                .withTransactionType(TransactionTypeDto.TAXABLE_REFUND);

        givenTransaction = givenTransaction.withShippingAddress(givenTransaction.shippingAddress().withState("DE").withCity("Dover").withCounty(null)); //salestaxtracking is approved and physical

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
                    assertNull(transactionDto.salesTax(), "salesTax shuold be null");
                    assertNotNull(transactionDto.shippingAddress().matchedAddressData().address().county(), "county is missing");
                    assertEquals(scoringDto.withScore(0.9), transactionDto.shippingAddress().matchedAddressData().scoring());
                });
    }

    @Order(3)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaShippingAddressDidNotPassedNexus_Returns200AddressWithCityCounty() {
        String externalId = "newNonExistingTransactionID_TAXABLE_REFUND2";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true)
                .withTransactionType(TransactionTypeDto.TAXABLE_REFUND);

        givenTransaction = givenTransaction.withShippingAddress(givenTransaction.shippingAddress().withState("DE").withCity("Dover").withCounty(null)); //salestaxtracking is approved and physical

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
                .value(transactionDto -> {
                    assertNull(transactionDto.salesTax());
                    assertNotNull(transactionDto.shippingAddress().matchedAddressData().address().county());
                    assertEquals(scoringDto.withScore(0.9), transactionDto.shippingAddress().matchedAddressData().scoring());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaCountryButSentAsAbbreviation_Returns201() {
        String externalId = "newNonExistingTransactionIDUsaAbbreviation";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true);

        givenTransaction = givenTransaction.withShippingAddress(givenTransaction.shippingAddress().withState("CO")
                .withCountry("US")); //salestaxtracking is approved and physical

        SalesTaxDto expectedSalesTax = expectedSt.withAmount(new BigDecimal("719.257541"));

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
                    assertEquals(expectedSalesTax, transactionDto.salesTax());
                    assertNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().regions());
                    assertNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().cities());
                    assertTrue(transactionDto.isTaxInclusive());
                    assertEquals(0, transactionDto.finalTransactionAmount().compareTo(new BigDecimal("10000")));
                    assertEquals("USA", transactionDto.shippingAddress().country());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaCountryWithPartialAddressWithoutState_Returns201() {
        String externalId = "newNonExistingTransactionWithPartialAddressWithoutState";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true);
        ShippingAddressDto partialShippingAddress = new ShippingAddressDto(null, "US", null, null, null, null, "80001", true, null); // zip code belongs to New York
        givenTransaction = givenTransaction.withShippingAddress(partialShippingAddress);

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
                    assertEquals("Colorado", transactionDto.shippingAddress().state());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaCountryWithPartialAddressAndBlankState_Returns201() {
        String externalId = "newNonExistingTransactionWithPartialAddressAndBlankState";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true);
        ShippingAddressDto partialShippingAddress = new ShippingAddressDto(null, "US", null, "", null, null, "80001", true, null); // zip code belongs to New York
        givenTransaction = givenTransaction.withShippingAddress(partialShippingAddress);

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
                    assertEquals("Colorado", transactionDto.shippingAddress().state());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaCountryTransactionWithTaxInclusive_Returns201() {
        String externalId = "newNonExistingUsaCountryTransactionWithTaxInclusive";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true);
        ShippingAddressDto partialShippingAddress = new ShippingAddressDto(null, "US", null, null, null, null, "80001", true, null); // zip code belongs to New York
        givenTransaction = givenTransaction.withShippingAddress(partialShippingAddress);

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
                    BigDecimal amountWithoutTax = transactionDto.finalTransactionAmount().subtract(transactionDto.salesTax().amount());

                    assertEquals(amountWithoutTax, transactionDto.tangibleItemsAmount());
                    assertEquals(amountWithoutTax, transactionDto.taxableItemsAmount());
                    assertEquals(amountWithoutTax, transactionDto.totalItemsAmount());
                    assertEquals(transactionDto.items().get(0).totalPrice(), transactionDto.finalTransactionAmount());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaCountryTransactionWithTaxExclusiveAndNewItems_Returns201() {
        String externalId = "newNonExistingUsaCountryTransactionWithTaxExclusiveAndNewItems";
        BigDecimal firstItemAmount = BigDecimal.valueOf(5000);
        BigDecimal secondItemAmount = BigDecimal.valueOf(10000);
        BigDecimal thirdItemAmount = BigDecimal.valueOf(1000);
        BigDecimal expectedTaxableAmount = firstItemAmount.add(thirdItemAmount);
        BigDecimal expectedTangibleAmount = secondItemAmount.add(thirdItemAmount);
        BigDecimal expectedTotalAmount = firstItemAmount.add(secondItemAmount).add(thirdItemAmount);
        List<ItemDto> items = Arrays.asList(
                ITUtilities.stubItemDto().withTaxCode("C2S1").withTotalPrice(firstItemAmount).withUnitPrice(firstItemAmount), // TAXABLE & INTANGIBLE
                ITUtilities.stubItemDto().withTaxCode("C3S1").withTotalPrice(secondItemAmount).withUnitPrice(secondItemAmount), // NOT_TAXABLE & TANGIBLE
                ITUtilities.stubItemDto().withTaxCode("C4S1").withTotalPrice(thirdItemAmount).withUnitPrice(thirdItemAmount) // TAXABLE & TANGIBLE
        );
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withItems(items)
                .withTaxInclusive(false);
        ShippingAddressDto partialShippingAddress = new ShippingAddressDto(null, "US", null, null, null, null, "80001", true, null); // zip code belongs to New York
        givenTransaction = givenTransaction.withShippingAddress(partialShippingAddress);

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
                    BigDecimal amountWithSalesTax = expectedTotalAmount.add(transactionDto.salesTax().amount());

                    assertEquals(expectedTaxableAmount, transactionDto.taxableItemsAmount());
                    assertEquals(expectedTangibleAmount, transactionDto.tangibleItemsAmount());
                    assertEquals(expectedTotalAmount, transactionDto.totalItemsAmount());
                    assertEquals(amountWithSalesTax, transactionDto.finalTransactionAmount());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaCountryTransactionWithTaxInclusiveAndNewItems_Returns201() {
        String externalId = "newNonExistingUsaCountryTransactionWithTaxInclusiveAndNewItems";
        BigDecimal firstItemAmount = BigDecimal.valueOf(5000);
        BigDecimal firstItemWithoutTax = BigDecimalProcessor.removeTrailingZeros(firstItemAmount.divide(BigDecimal.ONE.add(BigDecimal.valueOf(0.0775)), 6, RoundingMode.HALF_UP));
        BigDecimal secondItemAmount = BigDecimal.valueOf(10000);
        BigDecimal thirdItemAmount = BigDecimal.valueOf(1000);
        BigDecimal thirdItemWithoutTax = BigDecimalProcessor.removeTrailingZeros(thirdItemAmount.divide(BigDecimal.ONE.add(BigDecimal.valueOf(0.0775)), 6, RoundingMode.HALF_UP));
        BigDecimal expectedTaxableAmount = firstItemWithoutTax.add(thirdItemWithoutTax);
        BigDecimal expectedTangibleAmount = secondItemAmount.add(thirdItemWithoutTax);
        BigDecimal expectedTotalAmount = firstItemWithoutTax.add(secondItemAmount).add(thirdItemWithoutTax);
        List<ItemDto> items = Arrays.asList(
                ITUtilities.stubItemDto().withTaxCode("C2S1").withTotalPrice(firstItemAmount).withUnitPrice(firstItemAmount), // TAXABLE & INTANGIBLE
                ITUtilities.stubItemDto().withTaxCode("C3S1").withTotalPrice(secondItemAmount).withUnitPrice(secondItemAmount), // NOT_TAXABLE & TANGIBLE
                ITUtilities.stubItemDto().withTaxCode("C4S1").withTotalPrice(thirdItemAmount).withUnitPrice(thirdItemAmount) // TAXABLE & TANGIBLE
        );
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withItems(items)
                .withTaxInclusive(true);
        ShippingAddressDto partialShippingAddress = new ShippingAddressDto(null, "US", null, null, null, null, "80001", true, null); // zip code belongs to New York
        givenTransaction = givenTransaction.withShippingAddress(partialShippingAddress);

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
                    BigDecimal amountWithSalesTax = BigDecimalProcessor.removeTrailingZeros(expectedTotalAmount.add(transactionDto.salesTax().amount()));

                    assertEquals(expectedTaxableAmount, transactionDto.taxableItemsAmount());
                    assertEquals(expectedTangibleAmount, transactionDto.tangibleItemsAmount());
                    assertEquals(expectedTotalAmount, transactionDto.totalItemsAmount());
                    assertEquals(amountWithSalesTax, transactionDto.finalTransactionAmount());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaCountryTransactionWithTaxInclusiveAndNewItemsAndNoNexus_Returns201() {
        String externalId = "newNonExistingUsaCountryTransactionWithTaxInclusiveAndNewItemsAndNoNexus";
        BigDecimal firstItemAmount = BigDecimal.valueOf(5000);
        BigDecimal secondItemAmount = BigDecimal.valueOf(10000);
        BigDecimal thirdItemAmount = BigDecimal.valueOf(1000);
        BigDecimal expectedTaxableAmount = firstItemAmount.add(thirdItemAmount);
        BigDecimal expectedTangibleAmount = secondItemAmount.add(thirdItemAmount);
        BigDecimal expectedTotalAmount = firstItemAmount.add(secondItemAmount).add(thirdItemAmount);
        List<ItemDto> items = Arrays.asList(
                ITUtilities.stubItemDto().withTaxCode("C5S1").withTotalPrice(firstItemAmount).withUnitPrice(firstItemAmount), // TAXABLE & INTANGIBLE
                ITUtilities.stubItemDto().withTaxCode("C8S1").withTotalPrice(secondItemAmount).withUnitPrice(secondItemAmount), // NOT_TAXABLE & TANGIBLE
                ITUtilities.stubItemDto().withTaxCode("C3S1").withTotalPrice(thirdItemAmount).withUnitPrice(thirdItemAmount) // TAXABLE & TANGIBLE
        );
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withItems(items)
                .withTaxInclusive(true);
        ShippingAddressDto partialShippingAddress = new ShippingAddressDto(null, "US", null, null, null, null, "38603", true, null); // zip code belongs to New York
        givenTransaction = givenTransaction.withShippingAddress(partialShippingAddress);

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
                    assertNull(transactionDto.salesTax());
                    assertEquals(expectedTaxableAmount, transactionDto.taxableItemsAmount());
                    assertEquals(expectedTangibleAmount, transactionDto.tangibleItemsAmount());
                    assertEquals(expectedTotalAmount, transactionDto.totalItemsAmount());
                    assertEquals(expectedTotalAmount, transactionDto.finalTransactionAmount());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_NonUsaCountryTransactionWithTaxInclusiveAndNewItems_Returns201() {
        String externalId = "newNonExistingNonUsaCountryTransactionWithTaxInclusiveAndNewItems";
        BigDecimal firstItemAmount = BigDecimal.valueOf(5000);
        BigDecimal firstItemWithoutTax = BigDecimalProcessor.removeTrailingZeros(firstItemAmount.divide(BigDecimal.ONE.add(BigDecimal.valueOf(0.14975)), 6, RoundingMode.HALF_UP));
        BigDecimal secondItemAmount = BigDecimal.valueOf(10000);
        BigDecimal thirdItemAmount = BigDecimal.valueOf(1000);
        BigDecimal thirdItemWithoutTax = BigDecimalProcessor.removeTrailingZeros(thirdItemAmount.divide(BigDecimal.ONE.add(BigDecimal.valueOf(0.14975)), 6, RoundingMode.HALF_UP));
        BigDecimal expectedTaxableAmount = firstItemWithoutTax.add(thirdItemWithoutTax);
        BigDecimal expectedTangibleAmount = secondItemAmount.add(thirdItemWithoutTax);
        BigDecimal expectedTotalAmount = firstItemWithoutTax.add(secondItemAmount).add(thirdItemWithoutTax);
        List<ItemDto> items = Arrays.asList(
                ITUtilities.stubItemDto().withTaxCode("C6S1").withTotalPrice(firstItemAmount).withUnitPrice(firstItemAmount), // TAXABLE & INTANGIBLE
                ITUtilities.stubItemDto().withTaxCode("C4S1").withTotalPrice(secondItemAmount).withUnitPrice(secondItemAmount), // NOT_TAXABLE & TANGIBLE
                ITUtilities.stubItemDto().withTaxCode("C3S1").withTotalPrice(thirdItemAmount).withUnitPrice(thirdItemAmount) // TAXABLE & TANGIBLE
        );
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withItems(items)
                .withTaxInclusive(true);
        ShippingAddressDto partialShippingAddress = new ShippingAddressDto(null, "CA", null, null, "", "", "12345", false, null); // zip code belongs to New York
        givenTransaction = givenTransaction.withShippingAddress(partialShippingAddress);

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
                    BigDecimal amountWithOutSalesTax = BigDecimalProcessor.removeTrailingZeros(expectedTotalAmount.add(transactionDto.salesTax().amount()));

                    assertEquals(expectedTaxableAmount, transactionDto.taxableItemsAmount());
                    assertEquals(expectedTangibleAmount, transactionDto.tangibleItemsAmount());
                    assertEquals(expectedTotalAmount, transactionDto.totalItemsAmount());
                    assertEquals(amountWithOutSalesTax, transactionDto.finalTransactionAmount());
                });
    }


    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_NonUsaCountryTransactionWithNullZipAndTaxInclusiveAndNewItems_Returns201() {
        String externalId = "newNonExistingNonUsaCountryTransactionWithNullZipAndTaxInclusiveAndNewItems";
        BigDecimal firstItemAmount = BigDecimal.valueOf(5000);
        BigDecimal firstItemWithoutTax = BigDecimalProcessor.removeTrailingZeros(firstItemAmount.divide(BigDecimal.ONE.add(BigDecimal.valueOf(0.14975)), 6, RoundingMode.HALF_UP));
        BigDecimal secondItemAmount = BigDecimal.valueOf(10000);
        BigDecimal thirdItemAmount = BigDecimal.valueOf(1000);
        BigDecimal thirdItemWithoutTax = BigDecimalProcessor.removeTrailingZeros(thirdItemAmount.divide(BigDecimal.ONE.add(BigDecimal.valueOf(0.14975)), 6, RoundingMode.HALF_UP));
        BigDecimal expectedTaxableAmount = firstItemWithoutTax.add(thirdItemWithoutTax);
        BigDecimal expectedTangibleAmount = secondItemAmount.add(thirdItemWithoutTax);
        BigDecimal expectedTotalAmount = firstItemWithoutTax.add(secondItemAmount).add(thirdItemWithoutTax);
        List<ItemDto> items = Arrays.asList(
                ITUtilities.stubItemDto().withTaxCode("C6S1").withTotalPrice(firstItemAmount).withUnitPrice(firstItemAmount), // TAXABLE & INTANGIBLE
                ITUtilities.stubItemDto().withTaxCode("C4S1").withTotalPrice(secondItemAmount).withUnitPrice(secondItemAmount), // NOT_TAXABLE & TANGIBLE
                ITUtilities.stubItemDto().withTaxCode("C3S1").withTotalPrice(thirdItemAmount).withUnitPrice(thirdItemAmount) // TAXABLE & TANGIBLE
        );
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withItems(items)
                .withTaxInclusive(true);
        ShippingAddressDto partialShippingAddress = new ShippingAddressDto(null, "CA", null, null, "", "", null, false, null); // zip code belongs to New York
        givenTransaction = givenTransaction.withShippingAddress(partialShippingAddress);

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
                    BigDecimal amountWithOutSalesTax = BigDecimalProcessor.removeTrailingZeros(expectedTotalAmount.add(transactionDto.salesTax().amount()));

                    assertEquals(expectedTaxableAmount, transactionDto.taxableItemsAmount());
                    assertEquals(expectedTangibleAmount, transactionDto.tangibleItemsAmount());
                    assertEquals(expectedTotalAmount, transactionDto.totalItemsAmount());
                    assertEquals(amountWithOutSalesTax, transactionDto.finalTransactionAmount());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_NonUsaCountryButSentAsAbbreviationReturnUpperCase_Returns201() {
        String externalId = "newNonExistingTransactionIDNonUsaAbbreviation";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto(null, "CA", null, null, "", "", "12345", false, null));

        SalesTaxDto expectedSalesTax = new SalesTaxDto(null, new BigDecimal("1497.5"), BigDecimal.valueOf(0.14975), null, new GtRatesDto(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.0975), BigDecimal.valueOf(0.14975)));

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
                    assertEquals(expectedSalesTax, transactionDto.salesTax());
                    assertNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().regions());
                    assertNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().cities());
                    assertEquals(0, transactionDto.finalTransactionAmount().compareTo(new BigDecimal("11497.5")));
                    assertEquals("Canada", transactionDto.shippingAddress().country());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_NonUsaCountryButSentLowerCaseReturnsUpperCase_Returns201() {
        String externalId = "newNonExistingTransactionIDNonUsaLowercase";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto(null, "canada", null, null, "", "", "12345", false, null));

        SalesTaxDto expectedSalesTax = new SalesTaxDto(null, new BigDecimal("1497.5"), BigDecimal.valueOf(0.14975), null, new GtRatesDto(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.0975), BigDecimal.valueOf(0.14975)));

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
                    assertEquals(expectedSalesTax, transactionDto.salesTax());
                    assertNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().regions());
                    assertNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().cities());
                    assertEquals(0, transactionDto.finalTransactionAmount().compareTo(new BigDecimal("11497.5")));
                    assertEquals("Canada", transactionDto.shippingAddress().country());
                });
    }


    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_NonUsaCountryAndRegionTaxInclusive_ReturnsTaxableTransaction() {
        String externalId = "newNonExistingTransactionIDC";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDtoNonUsaCountry(externalId, customerId)
                .withTaxInclusive(true);

        SalesTaxDto expectedSalesTax = new SalesTaxDto(null, new BigDecimal("1285.40305"), new BigDecimal("0.14750"), null, new GtRatesDto(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.0975), BigDecimal.valueOf(0.14975)));

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
                    assertEquals(expectedSalesTax, transactionDto.salesTax());
                    assertNotNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().regions());
                    assertEquals(transactionDto.items().get(0).jurisdictionalSalesTaxRules().regions().size(), 1);
                    assertNotNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().regions().get("Quebec"));
                    assertNull(transactionDto.items().get(0).jurisdictionalSalesTaxRules().cities());
                    assertTrue(transactionDto.isTaxInclusive());
                    assertEquals(0, transactionDto.finalTransactionAmount().compareTo(new BigDecimal("10000")));
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_NonUsaCountryNotSupported_Returns400() {
        //Given
        String nullExternalId = "null";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(nullExternalId, customerId)
                .withShippingAddress(new ShippingAddressDto(null, "NotSupport", null, null, null, null, null, false, null));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + nullExternalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaCountryWithNoState_Returns400() {
        //Given
        String externalId = "errorTransaction";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId);
        givenTransaction = givenTransaction.withShippingAddress(givenTransaction.shippingAddress().withState(null));

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
                .expectStatus().isBadRequest();
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UsaCountryWithNoZip_Returns400() {
        //Given
        String externalId = "errorTransaction";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId);
        givenTransaction = givenTransaction.withShippingAddress(givenTransaction.shippingAddress().withZip(null));

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
                .expectStatus().isBadRequest();
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_NonUsaCountryNotSupportedCountry_Returns400() {
        //Given
        String externalId = "errorTransaction";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId);
        givenTransaction = givenTransaction.withShippingAddress(givenTransaction.shippingAddress().withZip(null));

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
                .expectStatus().isBadRequest();
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt

    /*
     This transaction's customer has an exemption in state PA with validation dates of:
     fromDate: 2025-01-01, toDate: 26-01-01, therefore transaction is NOT sales-tax exempt
    */
    public void upsertByExternalIdAndSource_CustomerIsNotExemptByStateAndDate_ReturnsTaxableTransaction() {
        String externalId = "anotherNonExistingTransactionID";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("fresno", "US", null, "PA", "st", "", "12345", false, null))
                .withExternalTimestamps(new TimestampsDto("2024-01-02", "2025-01-02"));

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
                .value(transactionDto -> assertNotNull(transactionDto.salesTax()));
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt

    /*
     This transaction's customer has an exemption in state FL with Exemption Status - CANCELLED,
     therefore transaction is NOT sales-tax exempt
    */
    public void upsertByExternalIdAndSource_CustomerIsNotExemptBecauseExemptionIsCancelled_ReturnsTaxableTransaction() {
        String externalId = "ThirdNonExistingIdForExemptionChecks";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "", "99801", false, null))
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

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
                .value(transactionDto -> assertNotNull(transactionDto.salesTax()));
    }

    //Exemptions Testing
    /*
    This transaction's customer has two exemptions in state PA by this schema in this timeframe
                 |-----Fully-Exemption-----------|
                                 |---------Partially-Exemption------------|
     time:  2025-01-01      2025-12-01        2026-01-01             2026-05-01
     transactionCreatedDate:               (T)
     */
    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_CustomerIsFullyExemptAndPartiallyExemption_Exempt() {
        String externalId = "nonExistingTransactionID_A";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("fresno", "US", null, "PA", "st", "", "12345", false, null))
                .withExternalTimestamps(new TimestampsDto("2025-12-02", "2025-12-02"));

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

    /*
    This transaction's customer has two exemptions in state PA by this schema in this timeframe
                 |-----Fully-Exemption-----------|
                                 |---------Partially-Exemption------------|
     time:  2025-01-01      2025-12-01        2026-01-01             2026-05-01
     transactionCreatedDate:                                     (T)
     */
    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_CustomerIsPartiallyExempt_NotExempted() {
        String externalId = "nonExistingTransactionID_B";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("fresno", "US", null, "PA", "st", "", "12345", false, null))
                .withExternalTimestamps(new TimestampsDto("2026-02-01", "2026-02-01"));

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
                .value(transactionDto -> assertNotNull(transactionDto.salesTax()));
    }

    /*
    This transaction's customer has two exemptions in state PA by this schema in this timeframe
                 |-----Fully-Exemption-----------|
                                 |---------Partially-Exemption------------|
     time:  2025-01-01      2025-12-01        2026-01-01          2026-05-01
     transactionCreatedDate:                                                         (T)
     */
    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_CustomerIsNotNoExempt_NoExempted() {
        String externalId = "nonExistingTransactionID_C";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("fresno", "US", null, "PA", "st", "", "12345", false, null))
                .withExternalTimestamps(new TimestampsDto("2026-05-02", "2026-05-02"));

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
                .value(transactionDto -> assertNotNull(transactionDto.salesTax()));
    }

    /*
    This transaction's customer has two exemptions in state PA by this schema in this timeframe
              |---------Not-Active-Exemption------------|
                                |----------Fully-Exemption-----------|
     time:  2027-01-01      2027-12-01             2028-01-01          2028-05-01
     transactionCreatedDate:               (T)
     */
    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_NotActiveExemptionAndFullyExempt_Exempted() {
        String externalId = "nonExistingTransactionID_D";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("fresno", "US", null, "PA", "st", "", "12345", false, null))
                .withExternalTimestamps(new TimestampsDto("2027-12-02", "2027-12-02"));

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

    /*
    This transaction's customer has two exemptions in state PA by this schema in this timeframe
              |---------Not-Active-Exemption------------|
                                        |----------Fully-Exemption-----------|
     time:  2027-01-01              2027-12-01             2028-01-01          2028-05-01
     transactionCreatedDate:  (T)
     */
    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_NotActiveExemption_NoExempted() {
        String externalId = "nonExistingTransactionID_E";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("fresno", "US", null, "PA", "st", "", "12345", false, null))
                .withExternalTimestamps(new TimestampsDto("2027-01-02", "2027-01-02"));

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
                .value(transactionDto -> assertNotNull(transactionDto.salesTax()));
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_DoesntExistsAndCustomerDoesntExists_Returns404() {
        String externalId = "10001";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, UUID.fromString(ITUtilities.NON_EXISTING_COMPLYT_ID))
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
                .expectStatus().isNotFound();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_ExistsAndCustomerDoesntExists_Returns404() {
        // Given
        String externalId = "10002";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, UUID.fromString(ITUtilities.NON_EXISTING_COMPLYT_ID))
                .withShippingAddress(referenceAddress);
        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/10002")
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_DoesntExistsAndSaleTaxTrackingDoesntExists_Returns400() {
        // Given
        String externalId = "10003";
        String nonExistingState = "Nilfgaard";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(referenceAddress.withState(nonExistingState));

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
                .expectStatus().is4xxClientError();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_ExistsAndSaleTaxTrackingDoesntExists_Returns400() {
        // Given
        String externalId = "10002";
        String nonExistingState = "Nilfgaard";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(referenceAddress.withState(nonExistingState));

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
                .expectStatus().is4xxClientError();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> assertEquals(transactionDto.shippingAddress().state(), "AZ"));
    }


    @Order(3)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_TransactionWithStatusCancelled_Returns204() {
        // Given
        String externalId = "10005"; // new externalID that does not exist
        TransactionDto givenCancelledTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTransactionStatus(TransactionStatusDto.CANCELLED);

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCancelledTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getAllBySource_Exists_Returns200() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/2")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> assertEquals(list.size(), 1));
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getAllBySource_QueryParamInvalid_Returns400() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getAllBySource_PathVariableInvalid_Returns400() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/1")
                        .queryParam("size", "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getAllBySource_DoesntExists_Returns200EmptyList() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/9")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> assertEquals(list.size(), 0));
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getAll_Exists_Returns200() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> assertEquals(PaginationConstants.DEFAULT_PAGE_SIZE, list.size()));
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getAll_QueryParamInvalid_Returns400() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .queryParam("page", "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt(tenantId = "different_tenant")
    public void getByAll_DoesntExists_Returns200EmptyList() {

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> assertEquals(0, list.size()));
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getByAll_QueryParamInvalid_Returns400() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .queryParam("page", "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }


    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getByExternalIdAndSource_Exists_Returns200() {
        //Given
        String externalId = "10002";
        UUID complytId = UUID.fromString("a6469aaf-e838-41df-8106-6a8927917985"); // complytId of existing transaction

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transaction -> {
                    assertEquals(transaction.complytId(), complytId);
                });
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getByExternalIdAndSource_Exists_Returns200CheckingDefaultNullFields() {
        //Given
        String externalId = "transactionToCheckProjectionWithSalesTax";
        UUID complytId = UUID.fromString("607f3926-61d3-40a4-9b3a-a6bf7c3a1d95"); // complytId of existing transaction

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transaction -> {
                    assertEquals(transaction.complytId(), complytId);
                    assertNotNull(transaction.items().get(0).salesTaxRates());
                    assertNull(transaction.items().get(0).salesTaxRates().cityRate());
                    assertNull(transaction.items().get(0).salesTaxRates().countyRate());
                    assertNull(transaction.items().get(0).salesTaxRates().stateRate());
                    assertNotNull(transaction.items().get(0).salesTaxRates().taxRate());

                    assertNull(transaction.items().get(0).jurisdictionalSalesTaxRules());

                    assertNull(transaction.salesTax().salesTaxRates().cityRate());
                    assertNull(transaction.salesTax().salesTaxRates().countyRate());
                    assertNull(transaction.salesTax().salesTaxRates().stateRate());
                    assertNotNull(transaction.salesTax().salesTaxRates().taxRate());
                    assertNotNull(transaction.salesTax().rate());

                    assertNotNull(transaction.complytId());
                    assertNotNull(transaction.externalId());
                    assertNotNull(transaction.source());
                    assertNotNull(transaction.customerId());
                    assertNotNull(transaction.documentName());
                    assertNotNull(transaction.subsidiary());
                    assertNotNull(transaction.currency());
                    assertNotNull(transaction.tangibleItemsAmount());
                    assertNotNull(transaction.taxableItemsAmount());
                    assertNotNull(transaction.totalDiscount());
                    assertNotNull(transaction.totalItemsAmount());
                    assertNotNull(transaction.finalTransactionAmount());
                    assertNotNull(transaction.transactionStatus());
                    assertNotNull(transaction.billingAddress());
                    assertNotNull(transaction.shippingAddress());
                    assertNotNull(transaction.internalTimestamps());
                    assertNotNull(transaction.externalTimestamps());
                    assertNotNull(transaction.transactionType());
                    assertNotNull(transaction.shippingFee().manualSalesTax());
                    assertNotNull(transaction.shippingFee().manualSalesTaxRate());
                    assertNotNull(transaction.shippingFee().totalPrice());

                    assertNotNull(transaction.shippingFee().salesTaxRates().taxRate());
                    assertNull(transaction.shippingFee().salesTaxRates().cityRate());
                    assertNull(transaction.shippingFee().salesTaxRates().countyRate());
                    assertNull(transaction.shippingFee().salesTaxRates().stateRate());
                    assertNull(transaction.shippingFee().salesTaxRates().ratesMetaData());
                    assertNull(transaction.shippingFee().salesTaxRates().combinedDistrictRate());

                    assertNotNull(transaction.shippingFee().gtRates().taxRate());
                    assertNull(transaction.shippingFee().gtRates().countryRate());
                    assertNull(transaction.shippingFee().gtRates().regionRate());

                    assertNotNull(transaction.shippingFee().taxCode());
                    assertNotNull(transaction.shippingFee().taxableCategory());
                    assertNotNull(transaction.shippingFee().tangibleCategory());
                    assertNotNull(transaction.shippingFee().calculatedTotal());
                    assertNotNull(transaction.salesTax().amount());
                    assertNotNull(transaction.salesTax().rate());
                    assertNotNull(transaction.salesTax().salesTaxRates().taxRate());
                    assertNotNull(transaction.salesTax().gtRates().taxRate());
                    assertNotNull(transaction.customer().complytId());
                    assertNotNull(transaction.customer().externalId());
                    assertNotNull(transaction.customer().name());
                    assertNotNull(transaction.customer().customerType());
                    assertNotNull(transaction.exchangeRateInfo());
                    assertNotNull(transaction.exchangeRateInfo().finalTransactionAmountInUsd());
                    assertNotNull(transaction.exchangeRateInfo().fxRate());
                    assertNotNull(transaction.exchangeRateInfo().fromCurrency());
                    assertNotNull(transaction.exchangeRateInfo().toCurrency());
                });
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getByExternalIdAndSource_ExistsDetailedTrue_Returns200CheckingProjectedFields() {
        //Given
        String externalId = "transactionToCheckProjectionWithSalesTax";
        UUID complytId = UUID.fromString("607f3926-61d3-40a4-9b3a-a6bf7c3a1d95"); // complytId of existing transaction


        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .queryParam("detailed", true)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transaction -> {
                    assertEquals(transaction.complytId(), complytId);
                    assertNotNull(transaction.items().get(0).salesTaxRates());
                    assertNotNull(transaction.items().get(0).salesTaxRates().cityRate());
                    assertNotNull(transaction.items().get(0).salesTaxRates().countyRate());
                    assertNotNull(transaction.items().get(0).salesTaxRates().stateRate());
                    assertNotNull(transaction.items().get(0).salesTaxRates().taxRate());

                    assertNotNull(transaction.items().get(0).jurisdictionalSalesTaxRules());

                    assertNotNull(transaction.salesTax().salesTaxRates().cityRate());
                    assertNotNull(transaction.salesTax().salesTaxRates().countyRate());
                    assertNotNull(transaction.salesTax().salesTaxRates().stateRate());
                    assertNotNull(transaction.salesTax().salesTaxRates().taxRate());
                    assertNotNull(transaction.salesTax().rate());

                    assertNotNull(transaction.complytId());
                    assertNotNull(transaction.externalId());
                    assertNotNull(transaction.source());
                    assertNotNull(transaction.customerId());
                    assertNotNull(transaction.documentName());
                    assertNotNull(transaction.subsidiary());
                    assertNotNull(transaction.currency());
                    assertNotNull(transaction.tangibleItemsAmount());
                    assertNotNull(transaction.taxableItemsAmount());
                    assertNotNull(transaction.totalDiscount());
                    assertNotNull(transaction.totalItemsAmount());
                    assertNotNull(transaction.finalTransactionAmount());
                    assertNotNull(transaction.transactionStatus());
                    assertNotNull(transaction.billingAddress());
                    assertNotNull(transaction.shippingAddress());
                    assertNotNull(transaction.internalTimestamps());
                    assertNotNull(transaction.externalTimestamps());
                    assertNotNull(transaction.transactionType());
                    assertNotNull(transaction.shippingFee().manualSalesTax());
                    assertNotNull(transaction.shippingFee().manualSalesTaxRate());
                    assertNotNull(transaction.shippingFee().totalPrice());

                    assertNotNull(transaction.shippingFee().salesTaxRates().taxRate());
                    assertNotNull(transaction.shippingFee().salesTaxRates().cityRate());
                    assertNotNull(transaction.shippingFee().salesTaxRates().countyRate());
                    assertNotNull(transaction.shippingFee().salesTaxRates().stateRate());
                    assertNotNull(transaction.shippingFee().salesTaxRates().ratesMetaData());
                    assertNotNull(transaction.shippingFee().salesTaxRates().combinedDistrictRate());

                    assertNotNull(transaction.shippingFee().gtRates().taxRate());
                    assertNotNull(transaction.shippingFee().gtRates().countryRate());
                    assertNotNull(transaction.shippingFee().gtRates().regionRate());

                    assertNotNull(transaction.shippingFee().taxCode());
                    assertNotNull(transaction.shippingFee().taxableCategory());
                    assertNotNull(transaction.shippingFee().tangibleCategory());
                    assertNotNull(transaction.shippingFee().calculatedTotal());
                    assertNotNull(transaction.salesTax().amount());
                    assertNotNull(transaction.salesTax().rate());
                    assertNotNull(transaction.salesTax().salesTaxRates().taxRate());
                    assertNotNull(transaction.salesTax().gtRates().taxRate());
                    assertNotNull(transaction.customer().complytId());
                    assertNotNull(transaction.customer().externalId());
                    assertNotNull(transaction.customer().name());
                    assertNotNull(transaction.customer().customerType());
                    assertNotNull(transaction.exchangeRateInfo());
                    assertNotNull(transaction.exchangeRateInfo().finalTransactionAmountInUsd());
                    assertNotNull(transaction.exchangeRateInfo().fxRate());
                    assertNotNull(transaction.exchangeRateInfo().fromCurrency());
                    assertNotNull(transaction.exchangeRateInfo().toCurrency());
                });
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getByExternalIdAndSource_PathVariableInvalid_Returns400() {
        String externalId = "null";

        // Then
        webTestClient

                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getByExternalIdAndSource_DoesntExists_Returns404() {
        webTestClient

                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/notExisting")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(3)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_Exists_Returns200() {
        //Given
        String externalId = "10004";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
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
                .expectStatus().isOk();
    }


    @Order(2)
    @Override
    @Test
    @WithMockJwt

    public void upsertByExternalIdAndSource_PathVariableError_Returns400() {
        //Given
        String nullExternalId = "null";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(nullExternalId, customerId)
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + nullExternalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_DoesntExists_Returns201() {
        //Given
        String externalId = "10004";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
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
                .expectStatus().isCreated();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_DoesntExistsWithComplytId_Returns400ConflictedData() {
        // Given
        String externalId = "10005";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withComplytId(UUID.randomUUID())
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
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_ConflictingSource_Returns400ConflictedData() {
        // Given
        String externalId = "10005";
        String differentSource = "2";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto("10005", customerId)
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + differentSource + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_ConflictingExternalId_Returns400ConflictedData() {
        // Given
        String externalId = "someId";
        String differentExternalId = "differentId";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withComplytId(UUID.randomUUID())
                .withShippingAddress(referenceAddress);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + differentExternalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_DoesntPassValidation_Returns400CValidationError() {
        // Given
        String externalId = "someId";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withInternalTimestamps(new TimestampsDto("", "2021-10-10T07:00:00"))
                .withSource("");
        Set expectedErrors = new HashSet<>(List.of(
                "Timestamps.createdDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR,
                DtoErrorMessages.SOURCE_FORMAT_ERROR));

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
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    String[] errors = message.substring(1, message.length() - 1).split(", ");
                    assertEquals(expectedErrors.size(), errors.length);
                    for (String err : errors) {
                        assertTrue(expectedErrors.contains(err));
                    }
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_NoBody_Returns400() {
        // Given
        String externalId = "0";

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.MISSING_BODY_ERROR, map.get("message")));
    }

    @Order(3)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UnsupportedMediaType_Returns415() {
        // Given
        String externalId = "0";

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("Unsupported data")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.UNSUPPORTED_MEDIA_TYPE, map.get("message")));
    }

    @Order(4)
    @Test
    @Override
    @WithMockJwt
    public void deleteByExternalIdAndSource_Exists_Returns204() {
        // Given
        String externalId = "10004";

        // Then
        webTestClient
                .mutateWith(csrf())


                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Order(5)
    @Test
    @Override
    @WithMockJwt
    public void get_checkDeletion_Returns200() {
        // Given
        String externalId = "10004";

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class).value(transactionDto ->
                        assertEquals(transactionDto.transactionStatus(), TransactionStatusDto.CANCELLED));
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void deleteByExternalIdAndSource_DoesntExists_Returns404() {
        webTestClient
                .mutateWith(csrf())


                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/notExisting")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getByComplytId_Exists_Returns200() {
        // Given
        String complytId = "88d951b8-4804-4bef-929a-cfd3670a82fa"; // complytId of existing transaction

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getByComplytId_PathVariableInvalid_Returns400() {
        // Given
        String complytId = "null";

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(3)
    @Test
    @Override
    @WithMockJwt
    public void getByComplytId_DoesntExists_Returns404() {
        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/complytId/" + ITUtilities.NON_EXISTING_COMPLYT_ID)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void getAll_GetByParamSize_ReturnsExpectedSize() {
        int size = 1;
        String expectedComplyId = "6ee574bb-0300-4c74-9e4f-1852f234a028";
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL) // Set your API endpoint
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Transaction.class)
                .value(transactions -> Assertions.assertEquals(transactions.get(0).getComplytId().toString(), expectedComplyId))
                .hasSize(size);
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void getAll_GetByParamPage_ReturnsExpectedPage() {
        int page = 2;
        int size = 1;
        String expectedComplyId = "4cfbbf0b-d3e5-4954-8a90-c9c2ec32e5f5";

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL) // Set your API endpoint
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Transaction.class)
                .value(transactions -> Assertions.assertEquals(transactions.get(0).getComplytId().toString(), expectedComplyId));
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void getAll_GetByDefaultsSizeAndPage_ReturnsExpectedEntries() {
        String expectedComplyId = "6ee574bb-0300-4c74-9e4f-1852f234a028";

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL) // Set your API endpoint
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Transaction.class)
                .value(transactions -> Assertions.assertEquals(transactions.get(0).getComplytId().toString(), expectedComplyId))
                .value(transactions -> Assertions.assertTrue(transactions.size() <= PaginationConstants.DEFAULT_PAGE_SIZE));
    }

    @Order(0)
    @Test
    @WithMockJwt
    public void getAll_SortedByExternalTimestampsCreatedDate_ReturnsSortedEntries() {
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Transaction.class)
                .value(transactions -> {
                    LocalDateTime lastDate = null;
                    for (Transaction transaction : transactions) {
                        LocalDateTime currentDate = transaction.getExternalTimestamps().getCreatedDate();
                        if (lastDate != null) {
                            Assertions.assertTrue(currentDate.isBefore(lastDate) || currentDate.isEqual(lastDate),
                                    "Transactions should be sorted by creation date in descending order");
                        }
                        lastDate = currentDate;
                    }
                });
    }


    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_OneItemIsNegativeAmount_ReturnsTaxableTransaction() {
        // Given
        String externalId = "NonExistingIdNegativeAmount";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto(),
                        ITUtilities.stubItemDto().withUnitPrice(BigDecimal.valueOf(-100)).withTotalPrice(BigDecimal.valueOf(-100)))
                .withShippingAddress(new ShippingAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "", "99801", false, null))
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

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
                .value(transactionDto -> assertNotNull(transactionDto.salesTax()));
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_TransactionWithShippingFee_ReturnsTaxableTransactionWithShippingFeeAndItemsCalculatedTotal() {
        //Given
        String externalId = "NonExistingIdTransactionWithShipping";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto())
                .withShippingAddress(new ShippingAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "", "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

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
                    assertEquals(ITUtilities.stubItemDto().totalPrice(), transactionDto.items().get(0).calculatedTotal());
                    assertEquals(ITUtilities.stubShippingFeeDto().totalPrice(), transactionDto.shippingFee().calculatedTotal());
                    assertNotNull(transactionDto.salesTax());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_ItemWithManualSalesTax_ReturnsTaxableTransactionWithCalculatedTotal() {
        //Given
        String externalId = "NonExistingIdTransactionWithManualSalesTax";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withManualSalesTax(true).withManualSalesTaxRate(new BigDecimal("0.2")))
                .withShippingAddress(new ShippingAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "", "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

        BigDecimal itemSalesTaxAmount = new BigDecimal(ITUtilities.stubItemDto().totalPrice().multiply(new BigDecimal("0.2")).stripTrailingZeros().toPlainString());

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
                    assertEquals(ITUtilities.stubItemDto().totalPrice(), transactionDto.items().get(0).calculatedTotal());
                    assertEquals(itemSalesTaxAmount, transactionDto.salesTax().amount());
                    assertNotNull(transactionDto.salesTax());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_NoItemHasDiscount_ReturnsTaxableTransactionWithDiscountTotal0() {
        //Given
        String externalId = "NonExistingIdTransactionWithNoDiscount";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto())
                .withShippingAddress(new ShippingAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "", "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

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
                    assertEquals(BigDecimal.ZERO, transactionDto.totalDiscount());
                    assertNotNull(transactionDto.salesTax());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_OneItemHasDiscount_ReturnsTaxableTransactionWithDiscountTotal() {
        //Given
        String externalId = "NonExistingIdTransactionOneItemHaveDiscount";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withDiscount(BigDecimal.valueOf(500)))
                .withShippingAddress(new ShippingAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "", "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

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
                    assertEquals(BigDecimal.valueOf(500), transactionDto.totalDiscount());
                    assertNotNull(transactionDto.salesTax());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_TwoItemHaveDiscount_ReturnsTaxableTransactionWithDiscountTotal() {
        //Given
        String externalId = "NonExistingIdTransactionTwoItemsHaveDiscount";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withDiscount(BigDecimal.valueOf(500)),
                        ITUtilities.stubItemDto().withDiscount(BigDecimal.valueOf(700)))
                .withShippingAddress(new ShippingAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "", "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

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
                    assertEquals(BigDecimal.valueOf(1200), transactionDto.totalDiscount());
                    assertNotNull(transactionDto.salesTax());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_OneItemHasDiscountOneItemIsNegative_ReturnsTaxableTransactionWithDiscount() {
        //Given
        String externalId = "NonExistingIdTransactionOneItemHaveDiscountOneNegative";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withDiscount(BigDecimal.valueOf(500)),
                        ITUtilities.stubItemDto().withUnitPrice(BigDecimal.valueOf(-800)).withTotalPrice(BigDecimal.valueOf(-800)))
                .withShippingAddress(new ShippingAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "", "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

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
                    assertEquals(BigDecimal.valueOf(500), transactionDto.totalDiscount());
                    assertNotNull(transactionDto.salesTax());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_ItemUnitPriceAndQuantityNullAndTotalNotNull_ReturnsTaxableTransaction() {
        //Given
        String externalId = "NonExistingIdTransactionUnitPriceQuantityNull";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withQuantity(null).withUnitPrice(null))
                .withShippingAddress(new ShippingAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "", "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

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
                    assertEquals(ITUtilities.stubItemDto().totalPrice(), transactionDto.totalItemsAmount());
                    assertNotNull(transactionDto.salesTax());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_ItemUnitPriceAndQuantityNotNullAndTotalNull_ReturnsTaxableTransaction() {
        //Given
        String externalId = "NonExistingIdTransactionItemUnitPriceAndQuantityNotNullAndTotalNull";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withTotalPrice(null))
                .withShippingAddress(new ShippingAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "", "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

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
                    assertNotNull(transactionDto.salesTax());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_ItemDiscountIsEqualsToTotal_ReturnsTaxableTransactionWithItemAmount0() {
        //Given
        String externalId = "NonExistingIdTransactionItemDiscountIsEqualsToTotal";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withTotalPrice(BigDecimal.valueOf(500))
                                .withDiscount(BigDecimal.valueOf(500)))
                .withShippingAddress(new ShippingAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "", "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

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
                    assertNotNull(transactionDto.salesTax());
                    assertEquals(BigDecimal.ZERO, transactionDto.salesTax().amount());
                    assertEquals(BigDecimal.ZERO, transactionDto.totalItemsAmount());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_ItemDiscountIsEqualsToUnitPriceMultiplyByQuantity_ReturnsTaxableTransactionWithItemAmount0() {
        //Given
        String externalId = "NonExistingIdTransactionItemDiscountIsEqualsToUnitPriceMultiplyByQuantity";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withTotalPrice(null)
                                .withQuantity(BigDecimal.ONE)
                                .withUnitPrice(BigDecimal.valueOf(500))
                                .withDiscount(BigDecimal.valueOf(500)))
                .withShippingAddress(new ShippingAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "", "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

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
                    assertNotNull(transactionDto.salesTax());
                    assertEquals(BigDecimal.ZERO, transactionDto.salesTax().amount());
                    assertEquals(BigDecimal.ZERO, transactionDto.totalItemsAmount());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_ConflictingItemHasNoUnitPriceAndQuantityAndTotal_Returns400ConflictedData() {
        //Given
        String externalId = "NonExistingIdTransactionItemHasNoUnitPriceAndQuantityAndTotal";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withTotalPrice(null).withQuantity(null).withUnitPrice(null))
                .withShippingAddress(new ShippingAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "", "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

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
                .expectStatus().isBadRequest();
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_ConflictingItemHasNegativeTotalAndDiscount_Returns400ConflictedData() {
        //Given
        String externalId = "NonExistingIdTransactionItemHasNegativeTotalAndDiscount";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withTotalPrice(BigDecimal.valueOf(-500))
                                .withQuantity(null).withUnitPrice(null).withDiscount(BigDecimal.valueOf(10)))
                .withShippingAddress(new ShippingAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "", "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

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
                .expectStatus().isBadRequest();
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_ConflictingItemHasNegativeUnitPriceAndQuantityAndDiscount_Returns400ConflictedData() {
        //Given
        String externalId = "NonExistingIdTransactionItemHasNegativeUnitPriceAndQuantityAndDiscount";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withTotalPrice(null)
                                .withQuantity(BigDecimal.valueOf(1)).withUnitPrice(BigDecimal.valueOf(-500))
                                .withDiscount(BigDecimal.valueOf(10)))
                .withShippingAddress(new ShippingAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "", "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

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
                .expectStatus().isBadRequest();
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_ConflictingItemHasNegativeDiscount_Returns400ConflictedData() {
        //Given
        String externalId = "NonExistingIdTransactionItemHasNegativeDiscount";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withTotalPrice(null)
                                .withQuantity(BigDecimal.valueOf(1)).withUnitPrice(BigDecimal.valueOf(-500))
                                .withDiscount(BigDecimal.valueOf(-10)))
                .withShippingAddress(new ShippingAddressDto("Juneau", "US", null, "AK", "2285 Trout St", "", "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

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
                .expectStatus().isBadRequest();
    }

    @Order(3)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_TransactionWithTransactionLevelDiscount_Returns201() {
        // Given
        String externalId = "10006"; // new externalID that does not exist
        BigDecimal givenDiscount = BigDecimal.valueOf(100);
        TransactionDto givenTransaction = ITUtilities.stubTransactionDtoWithThreeItems(externalId, customerId)
                .withTransactionLevelDiscount(givenDiscount);

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
                    BigDecimal totalActualGivenTransactionDiscount = BigDecimalProcessor.removeTrailingZeros(transactionDto.items().stream().map(ItemDto::relativeTransactionDiscount).reduce(BigDecimal.ZERO, BigDecimal::add));
                    BigDecimal totalTransactionAmountAfterDiscount = BigDecimalProcessor.removeTrailingZeros(transactionDto.items().stream().map(ItemDto::calculatedTotal).reduce(BigDecimal.ZERO, BigDecimal::add));

                    assertEquals(givenDiscount, transactionDto.transactionLevelDiscount());
                    assertEquals(givenDiscount, totalActualGivenTransactionDiscount);
                    assertEquals(totalTransactionAmountAfterDiscount, transactionDto.totalItemsAmount());
                });
    }

    @Order(3)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_TransactionWithBothItemAndTransactionDiscount_Returns201() {
        // Given
        String externalId = "10007"; // new externalID that does not exist
        BigDecimal givenDiscount = BigDecimal.valueOf(100);
        TransactionDto givenTransaction = ITUtilities.stubTransactionDtoWithThreeItems(externalId, customerId);
        TransactionDto transactionWithBothItemAndTransactionDiscount = givenTransaction
                .withItems(givenTransaction.items().stream().map(itemDto -> itemDto.withDiscount(BigDecimal.valueOf(50))).collect(Collectors.toList()))
                .withTransactionLevelDiscount(givenDiscount);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(transactionWithBothItemAndTransactionDiscount)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> {
                    BigDecimal totalActualGivenTransactionDiscount = BigDecimalProcessor.removeTrailingZeros(transactionDto.items().stream().map(ItemDto::relativeTransactionDiscount).reduce(BigDecimal.ZERO, BigDecimal::add));
                    BigDecimal totalTransactionAmountAfterDiscount = BigDecimalProcessor.removeTrailingZeros(transactionDto.items().stream().map(ItemDto::calculatedTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
                    BigDecimal totalGivenDiscount = BigDecimalProcessor.removeTrailingZeros(transactionDto.items().stream().map(itemDto -> itemDto.totalPrice().subtract(itemDto.calculatedTotal())).reduce(BigDecimal.ZERO, BigDecimal::add));

                    assertEquals(givenDiscount, transactionDto.transactionLevelDiscount());
                    assertEquals(givenDiscount, totalActualGivenTransactionDiscount);
                    assertEquals(totalTransactionAmountAfterDiscount, transactionDto.totalItemsAmount());
                    assertEquals(BigDecimal.valueOf(250), totalGivenDiscount);
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "sorted_by_date_pagination_tenant")
    public void getAll_PaginationSortedByDateDesc_ReturnsSortedList() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> {
                    LocalDateTime firstDate = LocalDateTime.parse(list.get(0).externalTimestamps().createdDate());
                    LocalDateTime secondDate = LocalDateTime.parse(list.get(1).externalTimestamps().createdDate());
                    LocalDateTime thirdDate = LocalDateTime.parse(list.get(2).externalTimestamps().createdDate());
                    assertTrue(firstDate.isAfter(secondDate));
                    assertTrue(secondDate.isAfter(thirdDate));
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "sorted_by_date_pagination_tenant")
    public void getAll_PaginationSortedByDateAsc_ReturnsSortedList() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .queryParam("sortOrder", "asc")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> {
                    LocalDateTime firstDate = LocalDateTime.parse(list.get(0).externalTimestamps().createdDate());
                    LocalDateTime secondDate = LocalDateTime.parse(list.get(1).externalTimestamps().createdDate());
                    LocalDateTime thirdDate = LocalDateTime.parse(list.get(2).externalTimestamps().createdDate());
                    assertTrue(firstDate.isBefore(secondDate));
                    assertTrue(secondDate.isBefore(thirdDate));
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "pagination_filtered_by_transaction_type_tenant")
    public void getAll_PaginationFilteredByTransactionType_ReturnsRefunds() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .queryParam("transactionType", "REFUND")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> {
                    assertEquals(2, list.size());
                    for (TransactionDto t : list)
                        assertEquals(t.transactionType(), TransactionTypeDto.REFUND);
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "pagination_filtered_by_transaction_type_tenant")
    public void getAll_PaginationFilteredByTransactionType_ReturnsInvoices() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .queryParam("transactionType", "INVOICE")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> {
                    assertEquals(4, list.size());
                    for (TransactionDto t : list)
                        assertEquals(t.transactionType(), TransactionTypeDto.INVOICE);
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "pagination_sort_by_transaction_city_tenant")
    public void getAll_PaginationSortedByCityDesc_ReturnsSortedTransactions() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .queryParam("sortBy", "shippingAddress.city")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> {
                    assertEquals(3, list.size());
                    String firstCity = list.get(0).shippingAddress().city();
                    String secondCity = list.get(1).shippingAddress().city();
                    String thirdCity = list.get(2).shippingAddress().city();
                    assertTrue(firstCity.compareTo(secondCity) > 0);
                    assertTrue(secondCity.compareTo(thirdCity) > 0);
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "pagination_sort_by_transaction_city_tenant")
    public void getAll_PaginationSortedByCityAsc_ReturnsSortedTransactions() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .queryParam("sortBy", "shippingAddress.city")
                        .queryParam("sortOrder", "asc")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> {
                    assertEquals(3, list.size());
                    String firstCity = list.get(0).shippingAddress().city();
                    String secondCity = list.get(1).shippingAddress().city();
                    String thirdCity = list.get(2).shippingAddress().city();
                    assertTrue(firstCity.compareTo(secondCity) < 0);
                    assertTrue(secondCity.compareTo(thirdCity) < 0);
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "dump_tenant")
    public void getAll_PaginationFilteredByExternalId_PartialIdSent_ReturnsEmptyList() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .queryParam("externalId", "412365812122")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> assertEquals(0, list.size()));
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "dump_tenant")
    public void getAll_PaginationFilteredByExternalId_fullIdSent_ReturnsTransaction() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .queryParam("externalId", "4123658121222")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> assertEquals(1, list.size()));
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "pagination_filter_by_transaction_city_and_type_tenant")
    public void getAll_PaginationFilteredByCityAndTransactionType_ReturnsTransactions() {
        String city = "A-city";

        // Making sure that there are 7 transactions when querying without filter
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> assertEquals(7, list.size()));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .queryParam("shippingAddress.city", city)
                        .queryParam("transactionType", "SALES_ORDER")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> {
                    assertEquals(2, list.size());
                    for (TransactionDto t : list) {
                        assertEquals(t.transactionType(), TransactionTypeDto.SALES_ORDER);
                        assertEquals(t.shippingAddress().city(), city);
                    }
                });
    }

    @Override
    @WithMockJwt
    public void getAll_DetailedTrue_Returns200CheckingProjectedFields() {
        //Given
        String externalId = "transactionToCheckProjectionWithSalesTax";
        UUID complytId = UUID.fromString("607f3926-61d3-40a4-9b3a-a6bf7c3a1d95"); // complytId of existing transaction
        boolean detailed = true;

        // Then
        webTestClient

                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .queryParam("detailed", detailed)
                        .queryParam("externalId", externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(transactions -> {
                    TransactionDto transaction = transactions.get(0);
                    assertEquals(transaction.complytId(), complytId);
                    assertNotNull(transaction.items().get(0).salesTaxRates());
                    assertNotNull(transaction.items().get(0).salesTaxRates().cityRate());
                    assertNotNull(transaction.items().get(0).salesTaxRates().countyRate());
                    assertNotNull(transaction.items().get(0).salesTaxRates().stateRate());
                    assertNotNull(transaction.items().get(0).salesTaxRates().taxRate());

                    assertNotNull(transaction.items().get(0).jurisdictionalSalesTaxRules());

                    assertNotNull(transaction.salesTax().salesTaxRates().cityRate());
                    assertNotNull(transaction.salesTax().salesTaxRates().countyRate());
                    assertNotNull(transaction.salesTax().salesTaxRates().stateRate());
                    assertNotNull(transaction.salesTax().salesTaxRates().taxRate());
                    assertNotNull(transaction.salesTax().rate());

                    assertNotNull(transaction.complytId());
                    assertNotNull(transaction.externalId());
                    assertNotNull(transaction.source());
                    assertNotNull(transaction.customerId());
                    assertNotNull(transaction.documentName());
                    assertNotNull(transaction.subsidiary());
                    assertNotNull(transaction.currency());
                    assertNotNull(transaction.tangibleItemsAmount());
                    assertNotNull(transaction.taxableItemsAmount());
                    assertNotNull(transaction.totalDiscount());
                    assertNotNull(transaction.totalItemsAmount());
                    assertNotNull(transaction.finalTransactionAmount());
                    assertNotNull(transaction.transactionStatus());
                    assertNotNull(transaction.billingAddress());
                    assertNotNull(transaction.shippingAddress());
                    assertNotNull(transaction.internalTimestamps());
                    assertNotNull(transaction.externalTimestamps());
                    assertNotNull(transaction.transactionType());
                    assertNotNull(transaction.shippingFee().manualSalesTaxRate());
                    assertNotNull(transaction.shippingFee().totalPrice());

                    assertNotNull(transaction.shippingFee().salesTaxRates().taxRate());
                    assertNotNull(transaction.shippingFee().salesTaxRates().cityRate());
                    assertNotNull(transaction.shippingFee().salesTaxRates().countyRate());
                    assertNotNull(transaction.shippingFee().salesTaxRates().stateRate());
                    assertNotNull(transaction.shippingFee().salesTaxRates().ratesMetaData());
                    assertNotNull(transaction.shippingFee().salesTaxRates().combinedDistrictRate());

                    assertNotNull(transaction.shippingFee().gtRates().taxRate());
                    assertNotNull(transaction.shippingFee().gtRates().countryRate());
                    assertNotNull(transaction.shippingFee().gtRates().regionRate());

                    assertNotNull(transaction.shippingFee().taxCode());
                    assertNotNull(transaction.shippingFee().taxableCategory());
                    assertNotNull(transaction.shippingFee().tangibleCategory());
                    assertNotNull(transaction.shippingFee().calculatedTotal());
                    assertNotNull(transaction.salesTax().amount());
                    assertNotNull(transaction.salesTax().rate());
                    assertNotNull(transaction.salesTax().salesTaxRates().taxRate());
                    assertNotNull(transaction.salesTax().gtRates().taxRate());
                    assertNotNull(transaction.customer().complytId());
                    assertNotNull(transaction.customer().externalId());
                    assertNotNull(transaction.customer().name());
                    assertNotNull(transaction.customer().customerType());
                    assertNotNull(transaction.exchangeRateInfo());
                    assertNotNull(transaction.exchangeRateInfo().finalTransactionAmountInUsd());
                    assertNotNull(transaction.exchangeRateInfo().fxRate());
                    assertNotNull(transaction.exchangeRateInfo().fromCurrency());
                    assertNotNull(transaction.exchangeRateInfo().toCurrency());
                });
    }

    @Test
    @Override
    @WithMockJwt
    public void getAll_InvalidSortOrderSent_Throws400() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .queryParam("sortOrder", "ascc")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains(GenericErrorMessages.INVALID_SORT_ORDER_PARAMETER));
                });
    }

    @Test
    @Override
    @WithMockJwt
    public void getAll_InvalidPageValuePassed_Throws400() {
        webTestClient

                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .queryParam("page", "0")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains(DtoErrorMessages.PAGE_FORMAT_ERROR));
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "pagination_filtered_by_transaction_type_tenant")
    public void getAll_PartialFilterValuePassed_ReturnsList() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .queryParam("transactionType", "voIc")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> {
                    assertEquals(4, list.size());
                    for (TransactionDto t : list)
                        assertEquals(t.transactionType(), TransactionTypeDto.INVOICE);
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "pagination_filtered_by_transaction_type_tenant")
    public void getAll_BlankFilterValuePassed_ReturnsFullList() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .queryParam("transactionType", "")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(list -> {
                    assertEquals(6, list.size());
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "dump_tenant")
    public void upsert_TransactionIsLinkedRefund_Returns201WithFullSalesTaxOfInvoice() {
        // Given + When
        String invoiceExternalId = "4123658121";


        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + invoiceExternalId)
                        .queryParam("detailed", true)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(invoice -> {
                    String refundExternalId = UUID.randomUUID().toString();
                    SalesTaxDto invoicesSalesTax = invoice.salesTax()
                            .withRate(new BigDecimal("0.00000"));

                    TransactionDto refund = invoice
                            .withComplytId(null)
                            .withExternalId(refundExternalId)
                            .withCreatedFrom(invoiceExternalId)
                            .withTransactionType(TransactionTypeDto.REFUND)
                            .withIsRefundLinked(true)
                            .withSalesTax(null)
                            .withCustomer(null);


                    webTestClient
                            .mutateWith(csrf())
                            .put()
                            .uri(uriBuilder -> uriBuilder
                                    .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + refundExternalId)
                                    .build())
                            .bodyValue(refund)
                            .accept(MediaType.APPLICATION_JSON)
                            .exchange()
                            .expectStatus().isCreated()
                            .expectBody(TransactionDto.class)
                            .value(TransactionDto::salesTax, equalTo(invoicesSalesTax));
                });
    }

    @Test
    @Override
    @WithMockJwt
    public void upsert_TransactionIsLinkedRefundAndInvoiceNotFound_Returns201WithSameRefund() {
        // Given + When
        String notFoundInvoiceExternalId = "4123658121-not found";
        TransactionDto refund = ITUtilities.stubTransactionDto("externalIdOfRefund", customerId,
                        ITUtilities.stubItemDto().withQuantity(null).withUnitPrice(null))
                .withShippingAddress(ITUtilities.createAddressDtoInKensas())
                .withCreatedFrom(notFoundInvoiceExternalId)
                .withTransactionType(TransactionTypeDto.REFUND)
                .withIsRefundLinked(true);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + refund.externalId())
                        .build())
                .bodyValue(refund)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(TransactionDto::salesTax, equalTo(null));
    }

    @Test
    @Override
    @WithMockJwt
    public void upsert_TransactionIsLinkedRefundWithNullCreatedFrom_Returns201WithSameRefund() {
        // Given + When
        TransactionDto refund = ITUtilities.stubTransactionDto("externalIdOfRefund2", customerId,
                        ITUtilities.stubItemDto().withQuantity(null).withUnitPrice(null))
                .withShippingAddress(ITUtilities.createAddressDtoInKensas())
                .withCreatedFrom(null)
                .withTransactionType(TransactionTypeDto.REFUND)
                .withIsRefundLinked(true);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + refund.externalId())
                        .build())
                .bodyValue(refund)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(TransactionDto::salesTax, equalTo(null));
    }

    @Test
    @Override
    @WithMockJwt
    public void upsert_IsLinkedRefundFieldIsNull_Returns201WithSameRefund() {
        // Given + When
        TransactionDto refund = ITUtilities.stubTransactionDto("externalIdOfRefund3", customerId,
                        ITUtilities.stubItemDto().withQuantity(null).withUnitPrice(null))
                .withShippingAddress(ITUtilities.createAddressDtoInKensas())
                .withCreatedFrom(null)
                .withTransactionType(TransactionTypeDto.REFUND)
                .withIsRefundLinked(null);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + refund.externalId())
                        .build())
                .bodyValue(refund)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(TransactionDto::salesTax, equalTo(null));
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "dump_tenant")
    public void upsert_TransactionIsLinkedRefundWithPercentage_Returns201WithHalfSalesTaxOfInvoice() {
        // Given + When
        String invoiceExternalId = "4123658121";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + invoiceExternalId)
                        .queryParam("detailed", true)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(invoice -> {
                    String refundExternalId = UUID.randomUUID().toString();
                    BigDecimal refundLinkedPercentage = BigDecimal.valueOf(0.5);
                    SalesTaxDto refundExpectedSalesTax = invoice.salesTax()
                            .withAmount(invoice.salesTax().amount().multiply(refundLinkedPercentage))
                            .withRate(new BigDecimal("0.00000"));

                    TransactionDto refund = invoice
                            .withComplytId(null)
                            .withExternalId(refundExternalId)
                            .withCreatedFrom(invoiceExternalId)
                            .withTransactionType(TransactionTypeDto.REFUND)
                            .withIsRefundLinked(true)
                            .withRefundLinkedPercentage(refundLinkedPercentage)
                            .withSalesTax(null)
                            .withCustomer(null);


                    webTestClient
                            .mutateWith(csrf())
                            .put()
                            .uri(uriBuilder -> uriBuilder
                                    .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + refundExternalId)
                                    .build())
                            .bodyValue(refund)
                            .accept(MediaType.APPLICATION_JSON)
                            .exchange()
                            .expectStatus().isCreated()
                            .expectBody(TransactionDto.class)
                            .value(TransactionDto::salesTax, equalTo(refundExpectedSalesTax));
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "dump_tenant")
    public void upsert_TransactionIsLinkedButInvoiceHasSalesTaxNull_Returns201WithNullSalesTax() {
        // Given + When
        String invoiceExternalId = "4123658121222";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + invoiceExternalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(invoice -> {
                    String refundExternalId = UUID.randomUUID().toString();
                    BigDecimal refundLinkedPercentage = BigDecimal.valueOf(0.5);

                    TransactionDto refund = invoice
                            .withComplytId(null)
                            .withExternalId(refundExternalId)
                            .withCreatedFrom(invoiceExternalId)
                            .withTransactionType(TransactionTypeDto.REFUND)
                            .withIsRefundLinked(true)
                            .withRefundLinkedPercentage(refundLinkedPercentage)
                            .withSalesTax(null)
                            .withCustomer(null);


                    webTestClient
                            .mutateWith(csrf())
                            .put()
                            .uri(uriBuilder -> uriBuilder
                                    .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + refundExternalId)
                                    .build())
                            .bodyValue(refund)
                            .accept(MediaType.APPLICATION_JSON)
                            .exchange()
                            .expectStatus().isCreated()
                            .expectBody(TransactionDto.class)
                            .value(TransactionDto::salesTax, equalTo(null));
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "dump_tenant")
    public void upsert_TransactionIsLinkedRefundWithPercentage_Returns201WithQuarterSalesTaxOfInvoice() {
        // Given + When
        String invoiceExternalId = "4123658121";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + invoiceExternalId)
                        .queryParam("detailed", true)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(invoice -> {
                    String refundExternalId = UUID.randomUUID().toString();
                    BigDecimal refundLinkedPercentage = BigDecimal.valueOf(0.25);
                    SalesTaxDto refundExpectedSalesTax = invoice.salesTax().withAmount(invoice.salesTax().amount().multiply(refundLinkedPercentage))
                            .withRate(new BigDecimal("0.00000"));

                    TransactionDto refund = invoice
                            .withComplytId(null)
                            .withExternalId(refundExternalId)
                            .withCreatedFrom(invoiceExternalId)
                            .withTransactionType(TransactionTypeDto.REFUND)
                            .withIsRefundLinked(true)
                            .withRefundLinkedPercentage(refundLinkedPercentage)
                            .withSalesTax(null)
                            .withCustomer(null);

                    webTestClient
                            .mutateWith(csrf())
                            .put()
                            .uri(uriBuilder -> uriBuilder
                                    .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + refundExternalId)
                                    .build())
                            .bodyValue(refund)
                            .accept(MediaType.APPLICATION_JSON)
                            .exchange()
                            .expectStatus().isCreated()
                            .expectBody(TransactionDto.class)
                            .value(TransactionDto::salesTax, equalTo(refundExpectedSalesTax));
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "dump_tenant")
    public void upsert_Transaction_RefundLinkedPercentageHasNegativeValue_Returns400() {
        // Given + When
        String externalId = "externalIdOfRefund3";
        TransactionDto refund = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withQuantity(null).withUnitPrice(null))
                .withShippingAddress(ITUtilities.createAddressDtoInKensas())
                .withCreatedFrom(null)
                .withTransactionType(TransactionTypeDto.REFUND)
                .withIsRefundLinked(true)
                .withRefundLinkedPercentage(BigDecimal.valueOf(-1));

        Set<String> expectedErrors = new HashSet<>(List.of(
                "refundLinkedPercentage " + NumericErrorMessages.NOT_NEGATIVE_ERROR));

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(refund)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    String[] errors = message.substring(1, message.length() - 1).split(", ");
                    assertEquals(expectedErrors.size(), errors.length);
                    for (String err : errors) {
                        assertTrue(expectedErrors.contains(err));
                    }
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "dump_tenant")
    public void upsert_Transaction_RefundLinkedPercentageHasValueGreaterThan1_Returns400() {
        // Given + When
        String externalId = "externalIdOfRefund4";
        TransactionDto refund = ITUtilities.stubTransactionDto(externalId, customerId,
                        ITUtilities.stubItemDto().withQuantity(null).withUnitPrice(null))
                .withShippingAddress(ITUtilities.createAddressDtoInKensas())
                .withCreatedFrom(null)
                .withTransactionType(TransactionTypeDto.REFUND)
                .withIsRefundLinked(true)
                .withRefundLinkedPercentage(BigDecimal.valueOf(1.1));

        Set<String> expectedErrors = new HashSet<>(List.of(
                "refundLinkedPercentage" + NumericErrorMessages.DECIMAL_MAX_1_ERROR));

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(refund)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    String[] errors = message.substring(1, message.length() - 1).split(", ");
                    assertEquals(expectedErrors.size(), errors.length);
                    System.out.println("expectedErrors: " + expectedErrors);
                    for (String err : errors) {
                        assertTrue(expectedErrors.contains(err));
                    }
                });
    }

    // Testing taxableItemsAmount property
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_TransactionWithTaxableState_ReturnsTaxableItemsAmountOfItemsPrice() {
        //Given
        String externalId = "externalIdOfTaxabilityTestTransaction";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(
                        externalId, customerId,
                        ITUtilities.stubItemDto().withTaxCode("PCforTestingTaxability"))
                .withShippingAddress(new ShippingAddressDto("Juneau", "US", null, "AK", "2285 Trout St", null, "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

        // Then
        webTestClient.mutateWith(csrf())


                .put()
                .uri(uriBuilder -> uriBuilder.path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> {
                    assertEquals(ITUtilities.stubItemDto().totalPrice(), transactionDto.taxableItemsAmount());
                });
    }

    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_TransactionWithTaxableCityAndNotTaxableState_ReturnsTaxableItemsAmountOfItemsPrice() {
        //Given
        String externalId = "externalIdOfTaxabilityTestTransaction";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(
                        externalId, customerId,
                        ITUtilities.stubItemDto().withTaxCode("PCforTestingTaxability"))
                .withShippingAddress(new ShippingAddressDto("Acampo", "US", null, "Arkansas", "2285 Trout St", null, "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

        // Then
        webTestClient.mutateWith(csrf())


                .put()
                .uri(uriBuilder -> uriBuilder.path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> assertEquals(ITUtilities.stubItemDto().totalPrice().add(ITUtilities.stubShippingFeeDto().totalPrice()), transactionDto.taxableItemsAmount()));
    }

    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_TransactionWithOutTaxableCityAndState_ReturnsTaxableItemsAmountOfZero() {
        //Given
        String externalId = "externalIdOfTaxabilityTestTransaction1";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(
                        externalId, customerId,
                        ITUtilities.stubItemDto().withTaxCode("PCforTestingTaxability"))
                .withShippingAddress(new ShippingAddressDto("Acampo", "US", null, "California", "2285 Trout St", null, "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

        // Then
        webTestClient.mutateWith(csrf())


                .put()
                .uri(uriBuilder -> uriBuilder.path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> {
                    assertEquals(BigDecimal.ZERO, transactionDto.taxableItemsAmount());
                });
    }

    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_TransactionWithOutTaxableCityAndStateWithZeroThatDoesNotExist_ReturnsTaxableItemsAmountOfZero() {
        //Given
        String externalId = "externalIdOfTaxabilityTestTransaction2";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(
                        externalId, customerId,
                        ITUtilities.stubItemDto().withTaxCode("PCforTestingTaxability"))
                .withShippingAddress(new ShippingAddressDto("Acampo", "US", null, "Texas", "2285 Trout St", null, "99801", false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

        // Then
        webTestClient.mutateWith(csrf())


                .put()
                .uri(uriBuilder -> uriBuilder.path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> {
                    assertEquals(BigDecimal.ZERO, transactionDto.taxableItemsAmount());
                });

    }

    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_GTTransactionWithTaxableCountry_ReturnsTaxableItemsAmountOfItemsPrice() {
        //Given
        String externalId = "externalIdOfTaxabilityTestTransaction4";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(
                        externalId, customerId,
                        ITUtilities.stubItemDto().withTaxCode("PCforTestingTaxability"))
                .withShippingAddress(new ShippingAddressDto(null, "Sweden", null, null, null, null, null, false, null))
                .withShippingFee(ITUtilities.stubShippingFeeDto())
                .withExternalTimestamps(new TimestampsDto("2025-01-02", "2025-01-02"));

        // Then
        webTestClient.mutateWith(csrf())


                .put()
                .uri(uriBuilder -> uriBuilder.path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> {
                    assertEquals(BigDecimal.valueOf(10500), transactionDto.taxableItemsAmount());
                });
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_CustomerIsFullyExemptAndUsaTransactionWithoutManualSalesTax_ReturnsTransactionWithoutSalesTax() {
        String externalId = "CustomerIsFullyExemptAndUsaTransactionWithoutManualSalesTax_ID";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDtoWithThreeItems(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("city", "US", null, "PA", "st", "", "16028", false, null))
                .withExternalTimestamps(new TimestampsDto("2025-01-26", "2025-01-26"));

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
    @WithMockJwt
    public void upsertByExternalIdAndSource_CustomerIsFullyExemptAndUsaTransactionWithSomeItemsWithManualSalesTax_ReturnsTransactionWithSalesTax() {
        String externalId = "CustomerIsFullyExemptAndUsaTransactionWithSomeItemsWithManualSalesTax_ID";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDtoWithThreeItems(externalId, customerId);
        ItemDto firstItemWithManualRate = givenTransaction.items().get(0).withManualSalesTax(true).withManualSalesTaxRate(BigDecimal.valueOf(0.1));
        ItemDto secondItem = givenTransaction.items().get(1);
        ItemDto thirdItem = givenTransaction.items().get(2);
        List<ItemDto> itemsDto = new ArrayList<>() {{
            add(firstItemWithManualRate);
            add(secondItem);
            add(thirdItem);
        }};

        givenTransaction = givenTransaction.withItems(itemsDto)
                .withShippingAddress(new ShippingAddressDto("city", "US", null, "PA", "st", "", "16028", false, null))
                .withExternalTimestamps(new TimestampsDto("2025-01-26", "2025-01-26"));

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
                            assertNotNull(transactionDto.salesTax());
                            assertEquals(BigDecimal.valueOf(50), transactionDto.salesTax().amount()); // Manual tax rate is 10% for each item
                            assertNull(transactionDto.items().get(0).salesTaxRates());
                            assertNull(transactionDto.items().get(1).salesTaxRates());
                            assertNull(transactionDto.items().get(2).salesTaxRates());
                        }
                );
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_CustomerIsFullyExemptAndUsaTransactionWithAllItemsWithManualSalesTax_ReturnsTransactionWithSalesTax() {
        String externalId = "CustomerIsFullyExemptAndUsaTransactionWithAllItemsWithManualSalesTax_ID";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDtoWithThreeItems(externalId, customerId);
        List<ItemDto> itemsDto = givenTransaction.items().stream().map(itemDto -> itemDto.withManualSalesTax(true).withManualSalesTaxRate(BigDecimal.valueOf(0.1))).toList();

        givenTransaction = givenTransaction.withItems(itemsDto)
                .withShippingAddress(new ShippingAddressDto("city", "US", null, "PA", "st", "", "16028", false, null))
                .withExternalTimestamps(new TimestampsDto("2025-01-26", "2025-01-26"));

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
                            assertNotNull(transactionDto.salesTax());
                            assertEquals(BigDecimal.valueOf(1150), transactionDto.salesTax().amount()); // Manual tax rate is 10% for each item
                            assertNull(transactionDto.items().get(0).salesTaxRates());
                            assertNull(transactionDto.items().get(1).salesTaxRates());
                            assertNull(transactionDto.items().get(2).salesTaxRates());
                        }
                );
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_CustomerIsFullyExemptAndNonUsaTransactionWithoutManualSalesTax_ReturnsTransactionWithoutSalesTax() {
        String externalId = "CustomerIsFullyExemptAndNonUsaTransactionWithoutManualSalesTax_ID";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDtoWithThreeItems(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto("Ramat Gan", "Sweden", null, "Complyt", "Menachem Begin road 7", "", "5268102", false, null))
                .withExternalTimestamps(new TimestampsDto("2022-12-02", "2022-12-02"));

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
    @WithMockJwt
    public void upsertByExternalIdAndSource_CustomerIsFullyExemptAndNonUsaTransactionWithSomeItemsWithManualSalesTax_ReturnsTransactionWithSalesTax() {
        String externalId = "CustomerIsFullyExemptAndNonUsaTransactionWithSomeItemsWithManualSalesTax_ID";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDtoWithThreeItems(externalId, customerId);
        ItemDto firstItemWithManualRate = givenTransaction.items().get(0).withManualSalesTax(true).withManualSalesTaxRate(BigDecimal.valueOf(0.1));
        ItemDto secondItem = givenTransaction.items().get(1);
        ItemDto thirdItem = givenTransaction.items().get(2);
        List<ItemDto> itemsDto = new ArrayList<>() {{
            add(firstItemWithManualRate);
            add(secondItem);
            add(thirdItem);
        }};

        givenTransaction = givenTransaction.withItems(itemsDto)
                .withShippingAddress(new ShippingAddressDto("Ramat Gan", "Sweden", null, "Complyt", "Menachem Begin road 7", "", "5268102", false, null))
                .withExternalTimestamps(new TimestampsDto("2022-12-02", "2022-12-02"));

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
                            assertNotNull(transactionDto.salesTax());
                            assertEquals(BigDecimal.valueOf(50), transactionDto.salesTax().amount()); // Manual tax rate is 10% for each item
                            assertNull(transactionDto.items().get(0).salesTaxRates());
                            assertNull(transactionDto.items().get(1).salesTaxRates());
                            assertNull(transactionDto.items().get(2).salesTaxRates());
                        }
                );
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_CustomerIsFullyExemptAndNonUsaTransactionWithAllItemsWithManualSalesTax_ReturnsTransactionWithSalesTax() {
        String externalId = "CustomerIsFullyExemptAndNonUsaTransactionWithAllItemsWithManualSalesTax_ID";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDtoWithThreeItems(externalId, customerId);
        List<ItemDto> itemsDto = givenTransaction.items().stream().map(itemDto -> itemDto.withManualSalesTax(true).withManualSalesTaxRate(BigDecimal.valueOf(0.1))).toList();

        givenTransaction = givenTransaction.withItems(itemsDto)
                .withShippingAddress(new ShippingAddressDto("Ramat Gan", "Sweden", null, "Complyt", "Menachem Begin road 7", "", "5268102", false, null))
                .withExternalTimestamps(new TimestampsDto("2022-12-02", "2022-12-02"));

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
                            assertNotNull(transactionDto.salesTax());
                            assertEquals(BigDecimal.valueOf(1150), transactionDto.salesTax().amount()); // Manual tax rate is 10% for each item
                            assertNull(transactionDto.items().get(0).salesTaxRates());
                            assertNull(transactionDto.items().get(1).salesTaxRates());
                            assertNull(transactionDto.items().get(2).salesTaxRates());
                        }
                );
    }

    @Order(3)
    @Test
//    @Override
    @WithMockJwt
    public void upsert_TTestingSalesTaxRateField_Returns201With0Rate() {
        // Given
        String externalId = "10006898"; // new externalID that does not exist
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withShippingAddress(new ShippingAddressDto(null, "Canada", null, null, "", "", "12345", false, null));
        List<ItemDto> items = List.of(
                givenTransaction.items().get(0).withManualSalesTax(true),
                givenTransaction.items().get(0).withManualSalesTax(true)
        );
        SalesTaxDto expectedSalesTax = new SalesTaxDto(null, new BigDecimal("0"), new BigDecimal("0.00000"), null, new GtRatesDto(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.0975), BigDecimal.valueOf(0.14975)));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction.withItems(items))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> {
                    System.out.println("transactionDto: " + transactionDto);
                    assertEquals(expectedSalesTax, transactionDto.salesTax());
                });
    }
}