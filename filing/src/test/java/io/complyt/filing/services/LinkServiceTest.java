package io.complyt.filing.services;

import io.complyt.filing.domain.Link;
import io.complyt.filing.repositories.LinkRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class LinkServiceTest {
    @InjectMocks
    LinkService linkService;

    @Mock
    LinkRepository linkRepository;

    @Test
    void find_tenantIdExistsInCollection_ReturnsLink() {
        // Given
        Link link = new Link(ObjectId.get().toString(), UUID.randomUUID().toString(), "http://localhost");

        // When
        when(linkRepository.find()).thenReturn(Mono.just(link));

        // Then
        Mono<Link> linkMono = linkService.find();
        StepVerifier.create(linkMono).expectNext(link).verifyComplete();
    }

    @Test
    void find_tenantIdNotExistsInCollection_ReturnsLink() {
        // When
        when(linkRepository.find()).thenReturn(Mono.empty());

        // Then
        Mono<Link> linkMono = linkService.find();
        StepVerifier.create(linkMono).verifyComplete();
    }
}