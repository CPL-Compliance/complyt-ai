package io.complyt.files.facade;

import io.complyt.files.domain.ComplytFile;
import io.complyt.files.domain.ComplytFileMetadata;
import io.complyt.files.services.ComplytFileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ComplytFileFacadeTest {

    @Mock
    private ComplytFileServiceImpl complytFileService;

    @InjectMocks
    private ComplytFileFacade complytFileFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllFilesInTenant_withSignedLink_Success() {
        ComplytFileMetadata metadata1 = TestUtilities.createComplytFileMetadata();
        ComplytFileMetadata metadata2 = TestUtilities.createComplytFileMetadata();

        when(complytFileService.getAllFilesWithLinkByTenant(any()))
                .thenReturn(Flux.just(metadata1, metadata2));

        StepVerifier.create(complytFileFacade.findAllFilesInTenant(true, "active"))
                .expectNext(metadata1)
                .expectNext(metadata2)
                .verifyComplete();
    }

    @Test
    void findAllFilesInTenant_withoutSignedLink_Success() {
        ComplytFileMetadata metadata1 = TestUtilities.createComplytFileMetadata();
        ComplytFileMetadata metadata2 = TestUtilities.createComplytFileMetadata();

        when(complytFileService.getAllFilesByTenant(any()))
                .thenReturn(Flux.just(metadata1, metadata2));

        StepVerifier.create(complytFileFacade.findAllFilesInTenant(false, "active"))
                .expectNext(metadata1)
                .expectNext(metadata2)
                .verifyComplete();
    }

    @Test
    void saveFile_ValidFile_Success() {
        ComplytFile complytFile = TestUtilities.createComplytFile();
        ComplytFileMetadata metadata = TestUtilities.createComplytFileMetadata();

        when(complytFileService.saveFile(any(ComplytFile.class)))
                .thenReturn(Mono.just(metadata));

        StepVerifier.create(complytFileFacade.saveFile(complytFile))
                .expectNext(metadata)
                .verifyComplete();
    }

    @Test
    void getSignedLinkForFile_ValidUUID_Success() {
        UUID complytId = UUID.randomUUID();
        ComplytFileMetadata metadata = TestUtilities.createComplytFileMetadata();

        when(complytFileService.getSignedLinkForFile(eq(complytId)))
                .thenReturn(Mono.just(metadata));

        StepVerifier.create(complytFileFacade.getSignedLinkForFile(complytId))
                .expectNext(metadata)
                .verifyComplete();
    }

    @Test
    void markAsDeleted_ValidUUID_Success() {
        UUID complytId = UUID.randomUUID();
        ComplytFileMetadata metadata = TestUtilities.createComplytFileMetadata();

        when(complytFileService.markAsDeleted(eq(complytId)))
                .thenReturn(Mono.just(metadata));

        StepVerifier.create(complytFileFacade.markAsDeleted(complytId))
                .expectNext(metadata)
                .verifyComplete();
    }
}