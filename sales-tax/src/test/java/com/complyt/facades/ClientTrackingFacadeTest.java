package com.complyt.facades;

import com.complyt.domain.ClientTracking;
import com.complyt.domain.customer.Customer;
import com.complyt.services.ClientTrackingService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientTrackingFacadeTest {

    @InjectMocks
    ClientTrackingFacade clientTrackingFacade;

    @Mock
    ClientTrackingService clientTrackingService;

    ClientTracking clientTracking;
    UnitTestUtilities testUtilities;

    @BeforeAll
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        String tenantId = "org_12345";
        clientTracking = testUtilities.createClientTracking(tenantId);
    }

    @Test
    void getAllClientTracking_ClientTrackingFound_ClientTrackingFound() {
        // Given
        ClientTracking secondClientTracking = clientTracking.withTenantId("org_B");
        List<ClientTracking> allClientTrackings = Arrays.asList(clientTracking, secondClientTracking);

        // When
        when(clientTrackingService.findAll(0, allClientTrackings.size())).thenReturn(Flux.fromIterable(allClientTrackings));
        Flux<ClientTracking> returnedClientTracking = clientTrackingFacade.getAll(0, allClientTrackings.size());

        // Then
        StepVerifier.create(returnedClientTracking).expectNextCount(2).verifyComplete();
    }

    @Test
    void getByTenantId_NullTenantIdPassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> clientTrackingFacade.getByTenantId(null));

        // Then
        assertEquals(nullPointerException.getMessage(), "tenantId is marked non-null but is null");
    }

    @Test
    void getByTenantId_ClientTrackingFound_ReturnClientTracking() {
        // Given
        String tenantId = "org_12345";

        // When
        when(clientTrackingService.getByTenantId(tenantId)).thenReturn(Mono.just(clientTracking));
        Mono<ClientTracking> returnedClientTracking = clientTrackingFacade.getByTenantId(tenantId);

        // Then
        StepVerifier.create(returnedClientTracking)
                .expectNext(clientTracking)
                .verifyComplete();
    }

    @Test
    void getByTenantId_ClientTrackingNotFound_ReturnEmpty() {
        // Given
        String tenantId = "nonexistent_tenant_id";

        // When
        when(clientTrackingService.getByTenantId(tenantId)).thenReturn(Mono.empty());
        Mono<ClientTracking> returnedClientTracking = clientTrackingFacade.getByTenantId(tenantId);

        // Then
        StepVerifier.create(returnedClientTracking)
                .verifyComplete();
    }

    @Test
    void getByName_NullNamePassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> clientTrackingFacade.getByName(null));

        // Then
        assertEquals(nullPointerException.getMessage(), "name is marked non-null but is null");
    }

    @Test
    void getByName_ClientTrackingFound_ReturnClientTrackings() {
        // Given
        String name = "clientName";
        List<ClientTracking> clientTrackings = Collections.singletonList(clientTracking);

        // When
        when(clientTrackingService.getByName(name)).thenReturn(Flux.fromIterable(clientTrackings));
        Flux<ClientTracking> returnedClientTrackings = clientTrackingFacade.getByName(name);

        // Then
        StepVerifier.create(returnedClientTrackings).expectNext(clientTracking).verifyComplete();
    }

    @Test
    void saveClientTracking_NullClientTrackingPassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> clientTrackingFacade.saveClientTracking(null, "org_12345"));

        // Then
        assertEquals(nullPointerException.getMessage(), "clientTracking is marked non-null but is null");
    }

    @Test
    void saveClientTracking_NullTenantIdPassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> clientTrackingFacade.saveClientTracking(clientTracking, null));

        // Then
        assertEquals(nullPointerException.getMessage(), "tenantId is marked non-null but is null");
    }


    @Test
    void saveClientTracking_ValidData_ReturnSavedClientTracking() {
        // Given
        String tenantId = "org_12345";
        ClientTracking clientTrackingToSave = testUtilities.createClientTracking(tenantId);

        // When
        when(clientTrackingService.injectDataToNewClientTracking(clientTrackingToSave))
                .thenReturn(Mono.just(clientTrackingToSave));
        when(clientTrackingService.saveByTenantId(clientTrackingToSave, tenantId))
                .thenReturn(Mono.just(clientTracking));

        Mono<ClientTracking> savedClientTracking = clientTrackingFacade.saveClientTracking(clientTrackingToSave, tenantId);

        // Then
        StepVerifier.create(savedClientTracking).expectNext(clientTracking).verifyComplete();
    }

    @Test
    void updateIfModified_NullNewClientTrackingPassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> clientTrackingFacade.updateIfModified(null, clientTracking, "org_12345"));

        // Then
        assertEquals(nullPointerException.getMessage(), "newClientTracking is marked non-null but is null");
    }

    @Test
    void updateIfModified_NullOriginalClientTrackingPassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> clientTrackingFacade.updateIfModified(clientTracking, null, "org_12345"));

        // Then
        assertEquals(nullPointerException.getMessage(), "originalClientTracking is marked non-null but is null");
    }

    @Test
    void updateIfModified_NullTenantIdPassed_ThrowsException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> clientTrackingFacade.updateIfModified(clientTracking, clientTracking, null));

        // Then
        assertEquals(nullPointerException.getMessage(), "tenantId is marked non-null but is null");
    }

    @Test
    void updateIfModified_ClientTrackingNotModified_ReturnOriginalClientTracking() {
        // Given
        ClientTracking newClientTracking = clientTracking;
        String tenantId = "org_12345";

        Mono<ClientTracking> updatedClientTracking = clientTrackingFacade.updateIfModified(newClientTracking, clientTracking, tenantId);

        // Then
        StepVerifier.create(updatedClientTracking).expectNext(clientTracking).verifyComplete();
    }

    @Test
    void updateIfModified_ClientTrackingModified_ReturnUpdatedClientTracking() {
        // Given
        ClientTracking newClientTracking = clientTracking.withName("UpdatedName");
        String tenantId = "org_12345";

        // When
        when(clientTrackingService.update(newClientTracking, clientTracking))
                .thenReturn(Mono.just(newClientTracking));
        when(clientTrackingService.saveByTenantId(newClientTracking, tenantId))
                .thenReturn(Mono.just(newClientTracking));

        Mono<ClientTracking> updatedClientTracking = clientTrackingFacade.updateIfModified(newClientTracking, clientTracking, tenantId);

        // Then
        StepVerifier.create(updatedClientTracking).expectNext(newClientTracking).verifyComplete();
    }
}
