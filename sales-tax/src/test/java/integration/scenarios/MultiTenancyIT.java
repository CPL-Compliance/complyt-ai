package integration.scenarios;

import com.complyt.SalesTaxApplication;
import com.complyt.business.sales_tax.sales_tax_web_clients.ComplytSalesTaxRatesClientWrapper;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.TransactionDto;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.routers.CustomerRouter;
import com.complyt.v1.routers.SalesTaxTrackingRouter;
import com.complyt.v1.routers.TransactionRouter;
import integration.TestContainersInitializerIT;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import testUtils.integration_test.ITUtilities;

import java.util.LinkedHashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.*;

@SpringBootTest(classes = {SalesTaxApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient()
public class MultiTenancyIT extends TestContainersInitializerIT implements MultiTenancyITTemplate {

    private final JwtMutator differentTenantMutator = mockJwt().jwt(ITUtilities.stubJwt().claim("tenant_id", "other_it_tenant").build());
    private final JwtMutator defaultTenantMutator = mockJwt().jwt(ITUtilities.stubJwt().build());
    private final String source = "1";
    @Mock
    ComplytSalesTaxRatesClientWrapper complytSalesTaxRatesClientWrapper;
    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @Order(1)
    @Test
    @Override
    public void getCustomer_ExistsInOtherTenant_Returns404() {
        // Given
        String externalId = "1586";

        // Then
        webTestClient
                .mutateWith(differentTenantMutator)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        webTestClient
                .mutateWith(defaultTenantMutator)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Order(1)
    @Test
    @Override
    public void getTransaction_ExistsInOtherTenant_Returns404() {
        // Given
        String externalId = "10002";

        // Then
        webTestClient
                .mutateWith(differentTenantMutator)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        webTestClient
                .mutateWith(defaultTenantMutator)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Order(1)
    @Test
    @Override
    public void getSalesTaxTracking_ExistsInOtherTenant_Returns404() {
        // Given
        String stateName = "Arizona";

        // Then
        webTestClient
                .mutateWith(differentTenantMutator)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        webTestClient
                .mutateWith(defaultTenantMutator)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Order(1)
    @Test
    @Override
    public void putCustomer_WithComplytIdAndExistsInOtherTenant_Returns400DataConflict() {
        // Given - details of a customer from the database: "Bestcompany Com"
        String externalId = "1586";

        // Then
        webTestClient
                .mutateWith(defaultTenantMutator)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(customerDto ->

                        webTestClient
                                .mutateWith(csrf())
                                .mutateWith(differentTenantMutator)
                                .put()
                                .uri(uriBuilder -> uriBuilder
                                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                                        .build())
                                .accept(MediaType.APPLICATION_JSON)
                                .bodyValue(customerDto)
                                .exchange()
                                .expectStatus().isBadRequest()
                                .expectBody(LinkedHashMap.class)
                                .value(map -> assertEquals(map.get("message"), "[" + DtoErrorMessages.COMPLYT_ID_IN_A_NEW_RECORD_ERROR + "]")));
    }

    @Order(1)
    @Test
    @Override
    public void putTransaction_WithComplytIdAndExistsInOtherTenant_Returns400DataConflict() {
        // Given - details of a transaction from the database
        String externalId = "10002";

        // Then
        webTestClient
                .mutateWith(defaultTenantMutator)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transactionDto ->

                        webTestClient
                                .mutateWith(csrf())
                                .mutateWith(differentTenantMutator)
                                .put()
                                .uri(uriBuilder -> uriBuilder
                                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                                        .build())
                                .accept(MediaType.APPLICATION_JSON)
                                .bodyValue(transactionDto)
                                .exchange()
                                .expectStatus().isBadRequest()
                                .expectBody(LinkedHashMap.class)
                                .value(map -> assertEquals(map.get("message"), "[" + DtoErrorMessages.COMPLYT_ID_IN_A_NEW_RECORD_ERROR + "]")));

    }

    @Order(1)
    @Test
    @Override
    public void putSalesTaxTracking_WithComplytIdAndExistsInOtherTenant_Returns400DataConflict() {
        // Given - details of a salesTaxTracking from the database
        StateDto state = new StateDto("AZ", "04", "Arizona");

        // Then
        webTestClient
                .mutateWith(defaultTenantMutator)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state.name())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto ->

                        webTestClient
                                .mutateWith(csrf())
                                .mutateWith(differentTenantMutator)
                                .put()
                                .uri(uriBuilder -> uriBuilder
                                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state.name())
                                        .build())
                                .accept(MediaType.APPLICATION_JSON)
                                .bodyValue(salesTaxTrackingDto)
                                .exchange()
                                .expectStatus().isBadRequest()
                                .expectBody(LinkedHashMap.class)
                                .value(map -> assertEquals(map.get("message"), "[" + DtoErrorMessages.COMPLYT_ID_IN_A_NEW_RECORD_ERROR + "]")));

    }

    @Order(3)
    @Test
    @Override
    public void putCustomer_ExistsInOtherTenant_Returns200WithoutDataLeak() {
        // Given - details of a customer from the database: "Bestcompany Com"
        String externalId = "1586";

        // Then
        webTestClient
                .mutateWith(defaultTenantMutator)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(customerDto -> {

                    webTestClient
                            .mutateWith(csrf())
                            .mutateWith(differentTenantMutator)
                            .put()
                            .uri(uriBuilder -> uriBuilder
                                    .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                                    .build())
                            .accept(MediaType.APPLICATION_JSON)
                            .bodyValue(customerDto.withComplytId(null))
                            .exchange()
                            .expectStatus().isCreated()
                            .expectBody(CustomerDto.class)
                            .value(receivedCustomerDto -> assertNotEquals(receivedCustomerDto.complytId(), customerDto.complytId()));

                    webTestClient
                            .mutateWith(defaultTenantMutator)
                            .get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                                    .build())
                            .accept(MediaType.APPLICATION_JSON)
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody(CustomerDto.class)
                            .value(receivedCustomerDto -> assertEquals(customerDto, receivedCustomerDto));
                });
    }

    @Order(4)
    @Test
    @Override
    public void putTransaction_ExistsInOtherTenant_Returns200WithoutDataLeak() {
        // Given - details of a transaction from the database
        String externalId = "10002";
        String customerExternalId = "1586";

        // Then
        webTestClient
                .mutateWith(defaultTenantMutator)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transactionDto ->

                        webTestClient
                                .mutateWith(differentTenantMutator)
                                .get()
                                .uri(uriBuilder -> uriBuilder
                                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + customerExternalId)
                                        .build())
                                .accept(MediaType.APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody(CustomerDto.class)
                                .value(customerDto -> {

                                    webTestClient
                                            .mutateWith(csrf())
                                            .mutateWith(differentTenantMutator)
                                            .put()
                                            .uri(uriBuilder -> uriBuilder
                                                    .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                                                    .build())
                                            .accept(MediaType.APPLICATION_JSON)
                                            .bodyValue(transactionDto.withComplytId(null).withCustomerId(customerDto.complytId()))
                                            .exchange()
                                            .expectStatus().isCreated()
                                            .expectBody(TransactionDto.class)
                                            .value(receivedTransactionDto -> assertNotEquals(receivedTransactionDto.complytId(), transactionDto.complytId()));

                                    webTestClient
                                            .mutateWith(defaultTenantMutator)
                                            .get()
                                            .uri(uriBuilder -> uriBuilder
                                                    .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                                                    .build())
                                            .accept(MediaType.APPLICATION_JSON)
                                            .exchange()
                                            .expectStatus().isOk()
                                            .expectBody(TransactionDto.class)
                                            .value(receivedTransactionDto -> assertEquals(transactionDto, receivedTransactionDto));

                                }));
    }

    @Order(2)
    @Test
    @Override
    public void putSalesTaxTracking_ExistsInOtherTenant_Returns200WithoutDataLeak() {
        // Given - details of a customer from the database: "Bestcompany Com"
        StateDto state = new StateDto("AZ", "04", "Arizona");

        // Then
        webTestClient
                .mutateWith(defaultTenantMutator)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state.abbreviation())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(salesTaxTrackingDto -> {

                    webTestClient
                            .mutateWith(csrf())
                            .mutateWith(differentTenantMutator)
                            .put()
                            .uri(uriBuilder -> uriBuilder
                                    .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state.abbreviation())
                                    .build())
                            .accept(MediaType.APPLICATION_JSON)
                            .bodyValue(salesTaxTrackingDto.withComplytId(null))
                            .exchange()
                            .expectStatus().isCreated()
                            .expectBody(SalesTaxTrackingDto.class)
                            .value(receivedSalesTaxTrackingDto -> assertNotEquals(receivedSalesTaxTrackingDto.complytId(), salesTaxTrackingDto.complytId()));

                    webTestClient
                            .mutateWith(defaultTenantMutator)
                            .get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state.abbreviation())
                                    .build())
                            .accept(MediaType.APPLICATION_JSON)
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody(SalesTaxTrackingDto.class)
                            .value(receivedSalesTaxTrackingDto -> assertEquals(salesTaxTrackingDto, receivedSalesTaxTrackingDto));
                });
    }

    @Order(5)
    @Test
    @Override
    public void putTransaction_CustomerIdExistingInAnotherTenant_Returns404() {
        // Given
        String externalId = "10001";
        UUID customerId = UUID.fromString("4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5");
        TransactionDto transactionDto = ITUtilities.stubTransactionDto(externalId, customerId);

        // Then
        webTestClient
                .mutateWith(csrf())
                .mutateWith(differentTenantMutator)
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto
                        .withShippingAddress(transactionDto.shippingAddress().withState("AZ")))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(5)
    @Test
    @Override
    public void putTransaction_SalesTaxTrackingOfShippingAddressExistInAnotherTenant_Returns404() {
        // Given
        String externalId = "10001";
        String customerExternalId = "1586";

        webTestClient
                .mutateWith(differentTenantMutator)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + customerExternalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(customerDto -> {

                    TransactionDto transactionDto = ITUtilities.stubTransactionDto(externalId, customerDto.complytId());

                    webTestClient
                            .mutateWith(csrf())
                            .mutateWith(differentTenantMutator)
                            .put()
                            .uri(uriBuilder -> uriBuilder
                                    .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                                    .build())
                            .accept(MediaType.APPLICATION_JSON)
                            .bodyValue(transactionDto
                                    .withShippingAddress(transactionDto.shippingAddress().withState("CA")))
                            .exchange()
                            .expectStatus().isNotFound();
                });
    }

    @Order(6)
    @Test
    @Override
    public void putSalesTaxTracking_SalesTaxTrackingWithStateRuleOfTaxableYear_Returns201() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(new StateDto("CA", "3463456", "California"));

        // Then
        webTestClient
                .mutateWith(csrf())
                .mutateWith(differentTenantMutator)
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + salesTaxTrackingDto.state().name())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isCreated();
    }

    @Order(7)
    @Test
    @Override
    public void putTransaction_ClientTrackingNotExistingForTenant_Returns404() {
        // Given
        String externalId = "10002";
        String customerExternalId = "1586";

        // Then
        webTestClient
                .mutateWith(differentTenantMutator)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + customerExternalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(customerDto -> {

                    TransactionDto transactionDto = ITUtilities.stubTransactionDto(externalId, customerDto.complytId());

                    webTestClient
                            .mutateWith(csrf())
                            .mutateWith(differentTenantMutator)
                            .put()
                            .uri(uriBuilder -> uriBuilder
                                    .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                                    .build())
                            .accept(MediaType.APPLICATION_JSON)
                            .bodyValue(transactionDto)
                            .exchange()
                            .expectStatus().isNotFound();
                });
    }
}