package com.complyt.business.transaction.data_injector;

import com.complyt.domain.transaction.MatchedAddressData;
import com.complyt.domain.transaction.ShippingAddress;
import com.complyt.domain.transaction.Transaction;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.BaseTestClass;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionMatchedAddressInjectorTest extends BaseTestClass {
    @InjectMocks
    TransactionMatchedAddressInjector transactionMatchedAddressInjector;

    Transaction transaction;

    UnitTestUtilities testUtilities;
    MatchedAddressData matchedAddressData;


    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        matchedAddressData = UnitTestUtilities.createMatchedAddressData();
    }

    @Test
    void inject_validMatchedAddressData_returnUpdatedTransaction() {
        // Given
        ShippingAddress modifiedAddress = transaction.getShippingAddress()
                .withMatchedAddressData(matchedAddressData);
        Transaction expectedTransaction = transaction.withShippingAddress(modifiedAddress);

        // When
        

        Mono<Transaction> actualTransactionMono = transactionMatchedAddressInjector.inject(matchedAddressData, transaction);

        // Then
        StepVerifier.create(actualTransactionMono)
                .expectNext(expectedTransaction)
                .verifyComplete();
    }

    @Test
    void inject_nullMatchedAddressData_throwsException() {
        // Given
        MatchedAddressData nullMatchedAddressData = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () ->
                transactionMatchedAddressInjector.inject(nullMatchedAddressData, transaction));

        assertEquals("matchedAddressData is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void inject_nullTransaction_throwsException() {
        // Given
        Transaction nullTransaction = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () ->
                transactionMatchedAddressInjector.inject(matchedAddressData, nullTransaction));

        assertEquals("transaction is marked non-null but is null", nullPointerException.getMessage());
    }
}