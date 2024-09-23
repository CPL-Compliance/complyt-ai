package integration.services.files;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;


public class ComplytFileEndpointsIT extends TestContainersInitializerIT implements ComplytFileEndpointsITTemplate {

    @Test
    @Override
    public void saveOneFile_Exists_Returns201() {
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.COMPLYT_FILES_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                })
                .bodyValue(TestUtilities.complytFileSaveFileExample().build())
                .exchange()
                .expectStatus().isCreated();
    }
}
