package com.complyt.v1.config;

import com.complyt.v1.model.AddressDto;
import com.complyt.v1.validators.DataConflictChecksProvider;
import com.complyt.v1.validators.ValidationHandler;
import com.complyt.v1.validators.query_params.AddressDtoQueryParamsExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.Map;

@Configuration
public class ValidatorConfig {

    @Bean
    public ValidationHandler<AddressDto, SpringValidatorAdapter> addressDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        AddressDtoQueryParamsExtractor addressDtoQueryParamsExtractor = new AddressDtoQueryParamsExtractor();

        return new ValidationHandler<>(
                AddressDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(BodyCheckConfig.ADDRESS_BODY_CHECK, Map.of()),
                addressDtoQueryParamsExtractor
        );
    }
}