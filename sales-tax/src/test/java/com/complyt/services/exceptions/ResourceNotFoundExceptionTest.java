package com.complyt.services.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    ResourceNotFoundException resourceNotFoundException;

    @BeforeEach
    void setup() {
        resourceNotFoundException = new ResourceNotFoundException();
    }

    @Test
    void constructor_WithoutParams_ExceptionThrown() {
        // Given + When
        ResourceNotFoundException actualException = assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException();
        });

        // Then
        assertNull(actualException.getMessage());
    }

    @Test
    void constructor_WithNewMessage_ExceptionThrown() {
        // Given + When
        ResourceNotFoundException actualException = assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException("this is an operation failed exception");
        });

        // Then
        assertEquals("this is an operation failed exception", actualException.getMessage());
    }

    @Test
    void constructor_WithNewMessageAndThrowableDetails_ExceptionThrown() {
        // Given + When
        ResourceNotFoundException actualException = assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException("this is an operation failed exception", resourceNotFoundException);
        });

        // Then
        assertEquals("this is an operation failed exception", actualException.getMessage());
    }

    @Test
    void constructor_WithThrowableDetails_ExceptionThrown() {
        // Given + When
        ResourceNotFoundException actualException = assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException(resourceNotFoundException);
        });

        // Then
        assertEquals("com.complyt.services.exceptions.ResourceNotFoundException", actualException.getMessage());
    }
}