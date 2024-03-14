package integration.services.sales_tax;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExemptionEndpointsIT extends TestContainersInitializerIT implements ExemptionEndpointsITTemplate {

    @Order(0)
    @Override
    @Test
    public void patch_PatchesOneField_ReturnsPatchedResource() {
        String complytId = "2aa5809f-301d-44f3-9081-b4f32613463c";
        String map = """
                {
                    "customerId": "2155317e-877d-4c08-8c5c-8cd2b485d80a"
                }
                """;

        WEB_TEST_CLIENT
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.EXEMPTION_BASE_URL + "/complytId/" + complytId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(map)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LinkedHashMap.class)
                .value(exemption -> assertEquals(exemption.get(0).get("customerId"),"2155317e-877d-4c08-8c5c-8cd2b485d80a"));
    }

    @Order(1)
    @Override
    @Test
    public void patch_PatchesTwoFields_ReturnsPatchedResource() {
        String complytId = "2aa5809f-301d-44f3-9081-b4f32613463c";

        WEB_TEST_CLIENT
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.EXEMPTION_BASE_URL + "/complytId/" + complytId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.exemptionPatchTwoFieldsJsonExample())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LinkedHashMap.class)
                .value(exemption -> {
                    assertEquals(exemption.get(0).get("customerId"),"2155317e-877d-4c08-8c5c-8cd2b485d80b");
                    assertTrue(exemption.get(0).get("classification").toString().contains("patchedDescription"));
                });
    }
}
