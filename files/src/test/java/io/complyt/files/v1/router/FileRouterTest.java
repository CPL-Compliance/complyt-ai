package io.complyt.files.v1.router;

import io.complyt.files.domain.File;
import io.complyt.files.services.FileService;
import io.complyt.files.v1.handler.FileHandler;
import io.complyt.files.v1.mappers.FileMapper;
import io.complyt.files.v1.model.FileDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {FileRouter.class, FileHandler.class})
@WebFluxTest
public class FileRouterTest {
    @Autowired
    private ApplicationContext context;

    @MockBean
    FileService fileService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    public void linkRoute_nullLinkHandler_ThrowsNullPointerException() {
        // Given
        FileRouter fileRouter = new FileRouter();
        FileHandler nullFileHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> fileRouter.fileRoute(nullFileHandler));

        // Then
        assertEquals("fileHandler is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_read:link")
    public void linkRoute_getFile_fileReturned() {
        // Given
        File file = new File(ObjectId.get().toString(), UUID.randomUUID().toString(), "http://localhost");
        FileDto fileDto = FileMapper.INSTANCE.fileToFileDto(file);

        // When
        when(fileService.find()).thenReturn(Mono.just(file));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(FileRouter.BASE_URL).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FileDto.class)
                .isEqualTo(fileDto);
    }

    @Test
    public void linkRoute_getFile_unauthorized() {
        // Given
        File file = new File(ObjectId.get().toString(), UUID.randomUUID().toString(), "http://localhost");
        FileDto fileDto = FileMapper.INSTANCE.fileToFileDto(file);

        // When
        when(fileService.find()).thenReturn(Mono.just(file));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(FileRouter.BASE_URL).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}