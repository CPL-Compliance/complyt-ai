package io.complyt.utils.exceptions.types;

import io.complyt.v1.config.error_messages.GenericErrorMessages;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZipCodeMismatchExceptionTest {

    @Test
    void testZipCodeMismatchExceptionMessage() {
        // Arrange
        String providedZip = "12345";
        String correctZip = "67890";
        String address = "Test Address";

        // Expected message format with 400 BAD_REQUEST
        String expectedMessage = "400 BAD_REQUEST \"" + String.format(GenericErrorMessages.ZIP_CODE_MISMATCH, providedZip, correctZip, address) + "\"";

        // Act
        ZipCodeMismatchException exception = new ZipCodeMismatchException(providedZip, correctZip, address);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }
}