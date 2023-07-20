package io.complyt.files.repositories;

import io.complyt.files.domain.ApiKey;
import io.complyt.files.security.TenantResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class FileRepositoryTest {
    @InjectMocks
    FileRepository fileRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    @Mock
    TenantResolver tenantResolver;


    ApiKey file;

    String tenantId;

    @BeforeEach
    void setUp() {
        tenantId = TestUtilities.tenantId;
        MockitoAnnotations.openMocks(this);
        file = TestUtilities.createFile();
    }

    @Test
    void find_tenantIdExistsInCollection_ReturnsLink() {
        // Given
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, ApiKey.class)).thenReturn(Mono.just(file));

        // Then
        Mono<ApiKey> linkMono = fileRepository.find();
        StepVerifier.create(linkMono).expectNext(file).verifyComplete();
    }

    @Test
    void find_tenantIdNotExistsInCollection_ReturnsEmpty() {
        // Given
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, ApiKey.class)).thenReturn(Mono.empty());

        // Then
        Mono<ApiKey> linkMono = fileRepository.find();
        StepVerifier.create(linkMono).verifyComplete();
    }
}