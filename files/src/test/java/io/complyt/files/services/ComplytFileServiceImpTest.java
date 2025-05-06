package io.complyt.files.services;

import io.complyt.files.business.storage.StorageWrapper;
import io.complyt.files.domain.ComplytFile;
import io.complyt.files.domain.ComplytFileMetadata;
import io.complyt.files.security.TenantResolver;
import io.complyt.files.v1.exceptions.types.ComplytApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.BaseTestClass;
import testUtils.TestUtilities;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ComplytFileServiceImpTest extends BaseTestClass {

    @InjectMocks
    ComplytFileServiceImpl complytFileService;

    @Mock
    StorageWrapper storageWrapper;

    @Mock
    TenantResolver tenantResolver;

    ComplytFile complytFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        complytFile = TestUtilities.createComplytFile();
    }

    @Test
    void getAllFilesByTenant_AllFilesRetrieved_ReturnsAllFilesFound() {
        // Given

        ComplytFile secondComplytFile = complytFile.withMetadata(TestUtilities.createComplytFileMetadata());

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(TestUtilities.tenantId));
        when(storageWrapper.listFilesInTenant(TestUtilities.tenantId, "active"))
                .thenReturn(Flux.just(complytFile.getMetadata(), secondComplytFile.getMetadata()));

        Flux<ComplytFileMetadata> complytFileMetadataFlux = complytFileService.getAllFilesByTenant("active");
        // Then
        StepVerifier.create(complytFileMetadataFlux).expectNext(complytFile.getMetadata(), secondComplytFile.getMetadata()).verifyComplete();
    }

    @Test
    void getAllFilesWithLinkByTenant_AllFilesRetrieved_ReturnsAllFilesFound() {
        // Given

        ComplytFile secondComplytFile = complytFile.withMetadata(TestUtilities.createComplytFileMetadata());

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(TestUtilities.tenantId));
        when(storageWrapper.listFilesWithLinkInTenant(TestUtilities.tenantId, "active"))
                .thenReturn(Flux.just(complytFile.getMetadata(), secondComplytFile.getMetadata()));

        Flux<ComplytFileMetadata> complytFileMetadataFlux = complytFileService.getAllFilesWithLinkByTenant("active");
        // Then
        StepVerifier.create(complytFileMetadataFlux).expectNext(complytFile.getMetadata(), secondComplytFile.getMetadata()).verifyComplete();
    }

    @Test
    void saveFile_FileSaved_FileReturned() throws IOException {
        // Given

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(TestUtilities.tenantId));
        when(storageWrapper.saveFile(complytFile)).thenReturn(Mono.just(complytFile.getMetadata()));
        Mono<ComplytFileMetadata> complytFileMetadataMono = complytFileService.saveFile(complytFile);

        // Then
        StepVerifier.create(complytFileMetadataMono).expectNext(complytFile.getMetadata()).verifyComplete();
    }

    @Test
    void saveFile_shouldThrowComplytApiExeptionOnIOException() throws IOException {
        // Given

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(TestUtilities.tenantId));
        when(storageWrapper.saveFile(complytFile)).thenThrow(new IOException("Error"));
        Mono<ComplytFileMetadata> complytFileMetadataMono = complytFileService.saveFile(complytFile);

        // Then
        StepVerifier.create(complytFileMetadataMono).expectError(ComplytApiException.class).verify();
    }

    @Test
    void getSignedLinkForFile_LinkCreated_LinkReturned() {
        // Given
        String storageLink = "https://greatest.storage.com/YOUR-TOKEN";
        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(TestUtilities.tenantId));
        when(storageWrapper.getSignedLinkForFile(complytFile.getMetadata().complytId(), TestUtilities.tenantId)).thenReturn(Mono.just(complytFile.getMetadata().withLink(storageLink)));
        Mono<ComplytFileMetadata> complytFileMetadataMono = complytFileService.getSignedLinkForFile(complytFile.getMetadata().complytId());

        // Then
        StepVerifier.create(complytFileMetadataMono).expectNext(complytFile.getMetadata().withLink(storageLink)).verifyComplete();
    }

    @Test
    void markAsDeleted_FileDeleted_ReturnFileWithStatusDeleted() {
        // Given
        Map<String, String> metadataDeleted = new HashMap<>(Map.of("type", "document", "display_name", "test.pdf", "status", "deleted"));
        ComplytFileMetadata complytFileMetadataDeleted = complytFile.getMetadata().withMetadata(metadataDeleted);
        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(TestUtilities.tenantId));
        when(storageWrapper.markAsDeleted(complytFile.getMetadata().complytId(), TestUtilities.tenantId)).thenReturn(Mono.just(complytFileMetadataDeleted));
        Mono<ComplytFileMetadata> complytFileMetadataMono = complytFileService.markAsDeleted(complytFile.getMetadata().complytId());

        // Then
        StepVerifier.create(complytFileMetadataMono).expectNext(complytFileMetadataDeleted).verifyComplete();

    }


}
