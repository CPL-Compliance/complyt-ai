package com.complyt.v1.validators;

import com.complyt.v1.models.customer.CustomerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import static org.junit.jupiter.api.Assertions.*;

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
    void customerDtoValidationHandler() {
        ValidationHandler<CustomerDto, SpringValidatorAdapter> actualCustomerDtoValidationHandler = new ValidationHandler<>(CustomerDto.class, springValidatorAdapter);
        ValidationHandler<CustomerDto, SpringValidatorAdapter> expectedCustomerDtoValidationHandler = validatorConfig.customerDtoValidationHandler(springValidatorAdapter);

        assertEquals(expectedCustomerDtoValidationHandler, actualCustomerDtoValidationHandler);
    }
}