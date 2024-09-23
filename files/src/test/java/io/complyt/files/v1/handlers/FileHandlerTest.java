package io.complyt.files.v1.handlers;

import io.complyt.files.domain.ComplytFile;
import io.complyt.files.domain.ComplytFileMetadata;
import io.complyt.files.facade.ComplytFileFacade;
import io.complyt.files.services.FileService;
import io.complyt.files.v1.mappers.ComplytFileMapper;
import io.complyt.files.v1.models.ComplytFileDto;
import io.complyt.files.v1.models.ComplytFileMetadataDto;
import io.complyt.files.v1.routers.FileRouter;
import io.complyt.files.v1.validators.ValidationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import testUtils.TestUtilities;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

class FileHandlerTest {

    private FileHandler fileHandler;
    private FileService fileService;
    private ComplytFileFacade complytFileFacade;
    private ValidationHandler<ComplytFileDto, SpringValidatorAdapter> complytFileDtoValidationHandler;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        fileService = Mockito.mock(FileService.class);
        complytFileFacade = Mockito.mock(ComplytFileFacade.class);
        complytFileDtoValidationHandler = Mockito.mock(ValidationHandler.class);
        fileHandler = new FileHandler(fileService, complytFileFacade, complytFileDtoValidationHandler);

        RouterFunction<ServerResponse> routerFunction = route(GET(FileRouter.BASE_URL), fileHandler::get)
                .andRoute(GET(FileRouter.COMPLYT_FILE_BASE_URL), fileHandler::getListOfFileInTenant)
                .andRoute(POST(FileRouter.COMPLYT_FILE_BASE_URL), fileHandler::saveFile)
                .andRoute(GET(FileRouter.COMPLYT_FILE_BASE_URL + "/{complytId}"), fileHandler::getFileWithSignedLink)
                .andRoute(DELETE(FileRouter.COMPLYT_FILE_BASE_URL + "/{complytId}"), fileHandler::markAsDeleted);

        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    void testGetListOfFileInTenant() {
        ComplytFileMetadata complytFileMetadata = TestUtilities.createComplytFileMetadata();
        when(complytFileFacade.findAllFilesInTenant(false, "active")).thenReturn(Flux.just(complytFileMetadata));

        webTestClient.get().uri(FileRouter.COMPLYT_FILE_BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ComplytFileMetadata.class);
    }

    @Test
    void testGetListOfFileInTenantWithSignedLink() {
        ComplytFileMetadata complytFileMetadata = TestUtilities.createComplytFileMetadata();
        when(complytFileFacade.findAllFilesInTenant(true, "active")).thenReturn(Flux.just(complytFileMetadata));

        webTestClient.get().uri(FileRouter.COMPLYT_FILE_BASE_URL + "?signed_link=true")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ComplytFileMetadata.class);
    }

    @Test
    void testGetListOfFileInTenantWithStatus() {
        ComplytFileMetadata complytFileMetadata = TestUtilities.createComplytFileMetadata().withMetadata(Map.of("status", "deleted"));
        when(complytFileFacade.findAllFilesInTenant(false, "deleted")).thenReturn(Flux.just(complytFileMetadata));

        webTestClient.get().uri(FileRouter.COMPLYT_FILE_BASE_URL + "?status=deleted")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ComplytFileMetadata.class);
    }

    @Test
    void testGetListOfFileInTenantEmpty() {
        when(complytFileFacade.findAllFilesInTenant(false, "active")).thenReturn(Flux.empty());

        webTestClient.get().uri(FileRouter.COMPLYT_FILE_BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ComplytFileMetadata.class);
    }

    @Test
    void testSaveFile() {
        // Arrange
        ComplytFile complytFile = TestUtilities.createComplytFile(); // Add any necessary fields to the DTO
        ComplytFileMetadata complytFileMetadata = complytFile.getMetadata().withUpdateTime(null).withCreateTime(null); // Add necessary fields

        when(complytFileDtoValidationHandler.handle(any()))
                .thenReturn(Mono.just(ComplytFileMapper.INSTANCE.complytFileToComplytFileDto(complytFile)));
        when(complytFileFacade.saveFile(any()))
                .thenReturn(Mono.just(complytFileMetadata));

        // Act & Assert
        webTestClient.post().uri(FileRouter.COMPLYT_FILE_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(complytFileMetadata) // Assuming the request contains the DTO
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location(URI.create(FileRouter.COMPLYT_FILE_BASE_URL).toString())
                .expectBody(ComplytFileMetadata.class)
                .isEqualTo(complytFileMetadata.withUpdateTime(null).withCreateTime(null));
    }


    @Test
    void testGetFileWithSignedLink() {
        UUID complytId = UUID.randomUUID();
        ComplytFileMetadata complytFileMetadata = TestUtilities.createComplytFileMetadata();
        ComplytFileMetadataDto complytFileMetadataDto = ComplytFileMapper.INSTANCE.complytFileMetadataToComplytFileMetadataDto(complytFileMetadata);
        when(complytFileFacade.getSignedLinkForFile(complytId)).thenReturn(Mono.just(complytFileMetadata.withUpdateTime(null).withCreateTime(null)));

        webTestClient.get().uri(FileRouter.COMPLYT_FILE_BASE_URL + "/" + complytId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ComplytFileMetadataDto.class)
                .isEqualTo(complytFileMetadataDto.withCreateTime(null).withUpdateTime(null));
    }

    @Test
    void testMarkAsDeleted() {
        UUID complytId = UUID.randomUUID();
        ComplytFileMetadata complytFileMetadata = TestUtilities.createComplytFileMetadata();
        ComplytFileMetadataDto complytFileMetadataDto = ComplytFileMapper.INSTANCE.complytFileMetadataToComplytFileMetadataDto(complytFileMetadata);
        when(complytFileFacade.markAsDeleted(complytId)).thenReturn(Mono.just(complytFileMetadata.withUpdateTime(null).withCreateTime(null)));

        webTestClient.delete().uri(FileRouter.COMPLYT_FILE_BASE_URL + "/" + complytId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ComplytFileMetadataDto.class)
                .isEqualTo(complytFileMetadataDto.withUpdateTime(null).withCreateTime(null));
    }
}