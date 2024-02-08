package com.complyt.repositories;

import com.complyt.domain.ClientTracking;
import com.complyt.domain.Nexus;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.TimestampsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class ClientTrackingRepositoryTest {

    @InjectMocks
    ClientTrackingRepository clientTrackingRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    @Mock
    TenantResolver tenantResolver;

    ClientTracking clientTracking;

    String tenantId;

    @BeforeEach
    void setUp() {
        clientTracking = createClientTracking();
        tenantId = UUID.randomUUID().toString();
    }

    private ClientTracking createClientTracking() {
        return new ClientTracking(tenantId, UUID.randomUUID().toString(), new Nexus(null), "name",null);
    }

    @Test
    void findClient_FindsClient_ReturnsClient() {
        // Given
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, ClientTracking.class)).thenReturn(Mono.just(clientTracking));

        // Then
        Mono<ClientTracking> actualClientTracking = clientTrackingRepository.findClient();

        StepVerifier.create(actualClientTracking).expectNext(clientTracking).verifyComplete();
    }

    @Test
    void save_SavesClient_ReturnsClient() {
        // Given
        ClientTracking clientTrackingNoId = clientTracking.withId(null).withTenantId(tenantId);

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.save(clientTrackingNoId)).thenReturn(Mono.just(clientTracking));

        // Then
        Mono<ClientTracking> actualClientTracking = clientTrackingRepository.save(clientTrackingNoId);

        StepVerifier.create(actualClientTracking).expectNext(clientTracking).verifyComplete();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void save_NullClientTrackingPassed_ThrowsException() {
        // Given
        ClientTracking nullClientTrackingNoId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> clientTrackingRepository.save(nullClientTrackingNoId));

        // Then
        assertEquals(nullPointerException.getMessage(), "clientTracking is marked non-null but is null");
    }

    @Test
    void findById_FindsClient_ReturnsClient() {
        // Given
        String id = clientTracking.getId();
        Query query = Query.query(Criteria.where("_id").is(id)
                .and("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, ClientTracking.class)).thenReturn(Mono.just(clientTracking));

        // Then
        Mono<ClientTracking> actualClientTracking = clientTrackingRepository.findById(id);

        StepVerifier.create(actualClientTracking).expectNext(clientTracking).verifyComplete();
    }

    @Test
    void findAll_FindsTwoClient_ReturnsTwoClients() {
        // Given
        List<ClientTracking> clientTrackingList = new ArrayList<>() {{
            add(clientTracking);
        }};
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.find(query, ClientTracking.class)).thenReturn(Flux.fromIterable(clientTrackingList));

        // Then
//        Flux<ClientTracking> clientTrackingFlux = clientTrackingRepository.findAll();

//        StepVerifier.create(clientTrackingFlux).expectNext(clientTracking).verifyComplete();
    }

    static TimestampsDto createTimestampsDto() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return new TimestampsDto(localDateTime.minusYears(1).toString(), localDateTime.toString());
    }
}