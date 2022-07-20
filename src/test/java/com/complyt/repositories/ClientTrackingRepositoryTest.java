package com.complyt.repositories;

import com.complyt.config.SecurityConfigMockTest;
import com.complyt.domain.ClientTracking;
import com.complyt.domain.Nexus;
import com.complyt.domain.security.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@Import(SecurityConfigMockTest.class)
public class ClientTrackingRepositoryTest {

    @InjectMocks
    ClientTrackingRepository clientTrackingRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    ClientTracking clientTracking;
    User user;

    @BeforeEach
    void setUp() {
        ObjectId clientId = new ObjectId("507f191e810c19729de860ea");
        user = User.builder().username("user").password("password").clientId(clientId).build();
        clientTracking = createClientTracking();
    }

    private ClientTracking createClientTracking() {
        return new ClientTracking(user.getClientId().toString(), new ObjectId(),
                new Nexus(false, null));
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findClient_FindsClient_ReturnsClient() {
        // Given
        Query query = Query.query(Criteria.where("clientId").is(user.getClientId()));

        // When
        when(reactiveMongoTemplate.findOne(query,ClientTracking.class)).thenReturn(Mono.just(clientTracking));
        Mono<ClientTracking> actualClientTracking = clientTrackingRepository.findClient();

        // Then
        StepVerifier.create(actualClientTracking).expectNext(clientTracking).verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void save_SavesClient_ReturnsClient() {
        // Given
        ClientTracking clientTrackingNoId = clientTracking.withId(null).withClientId(user.getClientId());

        // When
        when(reactiveMongoTemplate.save(clientTrackingNoId)).thenReturn(Mono.just(clientTracking));
        Mono<ClientTracking> actualClientTracking = clientTrackingRepository.save(clientTrackingNoId);

        // Then
        StepVerifier.create(actualClientTracking).expectNext(clientTracking).verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findById_FindsClient_ReturnsClient() {
        // Given
        String id = clientTracking.getId();
        Query query = Query.query(Criteria.where("_id").is(id)
                .and("clientId").is(user.getClientId()));

        // When
        when(reactiveMongoTemplate.findOne(query,ClientTracking.class)).thenReturn(Mono.just(clientTracking));
        Mono<ClientTracking> actualClientTracking = clientTrackingRepository.findById(id);

        // Then
        StepVerifier.create(actualClientTracking).expectNext(clientTracking).verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findAll_FindsTwoClient_ReturnsTwoClients() {
        // Given
        List<ClientTracking> clientTrackingList = new ArrayList<ClientTracking>(){{add(clientTracking);}};
        Query query = Query.query(Criteria.where("clientId").is(user.getClientId()));

        // When
        when(reactiveMongoTemplate.find(query,ClientTracking.class)).thenReturn(Flux.fromIterable(clientTrackingList));
        Flux<ClientTracking> clientTrackingFlux = clientTrackingRepository.findAll();

        // Then
        StepVerifier.create(clientTrackingFlux).expectNext(clientTracking).verifyComplete();
    }

}
