package io.complyt.files.v1.routers;

import io.complyt.files.config.ApiExceptionConfig;
import io.complyt.files.config.SecurityConfig;
import io.complyt.files.domain.ComplytFile;
import io.complyt.files.domain.ComplytFileMetadata;
import io.complyt.files.domain.File;
import io.complyt.files.facade.ComplytFileFacade;
import io.complyt.files.services.FileService;
import io.complyt.files.v1.exceptions.GlobalErrorAttributes;
import io.complyt.files.v1.exceptions.GlobalExceptionHandler;
import io.complyt.files.v1.handlers.FileHandler;
import io.complyt.files.v1.mappers.FileMapper;
import io.complyt.files.v1.models.FileDto;
import io.complyt.files.v1.validators.ValidatorConfig;
import io.complyt.files.v1.validators.query_params.QueryParamsExtractorFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import testUtils.TestUtilities;
import testUtils.annotations.WithMockJwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {FileRouter.class, FileHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalErrorAttributes.class,
        GlobalExceptionHandler.class,
        SecurityConfig.class,
        QueryParamsExtractorFile.class})
@ExtendWith(MockitoExtension.class)
@WebFluxTest
public class FileRouterTest implements FileRouterTestTemplate {
    @MockBean
    FileService fileService;

    @MockBean
    ComplytFileFacade complytFileFacade;


    @Autowired
    private ApplicationContext context;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
//        webTestClient = WebTestClient.bindToApplicationContext(context).build();
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
    @WithMockJwt
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
    @WithMockJwt
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
    @WithMockJwt
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
    @WithMockJwt
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

    @Test
    @WithMockJwt
    public void getAllFiles_Exists_Returns200() {
        // Given
        ComplytFile complytFile = TestUtilities.createComplytFile();
        ComplytFileMetadata complytFileMetadata = complytFile.getMetadata();
        // When
        when(complytFileFacade.findAllFilesInTenant(false, "active"))
                .thenReturn(Flux.just(complytFileMetadata));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(FileRouter.COMPLYT_FILE_BASE_URL).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .equals(complytFileMetadata);
    }

    @Test
    @WithMockJwt
    public void getSignedLink_Exists_Returns200() {
        // Given
        ComplytFile complytFile = TestUtilities.createComplytFile();
        ComplytFileMetadata complytFileMetadata = complytFile.getMetadata();

        // When
        when(complytFileFacade.getSignedLinkForFile(complytFile.getMetadata().complytId()))
                .thenReturn(Mono.just(complytFileMetadata.withLink("https://google.storage.com")));

        // Then
    }

    @Test
//    @Override
    public void saveFile_NullHandler_ThrowsNullPointerException() {
        // Given
        FileRouter fileRouter = new FileRouter();
        FileHandler nullFileHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> fileRouter.saveFile(nullFileHandler));

        // Then
        assertEquals("fileHandler is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
//    @Override
    public void getListOfFilesInTenant_NullHandler_ThrowsNullPointerException() {
        // Given
        FileRouter fileRouter = new FileRouter();
        FileHandler nullFileHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> fileRouter.getListOfFilesInTenant(nullFileHandler));

        // Then
        assertEquals("fileHandler is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
//    @Override
    public void getFileWithSignedLink_NullHandler_ThrowsNullPointerException() {
        // Given
        FileRouter fileRouter = new FileRouter();
        FileHandler nullFileHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> fileRouter.getFileWithSignedLink(nullFileHandler));

        // Then
        assertEquals("fileHandler is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
//    @Override
    public void markAsDeletedFile_NullHandler_ThrowsNullPointerException() {
        // Given
        FileRouter fileRouter = new FileRouter();
        FileHandler nullFileHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> fileRouter.markAsDeletedFile(nullFileHandler));

        // Then
        assertEquals("fileHandler is marked non-null but is null", nullPointerException.getMessage());
    }
}