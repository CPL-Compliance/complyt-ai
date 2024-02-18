package com.complyt.repositories;

import com.complyt.domain.ClientTracking;
import com.complyt.domain.Nexus;
import com.complyt.repositories.Constants.RepositoryConstant;
import com.complyt.security.TenantResolver;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

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
    String name;
    String tenantId;

    @BeforeEach
    void setUp() {
        name = "RAZ";
        clientTracking = createClientTracking().withName(name);
        tenantId = "org_12345";
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
        String id = UUID.randomUUID().toString();
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
        int page = RepositoryConstant.DEFAULT_PAGE_NUM;
        int size = RepositoryConstant.DEFAULT_PAGE_SIZE;
        int calculatedOffset = (page - 1) * size;

        Query query = new Query()
                .limit(size)
                .skip(calculatedOffset);
        // When
        when(reactiveMongoTemplate.find(query, ClientTracking.class)).thenReturn(Flux.fromIterable(clientTrackingList));

        // Then
        Flux<ClientTracking> clientTrackingFlux = clientTrackingRepository.findAll(page, size);

        StepVerifier.create(clientTrackingFlux).expectNext(clientTracking).verifyComplete();
    }

    @Test
    void findByName_FindsClient_ReturnsClient() {
        // Given
        List<ClientTracking> clientTrackingList = new ArrayList<>() {{
            add(clientTracking);
        }};
        Query query = new Query(Criteria.where("name").regex("^" + Pattern.quote(name) + "$", "i"));

        // When
        when(reactiveMongoTemplate.find(query, ClientTracking.class)).thenReturn(Flux.fromIterable(clientTrackingList));

        // Then
        Flux<ClientTracking> clientTrackingFlux = clientTrackingRepository.findByName(name);

        StepVerifier.create(clientTrackingFlux).expectNext(clientTracking).verifyComplete();
    }

    @Test
    void findByTenantId_FindsClient_ReturnsClient() {
        // Given
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));

        // When
        when(reactiveMongoTemplate.findOne(query, ClientTracking.class)).thenReturn(Mono.just(clientTracking));

        // Then
        Mono<ClientTracking> clientTrackingFlux = clientTrackingRepository.findByTenantId(tenantId);

        StepVerifier.create(clientTrackingFlux).expectNext(clientTracking).verifyComplete();
    }

    @Test
    void saveByTenantId_saveClientById_ReturnsClient() {
        // Given
        ClientTracking clientTrackingNoId = clientTracking.withId(null).withTenantId(tenantId);

        // When
        when(reactiveMongoTemplate.save(clientTrackingNoId)).thenReturn(Mono.just(clientTracking));

        // Then
        Mono<ClientTracking> actualClientTracking = clientTrackingRepository.saveByTenantId(clientTrackingNoId, tenantId);

        StepVerifier.create(actualClientTracking).expectNext(clientTracking).verifyComplete();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void saveByTenantId_NullClientTrackingPassed_ThrowsException() {
        // Given
        ClientTracking nullClientTrackingNoId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> clientTrackingRepository.saveByTenantId(nullClientTrackingNoId, tenantId));

        // Then
        assertEquals(nullPointerException.getMessage(), "clientTracking is marked non-null but is null");
    }

    @Test
    void findById_NullId_ThrowsNullPointerException() {
        // Given
        String nullId = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingRepository.findById(nullId).block();
        });

        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

    @Test
    void findByName_NullName_ThrowsNullPointerException() {
        // Given
        String nullName = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingRepository.findByName(nullName).collectList().block();
        });

        assertEquals(nullPointerException.getMessage(), "name is marked non-null but is null");
    }

    @Test
    void findByTenantId_NullTenantId_ThrowsNullPointerException() {
        // Given
        String nullTenantId = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingRepository.findByTenantId(nullTenantId).block();
        });

        assertEquals(nullPointerException.getMessage(), "tenantId is marked non-null but is null");
    }
}