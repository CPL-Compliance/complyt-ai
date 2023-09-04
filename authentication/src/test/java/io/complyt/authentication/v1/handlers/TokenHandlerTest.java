package io.complyt.authentication.v1.handlers;

import io.complyt.authentication.facades.TokenFacade;
import io.complyt.authentication.v1.models.ApiKeyDto;
import io.complyt.authentication.v1.validators.ValidationHandler;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class TokenHandlerTest {
    @InjectMocks
    TokenHandler tokenHandler;

    @Mock
    TokenFacade tokenFacade;

    @Mock
    ValidationHandler<ApiKeyDto, SpringValidatorAdapter> apiKeyDtoValidationHandler;

    @Test
    void get_serverRequestIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            tokenHandler.post(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "serverRequest is marked non-null but is null");
    }
}