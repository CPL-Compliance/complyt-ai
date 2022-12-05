package com.complyt.business.transaction.data_injector;

import com.complyt.domain.Transaction;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class TransactionDataInjectorTest {

    @Test
    void shouldInject_ReturnTrue() {
        // Given
        TransactionDataInjector<String> injector = t -> null;

        // When
        boolean expectedBoolean = injector.shouldInject("");

        // Then
        assertTrue(expectedBoolean);
    }
}