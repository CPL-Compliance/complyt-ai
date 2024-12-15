package io.complyt.v1.config;

import io.complyt.v1.models.AddressDto;
import io.complyt.v1.validators.DataConflictChecksProvider;
import io.complyt.v1.validators.ValidationHandler;
import io.complyt.v1.validators.address_body_checks.AddressExistingAbbreviationBodyCheck;
import io.complyt.v1.validators.address_body_checks.PartialAddressBodyChecker;
import io.complyt.v1.validators.query_params.QueryParamsExtractor;
import io.complyt.v1.validators.query_params.USZipCodeBodyChecker;
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
                                                                                             @Autowired QueryParamsExtractor addressDtoQueryParamsExtractor) {
        return new ValidationHandler<>(
                AddressDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(
                        new BodyCheckConfig<AddressDto>(List.of(
                                new PartialAddressBodyChecker(),
                                new AddressExistingAbbreviationBodyCheck()))
                                .entityDtoFluxFunction(), Map.of("zip", new USZipCodeBodyChecker())),
                addressDtoQueryParamsExtractor
        );
    }
}