package com.complyt.repositories.security;

import com.complyt.domain.security.Authority;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.when;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class AuthorityRepositoryTest {

    @InjectMocks
    AuthorityRepository authorityRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    Authority authority;
    Authority anotherAuthority;

    @BeforeEach
    void setUp() {
        authority = new Authority(new ObjectId().toString(),"permission");
        anotherAuthority = new Authority(new ObjectId().toString(),"another permission");
    }

    @Test
    void findById_FindsAuthority_ReturnsAuthority() {
        // Given
        ObjectId objectId = new ObjectId(authority.getId());

        // When
        when(reactiveMongoTemplate.findById(objectId,Authority.class)).thenReturn(Mono.just(authority));
        Mono<Authority> authorityMono = authorityRepository.findById(objectId);

        // Then
        StepVerifier.create(authorityMono).expectNext(authority).verifyComplete();
    }

    @Test
    void find_FindsByObjectIds_ReturnsAuthorities() {
        // Given
        Collection<ObjectId> objectIds = new ArrayList<ObjectId>() {{
           add(new ObjectId(authority.getId()));
           add(new ObjectId(anotherAuthority.getId()));
        }};

        Collection<Authority> authorities = new ArrayList<Authority>() {{
            add(authority);
            add(anotherAuthority);
        }};

        Query query = Query.query(Criteria.where("_id").in(objectIds));

        // When
        when(reactiveMongoTemplate.find(query,Authority.class)).thenReturn(Flux.fromIterable(authorities));
        Flux<Authority> authorityFlux = authorityRepository.find(objectIds);

        // Then
        StepVerifier.create(authorityFlux).expectNext(authority,anotherAuthority).verifyComplete();
    }

}