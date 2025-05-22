package integration.endpoints;

import integration.TestContainersInitializerIT;
import io.complyt.files.FilesApplication;
import io.complyt.files.domain.ComplytFileMetadata;
import io.complyt.files.security.TenantResolver;
import io.complyt.files.v1.routers.FileRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import testUtils.annotations.WithMockJwt;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FilesApplication.class)
@AutoConfigureWebTestClient
public class ComplytFileEndpointsIT extends TestContainersInitializerIT implements ComplytFileEndpointsITTemplate {
    
    @Autowired
    private WebTestClient webTestClient;

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void saveOneFile_Exists_Returns201() {
        ComplytFileMetadata complytFileMetadataToSave = new ComplytFileMetadata(
                UUID.fromString("fff38e2b-1fdd-4b43-ac7d-0058ecde600b"),
                Map.of("status", "active", "display_name", "test.it"),
                "it_tenant",
                null,
                null,
                "/v1/complyt_files/fff38e2b-1fdd-4b43-ac7d-0058ecde600b");

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ClassPathResource("application.yml")) // Add a file part
                .header("Content-Disposition", "form-data; name=file; filename=test.it");


        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(FileRouter.COMPLYT_FILE_BASE_URL)
                        .build())
                .accept(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(ComplytFileMetadata.class)
                .value(complytFileMetadata -> assertEquals(complytFileMetadataToSave, complytFileMetadata));
    }
}
