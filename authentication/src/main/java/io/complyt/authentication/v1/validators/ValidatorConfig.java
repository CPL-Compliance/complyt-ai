package io.complyt.authentication.v1.validators;

import io.complyt.authentication.v1.models.ApiKeyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Configuration
public class ValidatorConfig {
    @Bean
    ValidationHandler<ApiKeyDto, SpringValidatorAdapter> fileDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(ApiKeyDto.class, springValidatorAdapter);
    }
}
