package io.complyt.files.services;

import io.complyt.files.domain.ApiKey;
import io.complyt.files.repositories.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class FileServiceTest {
    @InjectMocks
    ApiKeyService apiKeyService;

    @Mock
    FileRepository fileRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void find_tenantIdExistsInCollection_ReturnsLink() {
        // Given
        ApiKey file = TestUtilities.createFile();

        // When
        when(fileRepository.find()).thenReturn(Mono.just(file));

        // Then
        Mono<ApiKey> linkMono = apiKeyService.find();
        StepVerifier.create(linkMono).expectNext(file).verifyComplete();
    }

    @Test
    void find_tenantIdNotExistsInCollection_ReturnsLink() {
        // When
        when(fileRepository.find()).thenReturn(Mono.empty());

        // Then
        Mono<ApiKey> linkMono = apiKeyService.find();
        StepVerifier.create(linkMono).verifyComplete();
    }
}