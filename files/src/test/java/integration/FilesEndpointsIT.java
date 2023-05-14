package integration;

import io.complyt.files.FilesApplication;
import io.complyt.files.v1.models.FileDto;
import io.complyt.files.v1.routers.FileRouter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = FilesApplication.class)
@AutoConfigureWebTestClient
public class FilesEndpointsIT extends TestContainersInitializerIT implements FilesEndpointsITTemplate {

    private final SecurityMockServerConfigurers.JwtMutator differentTenantMutator = mockJwt().jwt(TestUtilities.stubJwt().claim("tenant_id", "other_it_tenant").build());
    private final SecurityMockServerConfigurers.JwtMutator defaultTenantMutator = mockJwt().jwt(TestUtilities.stubJwt().build());

    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("files"));
    }

    @Test
    @Override
    @WithMockUser
    public void getFile_Exists_Returns200() {
        webTestClient
                .mutateWith(defaultTenantMutator)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(FileRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FileDto.class)
                .value(fileDto -> assertEquals(fileDto.link(), TestUtilities.linkStr));
    }

    @Test
    @Override
    @WithMockUser
    public void getFile_DoesntExists_Returns404() {
        webTestClient
                .mutateWith(differentTenantMutator)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(FileRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
