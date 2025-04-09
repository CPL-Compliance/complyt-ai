package integration.endpoints;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.customer.exemption.ClassificationDto;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.models.customer.exemption.ExemptionTypeDto;
import com.complyt.v1.routers.ExemptionRouter;
import integration.TestContainersInitializerIT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import testUtils.integration_test.ITUtilities;
import testUtils.integration_test.WithMockJwt;

import java.util.LinkedHashMap;
import java.util.UUID;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExemptionEndpointsIT extends TestContainersInitializerIT implements ExemptionEndpointsITITTemplate {

    @MockBean
    TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;
    String expectedComplyId = "f6da33d2-fc09-4c94-a01a-e89305163a2f";
    ClassificationDto classificationToPatch = ITUtilities.createClassificationDto();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }


    @Override
    @Test
    @WithMockJwt
    public void patch_PatchesOneField_ReturnsPatchedResource() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("classification", classificationToPatch);
        }};

        webTestClient
                .mutateWith(csrf()).patch()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + expectedComplyId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(map)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .value(returnedExemption -> Assertions.assertEquals(returnedExemption.classification(), classificationToPatch));
    }

    @Override
    @Test
    @WithMockJwt
    public void patch_PatchesTwoFields_ReturnsPatchedResource() {
        ExemptionTypeDto exemptionTypeToPatch = ExemptionTypeDto.PARTIALLY;
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("classification", classificationToPatch);
            put("exemptionType", exemptionTypeToPatch);
        }};

        webTestClient
                .mutateWith(csrf()).patch()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + expectedComplyId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(map)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .value(returnedExemption -> {
                    Assertions.assertEquals(returnedExemption.classification(), classificationToPatch);
                    Assertions.assertEquals(returnedExemption.exemptionType(), exemptionTypeToPatch);
                });
    }

    @Override
    @Test
    @WithMockJwt
    public void update_UpdatesExemption_ReturnsExemptionWithCustomer() {
        UUID complytId = UUID.fromString("6eaa133c-df9c-4f88-bba9-6dd3845c803a");
        StateDto updatedState = new StateDto("NY", "0", "New York");

        ExemptionDto exemptionToUpdate = ITUtilities.createExemptionDto()
                .withComplytId(complytId)
                .withState(updatedState);

        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(exemptionToUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .value(returnedExemption -> Assertions.assertEquals(updatedState, returnedExemption.state()));
    }

    @Override
    @Test
    @WithMockJwt
    public void update_CustomerNotFound_Throws404NotFound() {
        UUID exemptionComplytId = UUID.fromString("6eaa133c-df9c-4f88-bba9-6dd3845c803a");
        UUID nonExistingCustomerId = UUID.fromString("6eaa133c-df9c-4f88-bba9-6dd3845c803a");

        ExemptionDto exemptionToUpdate = ITUtilities.createExemptionDto()
                .withComplytId(exemptionComplytId)
                .withCustomerId(nonExistingCustomerId);


        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + exemptionComplytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(exemptionToUpdate)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Override
    @Test
    @WithMockJwt
    public void getByComplytId_Exists_Returns200() {
        UUID complytId = UUID.fromString("2aa5809f-301d-44f3-9081-b4f32613463c");

        webTestClient
                .mutateWith(csrf()).get()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class);
    }

    @Override
    @Test
    @WithMockJwt
    public void getByComplytId_PathVariableInvalid_Returns400() {
        String complytId = "null";

        webTestClient
                .mutateWith(csrf()).get()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Override
    @Test
    @WithMockJwt
    public void getByComplytId_DoesntExists_Returns404() {
        UUID complytId = UUID.fromString("2aa5809f-301d-44f3-9081-b4f32613463b");

        webTestClient
                .mutateWith(csrf()).get()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}