package com.complyt.repositories.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperationFailedExceptionTest {

    OperationFailedException operationFailedException;

    @BeforeEach
    void setup() {
        operationFailedException = new OperationFailedException();
    }

    @Test
    void constructor_WithoutParams_ExceptionThrown() {
        // Given + When
        OperationFailedException actualException = assertThrows(OperationFailedException.class, () -> {
            throw new OperationFailedException();
        });

        // Then
        assertNull(actualException.getMessage());
    }

    @Test
    void constructor_WithNewMessage_ExceptionThrown() {
        // Given + When
        OperationFailedException actualException = assertThrows(OperationFailedException.class, () -> {
            throw new OperationFailedException("this is an operation failed exception");
        });

        // Then
        assertEquals("this is an operation failed exception", actualException.getMessage());
    }


    @Test
    void constructor_WithNewMessageAndThrowableDetails_ExceptionThrown() {
        // Given + When
        OperationFailedException actualException = assertThrows(OperationFailedException.class, () -> {
            throw new OperationFailedException("this is an operation failed exception", operationFailedException);
        });

        // Then
        assertEquals("this is an operation failed exception", actualException.getMessage());
    }

    @Test
    void constructor_WithThrowableDetails_ExceptionThrown() {
        // Given + When
        OperationFailedException actualException = assertThrows(OperationFailedException.class, () -> {
            throw new OperationFailedException(operationFailedException);
        });

        // Then
        assertEquals("com.complyt.repositories.exceptions.OperationFailedException", actualException.getMessage());
    }
}