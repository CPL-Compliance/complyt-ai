package io.complyt.filing.repositories;

import io.complyt.filing.domain.Link;
import io.complyt.filing.security.TenantResolver;
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
class LinkRepositoryTest {
    @InjectMocks
    LinkRepository linkRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    @Mock
    TenantResolver tenantResolver;

    Link link;

    String tenantId;

    @BeforeEach
    private void setUp(){
        MockitoAnnotations.openMocks(this);
        tenantId = UUID.randomUUID().toString();
        link = createLink();
    }

    private Link createLink() {
        return new Link(ObjectId.get().toString(), UUID.randomUUID().toString(), "http://complyt.io");
    }

    @Test
    void find_tenantIdExistsInCollection_ReturnsLink() {
        // Given
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, Link.class)).thenReturn(Mono.just(link));

        // Then
        Mono<Link> linkMono = linkRepository.find();
        StepVerifier.create(linkMono).expectNext(link).verifyComplete();
    }

    @Test
    void find_tenantIdNotExistsInCollection_ReturnsEmpty() {
        // Given
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, Link.class)).thenReturn(Mono.empty());

        // Then
        Mono<Link> linkMono = linkRepository.find();
        StepVerifier.create(linkMono).verifyComplete();
    }
}