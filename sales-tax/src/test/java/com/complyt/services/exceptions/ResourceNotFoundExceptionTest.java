package com.complyt.services.exceptions;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class ResourceNotFoundExceptionTest {

    ResourceNotFoundException resourceNotFoundException;

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