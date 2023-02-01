package com.complyt.v1.validators;

import com.complyt.v1.models.TransactionDto;
import com.complyt.v1.models.customer.CustomerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Configuration
public class ValidatorConfig {
    @Bean
    ValidationHandler<CustomerDto, SpringValidatorAdapter> customerDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(CustomerDto.class, springValidatorAdapter);
    }

    @Bean
    ValidationHandler<TransactionDto, SpringValidatorAdapter> transactionDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(TransactionDto.class, springValidatorAdapter);
    }
}
