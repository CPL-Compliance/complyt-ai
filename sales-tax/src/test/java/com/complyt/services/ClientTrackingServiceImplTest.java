package com.complyt.services;

import com.complyt.business.timestamps_injection.ExistingClientTrackingInternalTimestampsInjector;
import com.complyt.domain.ClientTracking;
import com.complyt.domain.Nexus;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.repositories.ClientTrackingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class clientTrackingServiceImpImplTest {

    @InjectMocks
    ClientTrackingServiceImpl clientTrackingServiceImp;

    @Mock
    ClientTrackingRepository clientTrackingRepository;

    private ClientTracking clientTracking;

    @BeforeEach
    void setUp() {
        Nexus nexus = new Nexus(null);
        Timestamps internalTimestamps = new Timestamps(LocalDateTime.now(), LocalDateTime.now());
        clientTracking = new ClientTracking(UUID.randomUUID().toString(), UUID.randomUUID().toString(), nexus, "name", internalTimestamps);
    }

    @Test
    void save_SavesClientTracking_ReturnsClientTracking() {
        // Given

        // When + Then
        when(clientTrackingRepository.save(clientTracking)).thenReturn(Mono.just(clientTracking));
        Mono<ClientTracking> clientTrackingMono = clientTrackingServiceImp.save(clientTracking);

        StepVerifier.create(clientTrackingMono).expectNext(clientTracking).verifyComplete();
    }

    @Test
    void findById_FindsClientTracking_ReturnsClientTracking() {
        // Given
        String id = clientTracking.getId();

        // When = Then
        when(clientTrackingRepository.findById(id)).thenReturn(Mono.just(clientTracking));
        Mono<ClientTracking> clientTrackingMono = clientTrackingServiceImp.findById(id);

        StepVerifier.create(clientTrackingMono).expectNext(clientTracking).verifyComplete();
    }

    @Test
    void findById_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingServiceImp.findById(nullId);
        });

        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }


    @Test
    void getNexusInfo_GetsNexusInfo_ReturnsNexusInfo() {
        // Given

        // When
        when(clientTrackingRepository.findClient()).thenReturn(Mono.just(clientTracking));
        Mono<Nexus> nexusMono = clientTrackingServiceImp.getNexusInfo();

        // Then
        StepVerifier.create(nexusMono).expectNext(clientTracking.getNexus()).verifyComplete();

    }

    @Test
    void saveByTenantId_SavesClientTracking_ReturnsClientTracking() {
        // Given
        String tenantId = "org_12345";
        // When
        when(clientTrackingRepository.saveByTenantId(clientTracking.withTenantId(tenantId), tenantId)).thenReturn(Mono.just(clientTracking));

        Mono<ClientTracking> clientTrackingMono = clientTrackingServiceImp.saveByTenantId(clientTracking, tenantId);

        // Then
        StepVerifier.create(clientTrackingMono).expectNext(clientTracking).verifyComplete();
    }

    @Test
    void injectDataToExistingClientTracking_InjectsData_ReturnsInjectedClientTracking() {
        // Given
        ClientTracking anotherClientTracking = clientTracking.withName("changedName");
        ExistingClientTrackingInternalTimestampsInjector injector = new ExistingClientTrackingInternalTimestampsInjector(clientTracking);
        ClientTracking clientTrackingWithUpdatedDates = injector.inject();

        // Then
        Mono<ClientTracking> clientTrackingMono = clientTrackingServiceImp.injectDataToExistingClientTracking(clientTracking, anotherClientTracking);

        StepVerifier.create(clientTrackingMono).expectNextMatches(clientTracking -> {
            LocalDateTime expectedCreatedDateTime = clientTrackingWithUpdatedDates.getInternalTimestamps().getCreatedDate();
            LocalDateTime expectedUpdatedDateTime = clientTrackingWithUpdatedDates.getInternalTimestamps().getUpdatedDate();

            LocalDateTime actualCreatedDateTime = clientTracking.getInternalTimestamps().getCreatedDate();
            LocalDateTime actualUpdatedDateTime = clientTracking.getInternalTimestamps().getUpdatedDate();

            return expectedUpdatedDateTime.getYear() == actualUpdatedDateTime.getYear() &&
                    expectedUpdatedDateTime.getMonthValue() == actualUpdatedDateTime.getMonthValue() &&
                    expectedUpdatedDateTime.getDayOfYear() == actualUpdatedDateTime.getDayOfYear() &&
                    expectedUpdatedDateTime.getHour() == actualUpdatedDateTime.getHour() &&
                    expectedCreatedDateTime.getYear() == actualCreatedDateTime.getYear() &&
                    expectedCreatedDateTime.getMonthValue() == actualCreatedDateTime.getMonthValue() &&
                    expectedCreatedDateTime.getDayOfYear() == actualCreatedDateTime.getDayOfYear() &&
                    expectedCreatedDateTime.getHour() == actualCreatedDateTime.getHour();

        }).verifyComplete();
    }

    @Test
    void injectDataToNewClientTracking_InjectsData_ReturnsInjectedClientTracking() {
        // Given
        ExistingClientTrackingInternalTimestampsInjector injector = new ExistingClientTrackingInternalTimestampsInjector(clientTracking);
        ClientTracking clientTrackingWithUpdatedDates = injector.inject();

        // When + Then
        Mono<ClientTracking> clientTrackingMono = clientTrackingServiceImp.injectDataToNewClientTracking(clientTracking);

        StepVerifier.create(clientTrackingMono).expectNextMatches(clientTracking -> {
            LocalDateTime expectedCreatedDateTime = clientTrackingWithUpdatedDates.getInternalTimestamps().getCreatedDate();
            LocalDateTime expectedUpdatedDateTime = clientTrackingWithUpdatedDates.getInternalTimestamps().getUpdatedDate();

            LocalDateTime actualCreatedDateTime = clientTracking.getInternalTimestamps().getCreatedDate();
            LocalDateTime actualUpdatedDateTime = clientTracking.getInternalTimestamps().getUpdatedDate();

            return expectedUpdatedDateTime.getYear() == actualUpdatedDateTime.getYear() &&
                    expectedUpdatedDateTime.getMonthValue() == actualUpdatedDateTime.getMonthValue() &&
                    expectedUpdatedDateTime.getDayOfYear() == actualUpdatedDateTime.getDayOfYear() &&
                    expectedUpdatedDateTime.getHour() == actualUpdatedDateTime.getHour() &&
                    expectedCreatedDateTime.getYear() == actualCreatedDateTime.getYear() &&
                    expectedCreatedDateTime.getMonthValue() == actualCreatedDateTime.getMonthValue() &&
                    expectedCreatedDateTime.getDayOfYear() == actualCreatedDateTime.getDayOfYear() &&
                    expectedCreatedDateTime.getHour() == actualCreatedDateTime.getHour();

        }).verifyComplete();
    }

    @Test
    void update_UpdatesClientTracking_ReturnsUpdatedClientTracking() {
        // Given
        ClientTracking newClientTracking = clientTracking.withName("changedName");
        LocalDateTime expectedTime = clientTracking.getInternalTimestamps().getUpdatedDate();
        // When + Then
        Mono<ClientTracking> updatedClientTracking = clientTrackingServiceImp.update(newClientTracking, clientTracking);
        LocalDateTime actualTime = updatedClientTracking.block().getInternalTimestamps().getUpdatedDate();

        Assertions.assertEquals(updatedClientTracking.block().getName(), newClientTracking.getName());
        Assertions.assertEquals(actualTime.getYear(), expectedTime.getYear());
        Assertions.assertEquals(actualTime.getMonthValue(), expectedTime.getMonthValue());
        Assertions.assertEquals(actualTime.getDayOfYear(), expectedTime.getDayOfYear());
        Assertions.assertEquals(actualTime.getHour(), expectedTime.getHour());
        Assertions.assertEquals(actualTime.getYear(), expectedTime.getYear());
        Assertions.assertEquals(actualTime.getMonthValue(), expectedTime.getMonthValue());
        Assertions.assertEquals(actualTime.getDayOfYear(), expectedTime.getDayOfYear());
        Assertions.assertEquals(actualTime.getHour(), expectedTime.getHour());

    }

    @Test
    void findAll_FindsAllClientTrackings_ReturnsClientTrackings() {
        // Given
        int page = 0;
        int size = 10;
        List<ClientTracking> clientTrackings = List.of(clientTracking);

        // When + Then
        when(clientTrackingRepository.findAll(page, size)).thenReturn(Flux.fromIterable(clientTrackings));
        Flux<ClientTracking> foundClientTrackings = clientTrackingServiceImp.findAll(page, size);

        StepVerifier.create(foundClientTrackings).expectNextCount(1).verifyComplete();
    }

    @Test
    void getByName_FindsClientTrackingsByName_ReturnsClientTrackings() {
        // Given
        String name = "testName";
        List<ClientTracking> clientTrackings = List.of(clientTracking);

        // When + Then
        when(clientTrackingRepository.findByName(name)).thenReturn(Flux.fromIterable(clientTrackings));
        Flux<ClientTracking> foundClientTrackings = clientTrackingServiceImp.getByName(name);

        StepVerifier.create(foundClientTrackings).expectNextCount(1).verifyComplete();
    }

    @Test
    void getByTenantId_FindsClientTrackingByTenantId_ReturnsClientTracking() {
        // Given
        String tenantId = "org_12345";

        // When + Then
        when(clientTrackingRepository.findByTenantId(tenantId)).thenReturn(Mono.just(clientTracking));
        Mono<ClientTracking> foundClientTracking = clientTrackingServiceImp.getByTenantId(tenantId);

        StepVerifier.create(foundClientTracking).expectNext(clientTracking).verifyComplete();
    }

    @Test
    void getByName_NullName_ThrowsNullPointerException() {
        // Given
        String nullName = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingServiceImp.getByName(nullName).blockLast();
        });

        assertEquals(nullPointerException.getMessage(), "name is marked non-null but is null");
    }

    @Test
    void getByTenantId_NullTenantId_ThrowsNullPointerException() {
        // Given
        String nullTenantId = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingServiceImp.getByTenantId(nullTenantId).block();
        });

        assertEquals(nullPointerException.getMessage(), "tenantId is marked non-null but is null");
    }

    @Test
    void saveByTenantId_NullClientTracking_ThrowsNullPointerException() {
        // Given
        ClientTracking nullClientTracking = null;
        String tenantId = "org_12345";

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingServiceImp.saveByTenantId(nullClientTracking, tenantId).block();
        });

        assertEquals(nullPointerException.getMessage(), "clientTracking is marked non-null but is null");
    }

    @Test
    void saveByTenantId_NullTenantId_ThrowsNullPointerException() {
        // Given
        String tenantId = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingServiceImp.saveByTenantId(clientTracking, null).block();
        });

        assertEquals(nullPointerException.getMessage(), "tenantId is marked non-null but is null");
    }

    @Test
    void injectDataToExistingClientTracking_NullNewClientTracking_ThrowsNullPointerException() {
        // Given
        ClientTracking nullNewClientTracking = null;
        ClientTracking originalClientTracking = clientTracking;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingServiceImp.injectDataToExistingClientTracking(nullNewClientTracking, originalClientTracking).block();
        });

        assertEquals(nullPointerException.getMessage(), "newClientTracking is marked non-null but is null");
    }

    @Test
    void injectDataToExistingClientTracking_NullOriginalClientTracking_ThrowsNullPointerException() {
        // Given
        ClientTracking newClientTracking = clientTracking;
        ClientTracking nullOriginalClientTracking = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingServiceImp.injectDataToExistingClientTracking(newClientTracking, nullOriginalClientTracking).block();
        });

        assertEquals(nullPointerException.getMessage(), "originalClientTracking is marked non-null but is null");
    }

    @Test
    void injectDataToNewClientTracking_NullClientTracking_ThrowsNullPointerException() {
        // Given
        ClientTracking nullClientTracking = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingServiceImp.injectDataToNewClientTracking(nullClientTracking).block();
        });

        assertEquals(nullPointerException.getMessage(), "clientTracking is marked non-null but is null");
    }

    @Test
    void update_NullNewClientTracking_ThrowsNullPointerException() {
        // Given
        ClientTracking nullNewClientTracking = null;
        ClientTracking originalClientTracking = clientTracking;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingServiceImp.update(nullNewClientTracking, originalClientTracking).block();
        });

        assertEquals(nullPointerException.getMessage(), "newClientTracking is marked non-null but is null");
    }

    @Test
    void update_NullOriginalClientTracking_ThrowsNullPointerException() {
        // Given
        ClientTracking newClientTracking = clientTracking;
        ClientTracking nullOriginalClientTracking = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingServiceImp.update(newClientTracking, nullOriginalClientTracking).block();
        });

        assertEquals(nullPointerException.getMessage(), "originalClientTracking is marked non-null but is null");
    }

}