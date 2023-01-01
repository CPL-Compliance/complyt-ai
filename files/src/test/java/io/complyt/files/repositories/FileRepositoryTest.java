package io.complyt.files.repositories;

import io.complyt.files.domain.File;
import io.complyt.files.security.TenantResolver;
import org.bson.types.ObjectId;
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

import java.util.UUID;

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

    File file;

    String tenantId;

    @BeforeEach
    private void setUp(){
        MockitoAnnotations.openMocks(this);
        tenantId = UUID.randomUUID().toString();
        file = createLink();
    }

    private File createLink() {
        return new File(ObjectId.get().toString(), UUID.randomUUID().toString(), "http://complyt.io");
    }

    @Test
    void find_tenantIdExistsInCollection_ReturnsLink() {
        // Given
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, File.class)).thenReturn(Mono.just(file));

        // Then
        Mono<File> linkMono = fileRepository.find();
        StepVerifier.create(linkMono).expectNext(file).verifyComplete();
    }

    @Test
    void find_tenantIdNotExistsInCollection_ReturnsEmpty() {
        // Given
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, File.class)).thenReturn(Mono.empty());

        // Then
        Mono<File> linkMono = fileRepository.find();
        StepVerifier.create(linkMono).verifyComplete();
    }
}