package integration;

import com.complyt.SalesTaxApplication;
import com.complyt.domain.Transaction;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.ItemDto;
import com.complyt.v1.models.MandatoryAddressDto;
import com.complyt.v1.models.TransactionDto;
import com.complyt.v1.routers.TransactionRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
public class TransactionApiIT extends MongoContainerInitializer implements TransactionApiITTemplate {

    @MockBean
    TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach void setup() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntExistsAndCustomerDoesntExists_Returns404() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntExistsAndSaleTaxTrackingDoesntExists_Returns500() {

    }

    @Override
    public void upsertByExternalIdAndSource_DoesntExistsAndPassedEconomicNexus_Returns200() {
        TransactionDto transactionDto = new TransactionDto(UUID.randomUUID(), "27290", "1",
                List.of(new ItemDto(10000, 6, 60000, "some description", "Hardware", "C1S1",
                        null, null, false, 0, null, null)),
                null, new MandatoryAddressDto("Acampo", "US", null, "CA", "1525 R Jahant Rd", ));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/1/externalId/27290")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(transactionDtos -> LOGGER.info(transactionDtos.size() + " transactions: " + transactionDtos));
    }

    @Override
    public void upsertByExternalIdAndSource_DoesntExistsAndHavePhysicalNexus_Returns200() {

    }

    @Test
    @Override
    @WithMockUser
    public void getAllBySource_Exists_Returns200() {

    }

    @Test
    @Override
    @WithMockUser
    public void getAllBySource_DoesntExists_Returns200EmptyList() {

    }

    @Test
    @Override
    @WithMockUser
    public void getAll_Exists_Returns200() {

    }

    @Test
    @Override
    @WithMockUser
    public void getByAll_DoesntExists_Returns200EmptyList() {

    }

    @Test
    @Override
    @WithMockUser
    public void getByExternalIdAndSource_Exists_Returns200() {

    }

    @Test
    @Override
    @WithMockUser
    public void getByExternalIdAndSource_DoesntExists_Returns404() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_Exists_Returns200() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntExists_Returns201() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntExistsWithComplytId_Returns400ConflictedData() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_ConflictingSource_Returns400ConflictedData() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_ConflictingExternalId_Returns400ConflictedData() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntPassValidation_Returns400CValidationError() {

    }
}
