package integration.scenarios;

import com.complyt.SalesTaxApplication;
import com.complyt.config.SecurityConfig;
import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.TransactionDto;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.routers.CustomerRouter;
import com.complyt.v1.routers.SalesTaxTrackingRouter;
import com.complyt.v1.routers.TransactionRouter;
import integration.MongoContainerInitializerIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import testUtils.integration_test.ITUtilities;

import java.util.LinkedHashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {SalesTaxApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles({"integration-test", "stubFastTax"})
@AutoConfigureWebTestClient()
public class MultitenancyIT extends MongoContainerInitializerIT implements MultitenancyITTemplate {

    private String source = "1";
    private  UUID customerComplytIdFromDatabase = UUID.fromString("4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5");

    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        //when(tenantResolver.resolve()).thenReturn(Mono.just("multi_tenancy_it_tenant"));
    }

    @Test
    @Override
    public void getCustomer_ExistsInOtherTenant_Returns404() {
        // Given
        String externalId = "1586";

        // Then
        webTestClient
                .mutateWith(mockJwt().jwt(ITUtilities.stubJwt().build()))
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void getTransaction_ExistsInOtherTenant_Returns404() {
        // Given
        String externalId = "10000";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void getSalesTaxTracking_ExistsInOtherTenant_Returns404() {
        // Given
        String stateName = "Arizona";

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/state/" + stateName)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void putCustomer_WithComplytIdAndExistsInOtherTenant_Returns400DataConflict() {
        // Given - details of a customer from the database: "Bestcompany Com"
        String externalId = "1586";
        CustomerDto customerDto = ITUtilities.stubCustomerDto(externalId).withComplytId(customerComplytIdFromDatabase);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(map.get("message"), GenericErrorMessages.DATA_CONFLICT_ERROR));
    }

    @Test
    @Override
    @WithMockUser
    public void putTransaction_WithComplytIdAndExistsInOtherTenant_Returns400DataConflict() {
        // Given - details of a customer from the database
        String externalId = "10000";
        UUID complytId =  UUID.fromString("8b377411-da68-4807-8616-ee3a07c849f8");
        TransactionDto transactionDto = ITUtilities.stubTransactionDto(externalId,customerComplytIdFromDatabase).withComplytId(complytId);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(map.get("message"), GenericErrorMessages.DATA_CONFLICT_ERROR));
    }

    @Test
    @Override
    @WithMockUser
    public void putSalesTaxTracking_WithComplytIdAndExistsInOtherTenant_Returns400DataConflict() {
        // Given - details of a customer from the database
        StateDto state = new StateDto("AZ","04","Arizona");
        UUID complytId =  UUID.fromString("cba95b8d-ef9b-4f4d-831d-377621556b50");
        SalesTaxTrackingDto salesTaxTrackingDto = ITUtilities.stubSalesTaxTrackingDto(state).withComplytId(complytId);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state.name())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(map.get("message"), GenericErrorMessages.DATA_CONFLICT_ERROR));
    }
}
