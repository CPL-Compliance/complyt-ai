package com.complyt.v1.config;

import com.complyt.v1.model.AddressWithDateDto;
import com.complyt.v1.model.gt.GtAddressDto;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalSalesTaxRatesDto;
import com.complyt.v1.validators.DataConflictChecksProvider;
import com.complyt.v1.validators.ParameterChecksProvider;
import com.complyt.v1.validators.ShouldCallValidate;
import com.complyt.v1.validators.ValidationHandler;
import com.complyt.v1.validators.body_checkers.AddressChecker;
import com.complyt.v1.validators.query_params.QueryParamsExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.List;
import java.util.Map;

@Configuration
public class ValidatorConfig {

    ParameterChecksProvider pathVariableChecker = new ParameterChecksProvider(Map.of());

    ParameterChecksProvider queryParamChecker = new ParameterChecksProvider(Map.of());

    ShouldCallValidate shouldCallValidate = new ShouldCallValidate(Map.of(
            HttpMethod.PUT, "^/v1/sales_tax_rates$"));


    @Bean
    public ValidationHandler<AddressWithDateDto, SpringValidatorAdapter> addressDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter,
                                                                                                     @Autowired QueryParamsExtractor addressDtoQueryParamsExtractor) {

        return new ValidationHandler<>(
                AddressWithDateDto.class,
                springValidatorAdapter,
                new DataConflictChecksProvider(new BodyCheckConfig<AddressWithDateDto>(List.of(
                        new AddressChecker()
                )).entityDtoFluxFunction(), Map.of()),
                addressDtoQueryParamsExtractor,
                pathVariableChecker,
                queryParamChecker,
                shouldCallValidate
        );
    }

    @Bean
    public ValidationHandler<GtAddressDto, SpringValidatorAdapter> gtAddressDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter,
                                                                                                 @Autowired QueryParamsExtractor gtAddressQueryParamsExtractor) {
        return new ValidationHandler<>(
                GtAddressDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(new BodyCheckConfig<InternalSalesTaxRatesDto>(List.of()).entityDtoFluxFunction(), Map.of()),
                gtAddressQueryParamsExtractor,
                pathVariableChecker,
                queryParamChecker,
                shouldCallValidate
        );
    }

    @Bean
    public ValidationHandler<InternalSalesTaxRatesDto, SpringValidatorAdapter> internalRatesDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter,
                                                                                                                 @Autowired QueryParamsExtractor queryParamsExtractorEmpty) {
        return new ValidationHandler<>(
                InternalSalesTaxRatesDto.class,
                springValidatorAdapter,
                new DataConflictChecksProvider(new BodyCheckConfig<AddressWithDateDto>(List.of()).entityDtoFluxFunction(), Map.of()),
                queryParamsExtractorEmpty,
                pathVariableChecker,
                queryParamChecker,
                shouldCallValidate
        );
    }
}