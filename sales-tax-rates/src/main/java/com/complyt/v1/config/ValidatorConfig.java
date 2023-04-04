package com.complyt.v1.config;

import com.complyt.v1.model.AddressDto;
import com.complyt.v1.validators.ValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

public class ValidatorConfig {

    @Bean
    ValidationHandler<AddressDto, SpringValidatorAdapter> addressDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(AddressDto.class, springValidatorAdapter);
    }
}
