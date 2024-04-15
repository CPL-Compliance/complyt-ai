package com.complyt.business.transaction.data_injector;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionDataInjectorTest {

    @Test
    void shouldInject_ReturnTrue() {
        // Given
        Object genericObject = new Object();
        TransactionDataInjector<Object> genericDataInjector = (t, s) -> null;

        // When
        boolean shouldBeInjected = genericDataInjector.shouldInject(genericObject, null);

        // Then
        assertTrue(shouldBeInjected);
    }
}