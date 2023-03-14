package io.complyt.files.v1.validators;

import io.complyt.files.v1.models.FileDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ValidatorConfigTest {

    private static ValidatorConfig validatorConfig;

    @MockBean
    SpringValidatorAdapter springValidatorAdapter;

    @BeforeEach
    void setUp() {
        validatorConfig = new ValidatorConfig();
    }

    @Test
    void fileDtoValidationHandler() {
        ValidationHandler<FileDto, SpringValidatorAdapter> actualCustomerDtoValidationHandler = new ValidationHandler<>(FileDto.class, springValidatorAdapter);
        ValidationHandler<FileDto, SpringValidatorAdapter> expectedCustomerDtoValidationHandler = validatorConfig.fileDtoValidationHandler(springValidatorAdapter);

        assertEquals(expectedCustomerDtoValidationHandler, actualCustomerDtoValidationHandler);
    }
}