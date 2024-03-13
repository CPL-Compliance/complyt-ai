package integration.endpoints;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.customer.exemption.ClassificationDto;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.models.customer.exemption.ExemptionTypeDto;
import com.complyt.v1.routers.ExemptionRouter;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import testUtils.integration_test.ITUtilities;

import java.util.LinkedHashMap;

import static org.mockito.Mockito.when;
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

    @BeforeEach
    void setup() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));
    }


    @Override
    @Test
    @WithMockUser
    public void patch_PatchesOneField_ReturnsPatchedResource() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("classification", classificationToPatch);
        }};

        webTestClient
                .mutateWith(csrf())
                .patch()
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
    @WithMockUser
    public void patch_PatchesTwoFields_ReturnsPatchedResource() {
        ExemptionTypeDto exemptionTypeToPatch = ExemptionTypeDto.PARTIALLY;
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("classification", classificationToPatch);
            put("exemptionType", exemptionTypeToPatch);
        }};

        webTestClient
                .mutateWith(csrf())
                .patch()
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

}