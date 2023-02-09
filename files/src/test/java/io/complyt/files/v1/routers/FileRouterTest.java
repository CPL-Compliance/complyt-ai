package io.complyt.files.v1.routers;

import io.complyt.files.config.ApiExceptionConfig;
import io.complyt.files.config.SecurityConfig;
import io.complyt.files.domain.File;
import io.complyt.files.security.AudienceValidator;
import io.complyt.files.services.FileService;
import io.complyt.files.v1.exceptions.GlobalErrorAttributes;
import io.complyt.files.v1.exceptions.GlobalExceptionHandler;
import io.complyt.files.v1.handlers.FileHandler;
import io.complyt.files.v1.mappers.FileMapper;
import io.complyt.files.v1.models.FileDto;
import io.complyt.files.v1.validators.ValidatorConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.MockServerConfigurer;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import testUtils.ObjectStub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {FileRouter.class, FileHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalErrorAttributes.class,
        GlobalExceptionHandler.class,
SecurityConfig.class})
@WebFluxTest
public class FileRouterTest {
    @Autowired
    private ApplicationContext context;

    @MockBean
    FileService fileService;

    private WebTestClient webTestClient;

    private ObjectStub objectStub;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
        objectStub = new ObjectStub();
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    public void linkRoute_nullLinkHandler_ThrowsNullPointerException() {
        // Given
        FileRouter fileRouter = new FileRouter();
        FileHandler nullFileHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> fileRouter.getfileLinkRouterFunction(nullFileHandler));

        // Then
        assertEquals("fileHandler is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    @WithMockUser()
    public void linkRoute_getFile_fileReturned() {
        // Given
        File file = objectStub.createFile();
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
    @WithMockUser
    public void linkRoute_pathDoesntExist_returnsNotFound404() {
        // Given
        File file = objectStub.createFile();
        FileDto fileDto = FileMapper.INSTANCE.fileToFileDto(file);

        // When
        when(fileService.find()).thenReturn(Mono.just(file));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(FileRouter.BASE_URL + "/resource_not_found").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser(authorities = "SCOPE_read:link")
    public void linkRoute_resourceDoesntExist_returnsNotFound404() {
        // When
        when(fileService.find()).thenReturn(Mono.empty());

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(FileRouter.BASE_URL).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void linkRoute_getFile_unauthorized() {
        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(FileRouter.BASE_URL).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(authorities = "SCOPE_read:link")
    public void linkRoute_getFile_internalError() {
        // When
        when(fileService.find()).thenThrow(RuntimeException.class);

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(FileRouter.BASE_URL).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}