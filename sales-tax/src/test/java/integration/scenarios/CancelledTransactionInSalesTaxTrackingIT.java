package integration.scenarios;

import com.complyt.SalesTaxApplication;
import com.complyt.domain.nexus.*;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.transaction.ItemDto;
import com.complyt.v1.models.transaction.MandatoryAddressDto;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.models.transaction.TransactionStatusDto;
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
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.integration_test.ITUtilities;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CancelledTransactionInSalesTaxTrackingIT extends TestContainersInitializerIT implements CancelledTransactionInSalesTaxTrackingITTemplate {

    @MockBean
    TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    // Given
    private final UUID customerId = UUID.fromString("4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5"); // complytId of an existing customer in the database
    private final MandatoryAddressDto referenceAddress = new MandatoryAddressDto("Grand Rapids", "US", null, "MI", "417 Michigan St NE", "", "49503", false);
    private final String source = "1";
    private final String usaCountry = "USA";
    private UUID transactionUUID;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));
    }

    // Successfully upserting salesTaxTracking for Arizona
    @Order(0)
    @Test
    @WithMockUser
    public void salesTaxTracking_upsertByCountryAndState_UsaCountryDoesntExists_Returns201() {
        // Given
        StateDto stateDto = new StateDto("MI", "26", "Michigan");
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto("USA", stateDto)
                .withState(stateDto);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", usaCountry)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SalesTaxTrackingDto.class)
                .value(resultSalesTaxTrackingDto -> assertEquals(
                        salesTaxTrackingDto
                                .withComplytId(resultSalesTaxTrackingDto.complytId())
                                .withNexusCalculationSummaries(Map.of())
                                .withNexusStateRule(ITUtilities.stubMichiganNexusStateRuleDto())
                                .withCountry(usaCountry.toUpperCase())
                        , resultSalesTaxTrackingDto)
                );
    }

    // Successfully creates a new transaction with shipping address Michigan
    // Making sure that the transaction is added to transactionNexusSummaries in the corresponding salesTaxTracking
    @Order(1)
    @Test
    @WithMockUser
    public void transaction_upsertByExternalIdAndSource_ActiveUsaTransaction_InSalesTaxTrackingAndReturns201() {
        String externalId = "newNonExistingTransactionIDUsaAbbreviationForThisScenario";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true)
                .withShippingAddress(referenceAddress)
                .withTotalItemsAmount(BigDecimal.ONE);

        givenTransaction = givenTransaction.withShippingAddress(givenTransaction.shippingAddress().withCountry("USA")); //salestaxtracking is approved and physical

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
                    transactionUUID = transactionDto.complytId();

                    // Check the MongoDB to verify if the transaction is logged correctly
                    Query query = new Query(Criteria.where("state.abbreviation").is("MI"));
                    StepVerifier.create(reactiveMongoTemplate.findOne(query, SalesTaxTracking.class, "sales_tax_tracking"))
                            .assertNext(salesTaxTracking -> {
                                Map<UUID, TransactionNexusSummary> summaries = salesTaxTracking.getTransactionNexusSummaries();
                                assertTrue(summaries.containsKey(transactionUUID));
                            })
                            .verifyComplete();
                });
    }

    // Successfully creates a new transaction with shipping address Michigan and transactionStatus CANCELLED
    // Making sure that the transaction is saved to the DB but not added to transactionNexusSummaries
    @Order(2)
    @Test
    @WithMockUser
    public void transaction_upsertByExternalIdAndSource_CancelledUsaTransaction_NotInSalesTaxTrackingAndReturns204() {
        String externalId = "newNonExistingTransactionIDUsaAbbreviationForThisScenario1";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true)
                .withShippingAddress(referenceAddress)
                .withTransactionStatus(TransactionStatusDto.CANCELLED);

        givenTransaction = givenTransaction.withShippingAddress(givenTransaction.shippingAddress().withCountry("USA")); //salestaxtracking is approved and physical

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
                .expectStatus().isNoContent();

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transactionDto -> {
                    transactionUUID = transactionDto.complytId();
                    assertSame(transactionDto.transactionStatus(), TransactionStatusDto.CANCELLED);

                    // Check the MongoDB to verify if the transaction is logged correctly
                    Query query = new Query(Criteria.where("state.abbreviation").is("MI"));
                    StepVerifier.create(reactiveMongoTemplate.findOne(query, SalesTaxTracking.class, "sales_tax_tracking"))
                            .assertNext(salesTaxTracking -> {
                                Map<UUID, TransactionNexusSummary> summaries = salesTaxTracking.getTransactionNexusSummaries();
                                assertFalse(summaries.containsKey(transactionUUID));
                            })
                            .verifyComplete();
                });

    }

    // Successfully creates a new transaction with shipping address Michigan
    // making sure the new transaction was added to transactionNexusSummaries in SalesTaxTracking
    // then Successfully deletes the transaction and making sure it has been deleted from the transactionNexusSummaries in SalesTaxTracking
    @Order(4)
    @Test
    @WithMockUser
    public void transaction_deleteTransaction_ActiveUsaTransaction_NotInSalesTaxTrackingAndReturns204() {
        String externalId = "newNonExistingTransactionIDUsaAbbreviationForThisScenario2";
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true)
                .withShippingAddress(referenceAddress)
                .withTotalItemsAmount(BigDecimal.ONE);

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
                    transactionUUID = transactionDto.complytId();
                    Query query = new Query(Criteria.where("state.abbreviation").is("MI"));
                    StepVerifier.create(reactiveMongoTemplate.findOne(query, SalesTaxTracking.class, "sales_tax_tracking"))
                            .assertNext(salesTaxTracking -> {
                                Map<UUID, TransactionNexusSummary> summaries = salesTaxTracking.getTransactionNexusSummaries();
                                assertTrue(summaries.containsKey(transactionUUID));
                            });

                    webTestClient
                            .mutateWith(csrf())
                            .delete()
                            .uri(uriBuilder -> uriBuilder
                                    .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                                    .build())
                            .accept(MediaType.APPLICATION_JSON)
                            .exchange()
                            .expectStatus().isNoContent();

                    // Check the MongoDB to verify if the transaction is logged correctly
                    StepVerifier.create(reactiveMongoTemplate.findOne(query, SalesTaxTracking.class, "sales_tax_tracking"))
                            .assertNext(salesTaxTracking -> {
                                Map<UUID, TransactionNexusSummary> summaries = salesTaxTracking.getTransactionNexusSummaries();
                                assertFalse(summaries.containsKey(transactionUUID));
                            })
                            .verifyComplete();
                });
    }

    // Successfully creates a new transaction with shipping address Michigan with amount that crosses the economic nexus rule
    // making sure the new transaction was added to transactionNexusSummaries in SalesTaxTracking and nexus was established
    // then Successfully deletes the transaction and making sure it hadn't been deleted from the transactionNexusSummaries in SalesTaxTracking and the nexus is still true
    @Order(5)
    @Test
    @WithMockUser
    public void transaction_deleteTransaction_ActiveUsaTransactionPassesNexus_InSalesTaxTrackingAndReturns204() {
        String externalId = "newNonExistingTransactionIDUsaAbbreviationForThisScenario3";
        List<ItemDto> items = List.of(ITUtilities.stubItemDto().withTotalPrice(new BigDecimal("1000000")));
        TransactionDto givenTransaction = ITUtilities.stubTransactionDto(externalId, customerId)
                .withTaxInclusive(true)
                .withShippingAddress(referenceAddress)
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
                .value(transactionDto -> {
                    transactionUUID = transactionDto.complytId();
                    Query query = new Query(Criteria.where("state.abbreviation").is("MI"));
                    StepVerifier.create(reactiveMongoTemplate.findOne(query, SalesTaxTracking.class, "sales_tax_tracking"))
                            .assertNext(salesTaxTracking -> {
                                Map<UUID, TransactionNexusSummary> summaries = salesTaxTracking.getTransactionNexusSummaries();
                                assertTrue(summaries.containsKey(transactionUUID));
                                PhysicalNexusTracker physicalNexusTracker = salesTaxTracking.getPhysicalNexusTracker();
                                assertFalse(physicalNexusTracker.isEstablished());
                                EconomicNexusTracker economicNexusTracker = salesTaxTracking.getEconomicNexusTracker();
                                assertTrue(economicNexusTracker.isEstablished());
                            });

                    webTestClient
                            .mutateWith(csrf())
                            .delete()
                            .uri(uriBuilder -> uriBuilder
                                    .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                                    .build())
                            .accept(MediaType.APPLICATION_JSON)
                            .exchange()
                            .expectStatus().isNoContent();

                    // Check the MongoDB to verify if the transaction is logged correctly
                    StepVerifier.create(reactiveMongoTemplate.findOne(query, SalesTaxTracking.class, "sales_tax_tracking"))
                            .assertNext(salesTaxTracking -> {
                                Map<UUID, TransactionNexusSummary> summaries = salesTaxTracking.getTransactionNexusSummaries();
                                assertTrue(summaries.containsKey(transactionUUID));
                                PhysicalNexusTracker physicalNexusTracker = salesTaxTracking.getPhysicalNexusTracker();
                                assertFalse(physicalNexusTracker.isEstablished());
                                EconomicNexusTracker economicNexusTracker = salesTaxTracking.getEconomicNexusTracker();
                                assertTrue(economicNexusTracker.isEstablished());
                            })
                            .verifyComplete();
                });
    }
}