package io.complyt.files.v1.routers;

import io.complyt.files.config.ApiExceptionConfig;
import io.complyt.files.config.SecurityConfig;
import io.complyt.files.domain.File;
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
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {FileRouter.class, FileHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalErrorAttributes.class,
        GlobalExceptionHandler.class,
        SecurityConfig.class})
@WebFluxTest
public class FileRouterTest implements FileRouterTestTemplate {
    @MockBean
    FileService fileService;
    @Autowired
    private ApplicationContext context;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    @Override
    public void get_NullHandler_ThrowsNullPointerException() {
        // Given
        FileRouter fileRouter = new FileRouter();
        FileHandler nullFileHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> fileRouter.getfileLinkRouterFunction(nullFileHandler));

        // Then
        assertEquals("fileHandler is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    @Override
    @WithMockUser
    public void get_Exists_Returns200() {
        // Given
        File file = TestUtilities.createFile();
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
    @Override
    @WithMockUser
    public void getAny_InvalidUrl_Returns404() {
        // Given
        File file = TestUtilities.createFile();

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
    @Override
    @WithMockUser
    public void get_DoesntExist_Returns404() {
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
    @Override
    public void get_UnauthenticatedUser_Returns401() {
        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(FileRouter.BASE_URL).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Override
    public void get_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Test
    @Override
    @WithMockUser
    public void get_InternalServerError_Returns500() {
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