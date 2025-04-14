package com.complyt.repositories.exceptions;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class OperationFailedExceptionTest {

    OperationFailedException operationFailedException;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

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