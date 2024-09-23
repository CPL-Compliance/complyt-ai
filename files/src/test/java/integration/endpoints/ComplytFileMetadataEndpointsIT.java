package integration.endpoints;

import integration.TestContainersInitializerIT;
import io.complyt.files.FilesApplication;
import io.complyt.files.domain.ComplytFileMetadata;
import io.complyt.files.v1.routers.FileRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import testUtils.TestUtilities;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@SpringBootTest(classes = FilesApplication.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
public class ComplytFileMetadataEndpointsIT extends TestContainersInitializerIT implements ComplytFileMetadataEndpointsITTemplate {

    private final SecurityMockServerConfigurers.JwtMutator differentTenantMutator = mockJwt().jwt(TestUtilities.stubJwt().claim("tenant_id", "other_it_tenant").build());
    private final SecurityMockServerConfigurers.JwtMutator defaultTenantMutator = mockJwt().jwt(TestUtilities.stubJwt().build());
    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
    }

    @Override
    @Order(1)
    @Test
    @WithMockUser
    public void deleteByComplytId_ValidMetdata_Returns200() {
        ComplytFileMetadata complytFileMetadata = new ComplytFileMetadata(
                UUID.fromString("fff38e2b-1fdd-4b43-ac7d-0058ecde600b"),
                Map.of("status", "deleted", "display_name", "test.it"),
                "it_tenant",
                null,
                null,
                "/v1/complyt_files/fff38e2b-1fdd-4b43-ac7d-0058ecde600b");
        webTestClient
                .mutateWith(defaultTenantMutator)
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(FileRouter.COMPLYT_FILE_BASE_URL + "/" + complytFileMetadata.complytId())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ComplytFileMetadata.class)
                .value(complytFileMetadataOriginal -> assertEquals(complytFileMetadata, complytFileMetadataOriginal));
    }

    @Override
    @Order(2)
    @Test
    public void deleteByComplytId_InvalidMetadata_Returns404() {
        ComplytFileMetadata complytFileMetadata = new ComplytFileMetadata(
                UUID.randomUUID(),
                Map.of("status", "deleted", "display_name", "test.it"),
                "another_it_tenant",
                null,
                null,
                "https://test.it");
        webTestClient
                .mutateWith(defaultTenantMutator)
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(FileRouter.COMPLYT_FILE_BASE_URL + "/" + complytFileMetadata.complytId())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Override
    @Order(3)
    @Test
    public void getAll_Exists_Returns200() {
        ComplytFileMetadata complytFileMetadata = new ComplytFileMetadata(
                UUID.fromString("fff38e2b-1fdd-4b43-ac7d-0058ecde600b"),
                Map.of("status", "active", "display_name", "test.it"),
                "it_tenant",
                null,
                null,
                "/v1/complyt_files/fff38e2b-1fdd-4b43-ac7d-0058ecde600b");
        webTestClient
                .mutateWith(defaultTenantMutator)
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(FileRouter.COMPLYT_FILE_BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ComplytFileMetadata.class)
                .contains(complytFileMetadata);
    }

    @Override
    @Order(4)
    @Test
    public void getByAll_DoesntExists_Returns200EmptyList() {
        ComplytFileMetadata complytFileMetadata = new ComplytFileMetadata(
                UUID.fromString("fff38e2b-1fdd-4b43-ac7d-0058ecde600b"),
                Map.of("status", "active", "display_name", "test.it"),
                "it_tenant",
                null,
                null,
                "/v1/complyt_files/fff38e2b-1fdd-4b43-ac7d-0058ecde600b");
        webTestClient
                .mutateWith(differentTenantMutator)
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(FileRouter.COMPLYT_FILE_BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ComplytFileMetadata.class)
                .value(list -> assertEquals(list.size(), 0));
        ;
    }

    @Override
    @Order(5)
    @Test
    public void getByComplytId_Exists_Returns200() {
        ComplytFileMetadata complytFileMetadata = new ComplytFileMetadata(
                UUID.fromString("fff38e2b-1fdd-4b43-ac7d-0058ecde600b"),
                Map.of("status", "active", "display_name", "test.it"),
                "it_tenant",
                null,
                null,
                "https://storage.test.it");
        webTestClient
                .mutateWith(defaultTenantMutator)
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(FileRouter.COMPLYT_FILE_BASE_URL + "/" + complytFileMetadata.complytId())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ComplytFileMetadata.class)
                .value(complytFileMetadataOriginal -> assertEquals(complytFileMetadata, complytFileMetadataOriginal));
    }

    @Override
    @Order(6)
    @Test
    public void getByComplytId_DoesntExists_Returns404() {
        ComplytFileMetadata complytFileMetadata = new ComplytFileMetadata(
                UUID.randomUUID(),
                Map.of("status", "deleted", "display_name", "test.it"),
                "another_it_tenant",
                null,
                null,
                "https://storage.test.it");
        webTestClient
                .mutateWith(defaultTenantMutator)
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(FileRouter.COMPLYT_FILE_BASE_URL + "/" + complytFileMetadata.complytId())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
