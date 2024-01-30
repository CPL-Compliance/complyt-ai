package io.complyt.authentication.v1.handlers;

import io.complyt.authentication.facades.ApiKeyFacade;
import io.complyt.authentication.v1.models.ApiKeyDto;
import io.complyt.authentication.v1.models.CredentialsDto;
import io.complyt.authentication.v1.validators.ValidationHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ApiKeyHandlerTest {
    @InjectMocks
    ApiKeyHandler apiKeyHandler;

    @Mock
    ValidationHandler<CredentialsDto, SpringValidatorAdapter> credentialsDtoValidationHandler;

    @Mock
    ApiKeyFacade apiKeyFacade;

    @Mock
    ValidationHandler<ApiKeyDto, SpringValidatorAdapter> apiKeyDtoValidationHandler;


    @Test
    void post_serverRequestIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            apiKeyHandler.post(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "serverRequest is marked non-null but is null");
    }
}