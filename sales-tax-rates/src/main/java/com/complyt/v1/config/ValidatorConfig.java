package com.complyt.v1.config;

import com.complyt.v1.model.AddressDto;
import com.complyt.v1.validators.DataConflictChecksProvider;
import com.complyt.v1.validators.ParameterChecksProvider;
import com.complyt.v1.validators.ShouldCallValidate;
import com.complyt.v1.validators.ValidationHandler;
import com.complyt.v1.validators.query_params.QueryParamsExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.Map;

@Configuration
public class ValidatorConfig {

    ParameterChecksProvider pathVariableChecker = new ParameterChecksProvider(Map.of());
    ParameterChecksProvider queryParamChecker = new ParameterChecksProvider(Map.of());

    ShouldCallValidate shouldCallValidate = new ShouldCallValidate(Map.of(
            HttpMethod.GET, "/v1/sales_tax_rates"));

    @Bean
    public ValidationHandler<AddressDto, SpringValidatorAdapter> addressDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter,
                                                                                             @Autowired QueryParamsExtractor addressDtoQueryParamsExtractor) {
        return new ValidationHandler<>(
                AddressDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(BodyCheckConfig.ADDRESS_BODY_CHECK, Map.of()),
                addressDtoQueryParamsExtractor,
                pathVariableChecker,
                queryParamChecker,
                shouldCallValidate
        );
    }
}