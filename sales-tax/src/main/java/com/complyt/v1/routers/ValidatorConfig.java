package com.complyt.v1.routers;

import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.validators.ValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Configuration
public class ValidatorConfig {
    @Bean
    ValidationHandler<CustomerDto, SpringValidatorAdapter> customerDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter){
        ValidationHandler<CustomerDto, SpringValidatorAdapter> validationHandler = new ValidationHandler<>(CustomerDto.class, springValidatorAdapter);

        return validationHandler;
    }
}
