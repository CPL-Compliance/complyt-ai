package integration.scenarios;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.transaction.ItemDto;
import com.complyt.v1.models.transaction.MandatoryAddressDto;
import com.complyt.v1.models.transaction.ShippingAddressDto;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
public class MultipleSubsidiariesIT extends TestContainersInitializerIT implements MultipleSubsidiariesITTemplate {

    @MockBean
    private TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    // Given
    private final ShippingAddressDto referenceAddress = new ShippingAddressDto("Ammon", "US", null, "ID", "1875 South 25th East", "", "83406", false, null);
    private final UUID customerId = UUID.fromString("49755739-892a-4807-882c-68b0e209a980"); // complytId of an existing customer in the database
    private final String source = "1";

    private TransactionDto transactionDto;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        transactionDto = ITUtilities.stubTransactionDto(null, customerId,
                        ITUtilities.stubItemDto().withTotalPrice(new BigDecimal("10000")).withQuantity(new BigDecimal("2")).withUnitPrice(new BigDecimal("5000")))
                .withShippingAddress(referenceAddress);

        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));
    }

    @Override
    @Test
    @WithMockUser
    @Order(0)
    public void getOneSalesTaxTracking_NullSubsidiary_EstablishedByIsNullAndEconomicNexusEstablishedIsFalse() {
        // Given

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", "ID")
                        .queryParam("country", "US")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> {
                    assertNull(salesTaxTrackingDto.establishedBy());
                    assertFalse(salesTaxTrackingDto.economicNexusTracker().established());
                });
    }

    @Override
    @Test
    @WithMockUser
    @Order(0)
    public void getOneSalesTaxTracking_SubsidiaryB_EstablishedByIsNullAndEconomicNexusEstablishedIsFalse() {
        // Given

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", "ID")
                        .queryParam("country", "US")
                        .queryParam("subsidiary", "B")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> {
                    assertNull(salesTaxTrackingDto.establishedBy());
                    assertFalse(salesTaxTrackingDto.economicNexusTracker().established());
                });
    }


    @Override
    @Test
    @WithMockUser
    @Order(0)
    public void upsert_FirstTransactionToNullSubsidiary_AddsCalculationToNullSubsidiary_DidNotPassNexus() {
        // Given
        String externalId = "firstTransactionNullSubsidiary";
        TransactionDto givenTransaction = transactionDto.withExternalId(externalId);

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

    @Override
    @Test
    @WithMockUser
    @Order(0)
    public void upsert_SecondTransactionWithNonExistingSubsidiary_AddsCalculationToNullSubsidiary_DidNotPassNexus() {
        // Given
        String externalId = "secondTransactionWithNonExistingSubsidiary";
        TransactionDto givenTransaction = transactionDto.withExternalId(externalId)
                .withSubsidiary("nonExistingSubsidiary");

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

    @Override
    @Test
    @WithMockUser
    @Order(0)
    public void upsert_FirstTransactionToSubsidiaryA_AddsCalculationToSubsidiaryA_DidNotPassNexus() {
        // Given
        String externalId = "firstTransactionSubsidiaryA";
        TransactionDto givenTransaction = transactionDto.withExternalId(externalId)
                .withSubsidiary("A");

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

    @Override
    @Test
    @WithMockUser
    @Order(0)
    public void upsert_FirstTransactionToSubsidiaryB_AddsCalculationToSubsidiaryB_DidNotPassNexus() {
        // Given
        String externalId = "firstTransactionSubsidiaryB";
        TransactionDto givenTransaction = transactionDto.withExternalId(externalId)
                .withSubsidiary("B");

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

    @Override
    @Test
    @WithMockUser
    @Order(0)
    public void upsert_FirstTransactionToSubsidiaryC_AddsCalculationToSubsidiaryC_DidNotPassNexus() {
        // Given
        String externalId = "firstTransactionSubsidiaryC";
        TransactionDto givenTransaction = transactionDto.withExternalId(externalId)
                .withSubsidiary("C");

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

    @Override
    @Test
    @WithMockUser
    @Order(1)
    public void upsert_SecondTransactionToSubsidiaryC_AddsCalculationToSubsidiaryC_DidNotPassNexus() {
        // Given
        String externalId = "secondTransactionSubsidiaryC";
        List<ItemDto> items = new ArrayList<>() {{
            add(transactionDto.items().get(0).withUnitPrice(BigDecimal.valueOf(89999)).withTotalPrice(BigDecimal.valueOf(89999)).withQuantity(BigDecimal.valueOf(1)));
        }};
        TransactionDto givenTransaction = transactionDto.withExternalId(externalId)
                .withSubsidiary("C")
                .withItems(items);

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

    @Override
    @Test
    @WithMockUser
    @Order(1)
    public void upsert_SecondTransactionToSubsidiaryB_AddsCalculationToSubsidiaryB_PassedNexus() {
        // Given
        String externalId = "secondTransactionSubsidiaryB";
        List<ItemDto> items = new ArrayList<>() {{
            add(transactionDto.items().get(0).withUnitPrice(BigDecimal.valueOf(90000)).withTotalPrice(BigDecimal.valueOf(90000)).withQuantity(BigDecimal.valueOf(1)));
        }};
        TransactionDto givenTransaction = transactionDto.withExternalId(externalId)
                .withSubsidiary("B")
                .withItems(items);

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

    @Override
    @Test
    @WithMockUser
    @Order(2)
    public void upsert_SecondTransactionToSubsidiaryA_TransactionReturnedWithSalesTax() {
        // Given
        String externalId = "secondTransactionSubsidiaryA";
        TransactionDto givenTransaction = transactionDto.withExternalId(externalId)
                .withSubsidiary("A");

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

    @Override
    @Test
    @WithMockUser
    @Order(2)
    public void upsert_SecondTransactionToNullSubsidiary_TransactionReturnedWithSalesTax() {
        // Given
        String externalId = "secondTransactionNullSubsidiaryA";
        TransactionDto givenTransaction = transactionDto.withExternalId(externalId);

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

    @Override
    @Test
    @WithMockUser
    @Order(3)
    public void upsert_ThirdTransactionToNullSubsidiaryB_TransactionReturnedWithSalesTax() {
        // Given
        String externalId = "thirdTransactionSubsidiaryB";
        TransactionDto givenTransaction = transactionDto.withExternalId(externalId)
                .withSubsidiary("B");

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

    @Override
    @Test
    @WithMockUser
    @Order(2)
    public void upsert_ThirdTransactionToNullSubsidiaryC_TransactionReturnedWithSalesTax() {
        // Given
        String externalId = "thirdTransactionSubsidiaryC";
        TransactionDto givenTransaction = transactionDto.withExternalId(externalId)
                .withSubsidiary("C");

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

    @Override
    @Test
    @WithMockUser
    @Order(2)
    public void upsert_TransactionWithNonExistingSubsidiary_TransactionReturnedWithSalesTax() {
        // Given
        String externalId = "transactionWithNonExistingSubsidiary";
        TransactionDto givenTransaction = transactionDto.withExternalId(externalId)
                .withSubsidiary("NonExistingSubsidiary");

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

    @Override
    @Test
    @WithMockUser
    @Order(2)
    public void upsert_TransactionWithNullSubsidiary_TransactionReturnedWithSalesTax() {
        // Given
        String externalId = "TransactionWithNullSubsidiaryAndSalesTax";
        TransactionDto givenTransaction = transactionDto.withExternalId(externalId)
                .withSubsidiary("NonExistingSubsidiary");

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

    @Override
    @Test
    @WithMockUser
    @Order(3)
    public void getOneSalesTaxTracking_SubsidiaryA_EstablishedBySubsidiaryB() {
        // Given

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", "ID")
                        .queryParam("country", "US")
                        .queryParam("subsidiary", "A")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> assertEquals(salesTaxTrackingDto.establishedBy(), "B"));
    }

    @Override
    @Test
    @WithMockUser
    @Order(3)
    public void getOneSalesTaxTracking_SubsidiaryB_EstablishedBySubsidiaryB() {
        // Given
        String externalId = "transactionWithNonExistingSubsidiary";
        TransactionDto givenTransaction = transactionDto.withExternalId(externalId)
                .withSubsidiary("NonExistingSubsidiary");

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", "ID")
                        .queryParam("country", "US")
                        .queryParam("subsidiary", "B")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> assertEquals(salesTaxTrackingDto.establishedBy(), "B"));
    }

    @Override
    @Test
    @WithMockUser
    @Order(3)
    public void getOneSalesTaxTracking_SubsidiaryC_EstablishedBySubsidiaryB() {
        // Given

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", "ID")
                        .queryParam("country", "US")
                        .queryParam("subsidiary", "C")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> assertEquals(salesTaxTrackingDto.establishedBy(), "B"));
    }

    @Override
    @Test
    @WithMockUser
    @Order(3)
    public void getOneSalesTaxTracking_NullSubsidiary_EstablishedBySubsidiaryB() {
        // Given

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", "ID")
                        .queryParam("country", "US")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> assertEquals(salesTaxTrackingDto.establishedBy(), "B"));
    }

}