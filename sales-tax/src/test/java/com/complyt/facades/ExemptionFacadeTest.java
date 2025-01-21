package com.complyt.facades;

import com.complyt.domain.State;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionStatus;
import com.complyt.domain.customer.exemption.ExemptionWrapper;
import com.complyt.domain.customer.exemption.Status;
import com.complyt.services.CustomerServiceImpl;
import com.complyt.services.ExemptionServiceImpl;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExemptionFacadeTest {

    @InjectMocks
    ExemptionFacade exemptionFacade;

    @Mock
    ExemptionServiceImpl exemptionService;

    @Mock
    CustomerServiceImpl customerService;

    Customer customer;

    Exemption exemption;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        customer = testUtilities.createCustomer(UUID.randomUUID().toString());
        exemption = testUtilities.createExemption(UUID.randomUUID().toString()).withCustomerId(customer.getComplytId());
    }

    @Test
    void findById_FindsExemption_ReturnsExemption() {
        // Given
        String id = exemption.getId();
        Exemption expectedExemption = exemption.withCustomer(customer);

        // When
        when(exemptionService.findById(id)).thenReturn(Mono.just(exemption));
        when(customerService.findByComplytIdProjection(exemption.getCustomerId())).thenReturn(Mono.just(customer));
        Mono<Exemption> exemptionMono = exemptionFacade.findById(id);

        // Then
        StepVerifier.create(exemptionMono).expectNext(expectedExemption).verifyComplete();
    }

    @Test
    void findById_ExemptionDoesNotExist_ReturnsMonoEmpty() {
        // Given
        String idThatDoesNotExist = UUID.randomUUID().toString();

        // When
        when(exemptionService.findById(idThatDoesNotExist)).thenReturn(Mono.empty());
        Mono<Exemption> exemptionMono = exemptionFacade.findById(idThatDoesNotExist);

        // Then
        StepVerifier.create(exemptionMono).verifyComplete();
    }

    @Test
    void findById_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionFacade.findById(nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

    @Test
    void findAll_FindsTwoExemptions_ReturnsTwoExemptions() {
        // Given
        Exemption secondExemption = exemption.withId(UUID.randomUUID().toString())
                .withState(new State("NY", "05", "New York"));
        List<Exemption> exemptions = new ArrayList<>() {{
            add(exemption);
            add(secondExemption);
        }};

        Map<String, String> filterMap = new LinkedHashMap<>();
        String sortOrder = "DESC", sortBy = "externalTimetamps.createdDate";

        // When
        when(exemptionService.findAll(0, exemptions.size(), filterMap, sortOrder, sortBy)).thenReturn(Flux.fromIterable(exemptions));
        when(customerService.findByComplytIdProjection(exemption.getCustomerId())).thenReturn(Mono.just(customer));
        Flux<Exemption> exemptionFlux = exemptionFacade.findAll(0, exemptions.size(), filterMap, sortOrder, sortBy);

        // Then
        StepVerifier.create(exemptionFlux)
                .expectNext(exemption.withCustomer(customer))
                .expectNext(secondExemption.withCustomer(customer))
                .verifyComplete();
    }

    @Test
    void update_UpdatesExemption_ReturnsUpdatedExemption() {
        // Given
        Exemption newExemption = exemption.withStatus(new Status("new code", "new name"));
        UUID id = exemption.getComplytId();
        Exemption expectedExemption = newExemption.withCustomer(customer);

        // When
        when(exemptionService.update(newExemption, exemption, id)).thenReturn(Mono.just(newExemption));
        when(customerService.findByComplytIdProjection(newExemption.getCustomerId())).thenReturn(Mono.just(customer));
        when(exemptionService.findByComplytId(id)).thenReturn(Mono.just(exemption));
        when(exemptionService.checkComplytIdOfModifiedEqualsToOriginal(newExemption, exemption)).thenReturn(Mono.just(newExemption));
        Mono<Exemption> exemptionMono = exemptionFacade.update(newExemption, id);

        // Then
        StepVerifier.create(exemptionMono).expectNext(expectedExemption).verifyComplete();
    }

    @Test
    void update_ExemptionDoesNotExist_ReturnsMonoEmpty() {
        // Given
        Exemption newExemption = exemption.withStatus(new Status("new code", "new name"));
        UUID idThatDoesNotExist = UUID.randomUUID();

        // When
        when(exemptionService.findByComplytId(idThatDoesNotExist)).thenReturn(Mono.empty());
        Mono<Exemption> exemptionMono = exemptionFacade.update(newExemption, idThatDoesNotExist);

        // Then
        StepVerifier.create(exemptionMono).expectError(ObjectNotFoundApiException.class).verify();
    }

    @Test
    void getByComplytId_ExemptionExists_ReturnsExemption() {
        // Given
        UUID complytId = exemption.getComplytId();
        Exemption expectedExemption = exemption.withCustomer(customer);

        // When
        when(exemptionService.findByComplytId(complytId)).thenReturn(Mono.just(exemption));
        when(customerService.findByComplytIdProjection(exemption.getCustomerId())).thenReturn(Mono.just(customer));
        Mono<Exemption> exemptionMono = exemptionFacade.findByComplytId(complytId);

        // Then
        StepVerifier.create(exemptionMono).expectNext(expectedExemption).verifyComplete();
    }

    @Test
    void delete_DeletesExemption_ReturnsAcknowledgedDeleteResultWithCount1() {
        // Given
        UUID id = UUID.randomUUID();
        Exemption deletedExemption = exemption.withExemptionStatus(ExemptionStatus.CANCELLED);

        // When
        when(exemptionService.markAsCancelled(id)).thenReturn(Mono.just(deletedExemption));
        Mono<Exemption> exemptionMono = exemptionFacade.markAsCancelled(id);

        // Then
        StepVerifier.create(exemptionMono).expectNext(deletedExemption).verifyComplete();
    }

    @Test
    void delete_NoExemptionFoundToDelete_ReturnMonoEmpty() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        when(exemptionService.markAsCancelled(id)).thenReturn(Mono.empty());
        Mono<Exemption> exemptionMono = exemptionFacade.markAsCancelled(id);

        // Then
        StepVerifier.create(exemptionMono).verifyComplete();
    }

    @Test
    void save_UpdatesExemptions_ReturnsExemptions() {
        // Given
        List<State> states = UnitTestUtilities.createStateList();
        ExemptionWrapper exemptionWrapper = new ExemptionWrapper(exemption, states);
        List<Exemption> expectedExemptions = UnitTestUtilities.createExemptionsListFromWrapper(exemptionWrapper);
        List<Exemption> expectedExemptionsWithCustomers = expectedExemptions.stream().map(e -> e.withCustomer(customer)).toList();

        // When
        when(exemptionService.saveMany(exemptionWrapper)).thenReturn(Flux.fromIterable(expectedExemptions));
        when(customerService.findByComplytIdProjection(expectedExemptionsWithCustomers.get(0).getCustomerId())).thenReturn(Mono.just(customer));
        Flux<Exemption> exemptionFlux = exemptionFacade.save(exemptionWrapper);

        // Then
        StepVerifier.create(exemptionFlux)
                .expectNext(expectedExemptionsWithCustomers.get(0))
                .expectNext(expectedExemptionsWithCustomers.get(1))
                .expectNext(expectedExemptionsWithCustomers.get(2))
                .verifyComplete();
    }

    @Test
    void save_NullExemptionWrapperPassed_ThrowsException() {
        // Given
        ExemptionWrapper nullExemptionWrapper = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionFacade.save(nullExemptionWrapper));

        // Then
        assertEquals(nullPointerException.getMessage(), "exemptionWrapper is marked non-null but is null");
    }

    @Test
    void delete_NullIdPassed_ThrowsException() {
        // Given
        UUID nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionFacade.markAsCancelled(nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "complytId is marked non-null but is null");
    }

    @Test
    void update_NullExemptionPassed_ThrowsException() {
        // Given
        Exemption nullExemption = null;
        UUID id = UUID.randomUUID();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionFacade.update(nullExemption, id));

        // Then
        assertEquals(nullPointerException.getMessage(), "exemption is marked non-null but is null");
    }

    @Test
    void update_NullIdPassed_ThrowsException() {
        // Given
        UUID nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionFacade.update(exemption, nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "complytId is marked non-null but is null");
    }

    @Test
    void findByComplytId_NullIdPassed_ThrowsException() {
        // Given
        UUID nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionFacade.findByComplytId(nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "complytId is marked non-null but is null");
    }

}
