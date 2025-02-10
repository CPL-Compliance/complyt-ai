package io.complyt.v1.config;

import io.complyt.v1.models.AddressDto;
import io.complyt.v1.validators.DataConflictChecksProvider;
import io.complyt.v1.validators.ValidationHandler;
import io.complyt.v1.validators.address_body_checks.AddressDtoChecker;
import io.complyt.v1.validators.query_params.QueryParamsExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.List;
import java.util.Map;

@Configuration
public class ValidatorConfig {
    @Bean
    public ValidationHandler<AddressDto, SpringValidatorAdapter> addressDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter,
                                                                                             @Autowired QueryParamsExtractor<AddressDto> addressDtoQueryParamsExtractor) {
        return new ValidationHandler<>(
                AddressDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(
                        new BodyCheckConfig<AddressDto>(List.of(
                                new AddressDtoChecker()))
                                .entityDtoFluxFunction()
                        , Map.of()),
                addressDtoQueryParamsExtractor
        );
    }
}