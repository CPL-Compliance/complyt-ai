package integration.endpoints;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import integration.MongoContainerInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
public class TransactionEndpointsIT extends MongoContainerInitializer implements TransactionEndpointsITTemplate {

    @MockBean
    TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
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
    public void upsertByExternalIdAndSource_ExistsAndCustomerDoesntExists_Returns404() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntExistsAndSaleTaxTrackingDoesntExists_Returns500() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_ExistsAndSaleTaxTrackingDoesntExists_Returns500() {

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

    @Test
    @Override
    @WithMockUser
    public void deleteByExternalIdAndSource_Exists_Returns204() {

    }

    @Test
    @Override
    @WithMockUser
    public void deleteByExternalIdAndSource_DoesntExists_Returns404() {

    }

    @Test
    @Override
    @WithMockUser
    public void deleteByExternalIdAndSource_UnauthenticatedUser_Returns401() {

    }

    @Test
    @Override
    @WithMockUser
    public void deleteByExternalIdAndSource_UserWithoutAuthorities_Returns403() {

    }

    @Test
    @Override
    @WithMockUser
    public void deleteByExternalIdAndSource_UserWithoutCSRFToken_Returns403() {

    }

    @Test
    @Override
    @WithMockUser
    public void deleteByExternalIdAndSource_InternalServerError_Returns500() {

    }

    @Test
    @Override
    @WithMockUser
    public void deleteByExternalIdAndSource_NullHandler_ThrowsNullPointerException() {

    }
}
