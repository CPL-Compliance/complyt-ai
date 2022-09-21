package com.complyt.services;

import com.complyt.domain.ClientTracking;
import com.complyt.domain.Nexus;
import com.complyt.repositories.ClientTrackingRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ClientTrackingServiceImplTest {

    @InjectMocks
    ClientTrackingServiceImpl clientTrackingService;

    @Mock
    ClientTrackingRepository clientTrackingRepository;

    private ClientTracking clientTracking;

    @BeforeEach
    void setUp() {
        Nexus nexus = new Nexus(null);
        clientTracking = new ClientTracking(UUID.randomUUID().toString(),new ObjectId(), nexus);
    }

    @Test
    void save_SavesClientTracking_ReturnsClientTracking() {
        // Given

        // When + Then
        when(clientTrackingRepository.save(clientTracking)).thenReturn(Mono.just(clientTracking));
        Mono<ClientTracking> clientTrackingMono = clientTrackingService.save(clientTracking);

        StepVerifier.create(clientTrackingMono).expectNext(clientTracking).verifyComplete();
    }

    @Test
    void findById_FindsClientTracking_ReturnsClientTracking() {
        // Given
        String id = clientTracking.getId();

        // When = Then
        when(clientTrackingRepository.findById(id)).thenReturn(Mono.just(clientTracking));
        Mono<ClientTracking> clientTrackingMono = clientTrackingService.findById(id);

        StepVerifier.create(clientTrackingMono).expectNext(clientTracking).verifyComplete();
    }

    @Test
    void findById_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingService.findById(nullId);
        });

        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }


    @Test
    void getNexusInfo_GetsNexusInfo_ReturnsNexusInfo() {
        // Given

        // When
        when(clientTrackingRepository.findClient()).thenReturn(Mono.just(clientTracking));
        Mono<Nexus> nexusMono = clientTrackingService.getNexusInfo();

        // Then
        StepVerifier.create(nexusMono).expectNext(clientTracking.getNexus()).verifyComplete();

    }

    @Test
    void findAll() {
        // Given
        List<ClientTracking> trackingList = new ArrayList<ClientTracking>(){{add(clientTracking);}};
        Flux<ClientTracking> trackingFlux = Flux.fromIterable(trackingList);

        // When
        when(clientTrackingRepository.findAll()).thenReturn(trackingFlux);
        Flux<ClientTracking> actualTrackingFlux = clientTrackingService.findAll();

        // Then
        StepVerifier.create(actualTrackingFlux).expectNext(clientTracking).verifyComplete();

    }
}