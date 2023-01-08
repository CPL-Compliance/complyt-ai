package io.complyt.files.v1.exception.handler;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class GlobalErrorAttributesTest {
    @Mock
    private GlobalErrorAttributes globalErrorAttributes;

    private MockServerRequest mockServerRequest;

    @Mock
    private ErrorAttributeOptions options;

    @Disabled
    @Test
    void getErrorAttributes() {
        // Given
        Throwable error = new Throwable("This is a test message");
        mockServerRequest = MockServerRequest.builder().attribute(DefaultErrorAttributes.class.getName() + ".ERROR", "This is an error message").build();
        Map<String, Object> expectedErrorAttributes = new HashMap<>();
        expectedErrorAttributes.put("message", error.getMessage());
        expectedErrorAttributes.put("endpoint url ", mockServerRequest.path());

        // Then
        Map<String, Object> actualErrorAttributes = globalErrorAttributes.getErrorAttributes(mockServerRequest, options);

        assertEquals(expectedErrorAttributes, actualErrorAttributes);
    }
}