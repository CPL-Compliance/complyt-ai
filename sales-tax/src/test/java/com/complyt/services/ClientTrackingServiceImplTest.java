package com.complyt.services;

import com.complyt.business.timestamps_injection.InternalTimestampsInjector;
import com.complyt.domain.ClientTracking;
import com.complyt.domain.Nexus;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.repositories.ClientTrackingRepository;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ClientTrackingServiceImplTest {

    @InjectMocks
    ClientTrackingServiceImpl clientTrackingServiceImp;

    @Mock
    ClientTrackingRepository clientTrackingRepository;

    @Mock
    InternalTimestampsInjector<ClientTracking> internalTimestampsInjector;

    private ClientTracking clientTracking;



    @BeforeEach
    void setUp() {
        Nexus nexus = new Nexus(null);
        Timestamps internalTimestamps = new Timestamps(LocalDateTime.now(), LocalDateTime.now());
        clientTracking = new ClientTracking(UUID.randomUUID().toString(), UUID.randomUUID().toString(), nexus, "name", internalTimestamps, null);
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
    void save_NullClientTracking_ThrowsException() {
        // Given
        ClientTracking nullId = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingServiceImp.save(nullId);
        });

        assertEquals(nullPointerException.getMessage(), "clientTracking is marked non-null but is null");
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
        when(clientTrackingRepository.saveWithoutTenant(clientTracking.withTenantId(tenantId))).thenReturn(Mono.just(clientTracking));

        Mono<ClientTracking> clientTrackingMono = clientTrackingServiceImp.saveByTenantId(clientTracking, tenantId);

        // Then
        StepVerifier.create(clientTrackingMono).expectNext(clientTracking).verifyComplete();
    }

    @Test
    void injectDataToExistingClientTracking_InjectsData_ReturnsInjectedClientTracking() {
        // Given
        ClientTracking anotherClientTracking = clientTracking.withName("changedName");
        LocalDateTime now = LocalDateTime.now();
        Timestamps internalTimestamps = new Timestamps(clientTracking.getInternalTimestamps().getCreatedDate(),now);
        ClientTracking clientTrackingWithUpdatedDates = clientTracking.withInternalTimestamps(internalTimestamps);

        // Then
        when(internalTimestampsInjector.insertTimestampsToExisting(clientTracking, anotherClientTracking)).thenReturn(clientTrackingWithUpdatedDates);
        Mono<ClientTracking> clientTrackingMono = clientTrackingServiceImp.injectDataToExistingClientTracking(clientTracking, anotherClientTracking);

        StepVerifier.create(clientTrackingMono).expectNextMatches(clientTracking -> {
            LocalDateTime expectedUpdatedDateTime = clientTrackingWithUpdatedDates.getInternalTimestamps().getUpdatedDate();
            LocalDateTime actualUpdatedDateTime = clientTracking.getInternalTimestamps().getUpdatedDate();

            return expectedUpdatedDateTime.getYear() == actualUpdatedDateTime.getYear() &&
                    expectedUpdatedDateTime.getMonthValue() == actualUpdatedDateTime.getMonthValue() &&
                    expectedUpdatedDateTime.getDayOfYear() == actualUpdatedDateTime.getDayOfYear() &&
                    expectedUpdatedDateTime.getHour() == actualUpdatedDateTime.getHour();
        }).verifyComplete();
    }

    @Test
    void injectDataToNewClientTracking_InjectsData_ReturnsInjectedClientTracking() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Timestamps internalTimestamps = new Timestamps(clientTracking.getInternalTimestamps().getCreatedDate(),now);
        ClientTracking clientTrackingWithUpdatedDates = clientTracking.withInternalTimestamps(internalTimestamps);


        // When + Then
        when(internalTimestampsInjector.insertTimestampsToNew(clientTracking)).thenReturn(clientTrackingWithUpdatedDates);
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
        Timestamps internalTimestamps = new Timestamps(clientTracking.getInternalTimestamps().getCreatedDate(), expectedTime);

        // When + Then
        when(internalTimestampsInjector.insertTimestampsToExisting(newClientTracking, clientTracking)).thenReturn(newClientTracking.withInternalTimestamps(internalTimestamps));
        Mono<ClientTracking> updatedClientTracking = clientTrackingServiceImp.update(newClientTracking, clientTracking);

        StepVerifier.create(updatedClientTracking).expectNextMatches(actualClientTracking -> {
            LocalDateTime actualTime = actualClientTracking.getInternalTimestamps().getUpdatedDate();
            return Objects.equals(actualClientTracking.getName(), newClientTracking.getName()) &&
                    Objects.equals(actualTime.getYear(), expectedTime.getYear()) &&
                    Objects.equals(actualTime.getMonthValue(), expectedTime.getMonthValue()) &&
                    Objects.equals(actualTime.getDayOfYear(), expectedTime.getDayOfYear()) &&
                    Objects.equals(actualTime.getHour(), expectedTime.getHour());
        }).expectComplete().verify();


    }

    @Test
    void findAll_FindsAllClientTrackings_ReturnsClientTrackings() {
        // Given
        int page = 0;
        int size = 10;

        Map<String, String> filterMap = new LinkedHashMap<>();
        String sortOrder = "DESC", sortBy = "externalTimetamps.createdDate";
        List<ClientTracking> clientTrackings = List.of(clientTracking);

        // When + Then
        when(clientTrackingRepository.findAll(page, size)).thenReturn(Flux.fromIterable(clientTrackings));
        Flux<ClientTracking> foundClientTrackings = clientTrackingServiceImp.findAll(page, size, filterMap, sortOrder, sortBy);

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
            clientTrackingServiceImp.getByName(nullName);
        });

        assertEquals(nullPointerException.getMessage(), "name is marked non-null but is null");
    }

    @Test
    void getByTenantId_NullTenantId_ThrowsNullPointerException() {
        // Given
        String nullTenantId = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingServiceImp.getByTenantId(nullTenantId);
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
            clientTrackingServiceImp.saveByTenantId(nullClientTracking, tenantId);
        });

        assertEquals(nullPointerException.getMessage(), "clientTracking is marked non-null but is null");
    }

    @Test
    void saveByTenantId_NullTenantId_ThrowsNullPointerException() {
        // Given
        String tenantId = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingServiceImp.saveByTenantId(clientTracking, null);
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
            clientTrackingServiceImp.injectDataToExistingClientTracking(nullNewClientTracking, originalClientTracking);
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
            clientTrackingServiceImp.injectDataToExistingClientTracking(newClientTracking, nullOriginalClientTracking);
        });

        assertEquals(nullPointerException.getMessage(), "originalClientTracking is marked non-null but is null");
    }

    @Test
    void injectDataToNewClientTracking_NullClientTracking_ThrowsNullPointerException() {
        // Given
        ClientTracking nullClientTracking = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            clientTrackingServiceImp.injectDataToNewClientTracking(nullClientTracking);
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
            clientTrackingServiceImp.update(nullNewClientTracking, originalClientTracking);
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
            clientTrackingServiceImp.update(newClientTracking, nullOriginalClientTracking);
        });

        assertEquals(nullPointerException.getMessage(), "originalClientTracking is marked non-null but is null");
    }

}