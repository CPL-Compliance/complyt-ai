package com.complyt.business.complyt_id;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class CustomerComplytIdHandlerTest {

    @InjectMocks
    CustomerComplytIdHandler complytIdHandler;
    Customer customer;
    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        customer = objectStub.createCustomer(new ObjectId().toString());
    }

    @Test
    void isComplytIdOfUpdatedEqualsToOld_NewDoesntHaveComplytId_ReturnsNewCustomer() {
        // Given
        Customer newCustomer = customer.withComplytId(null);

        // When
        Mono<Customer> customerMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newCustomer, customer);

        // Then
        StepVerifier.create(customerMono).expectNext(newCustomer).verifyComplete();
    }

    @Test
    void isComplytIdOfUpdatedEqualsToOld_ComplytIdsAreEqual_ReturnsNewCustomer() {
        // Given
        Customer newCustomer = customer.withComplytId(customer.getComplytId());

        // When
        Mono<Customer> customerMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newCustomer, customer);

        // Then
        StepVerifier.create(customerMono).expectNext(newCustomer).verifyComplete();
    }

    @Test
    void isComplytIdOfUpdatedEqualsToOld_ComplytIdsAreNotEqual_ReturnsNewCustomer() {
        // Given
        Customer newCustomer = customer.withComplytId(UUID.randomUUID());

        // When
        Mono<Customer> customerMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newCustomer, customer);

        // Then
        StepVerifier.create(customerMono).expectErrorMessage("400 BAD_REQUEST \"The requested operation failed because there was an unresolvable conflict between two or more inputs.\"").verify();
    }

    @Test
    void isNewDontHaveComplytId_DoesntHaveComplytId_ReturnsNewCustomer() {
        // Given
        Customer newCustomer = customer.withComplytId(null);

        // When
        Mono<Customer> customerMono = complytIdHandler.checkNewDontHaveComplytId(newCustomer);

        // Then
        StepVerifier.create(customerMono).expectNext(newCustomer).verifyComplete();
    }

    @Test
    void isNewDontHaveComplytId_DoesHaveComplytId_ReturnsEmpty() {
        // Given
        Customer newCustomer = customer.withComplytId(UUID.randomUUID());

        // When
        Mono<Customer> customerMono = complytIdHandler.checkNewDontHaveComplytId(newCustomer);

        // Then
        StepVerifier.create(customerMono).expectErrorMessage("400 BAD_REQUEST \"The requested operation failed because there was an unresolvable conflict between two or more inputs.\"").verify();
    }

    @Test
    void insertComplytIdToNew_NewCustomer_ReturnsWithNewComplytId() {
        // Given
        Customer newCustomer = customer.withComplytId(null);

        // When
        Customer actualCustomer = complytIdHandler.insertComplytIdToNew(newCustomer);

        // Then
        assertNotNull(actualCustomer.getComplytId());
    }
}