package com.complyt.v1.config;

import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.v1.model.AddressWithDateDto;
import com.complyt.v1.model.gt.GtAddressDto;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalSalesTaxRatesDto;
import com.complyt.v1.validators.DataConflictChecksProvider;
import com.complyt.v1.validators.ParameterChecksProvider;
import com.complyt.v1.validators.ShouldCallValidate;
import com.complyt.v1.validators.ValidationHandler;
import com.complyt.v1.validators.body_checkers.AddressDtoChecker;
import com.complyt.v1.validators.body_checkers.InternalSalesTaxRatesDtoChecker;
import com.complyt.v1.validators.param_checker.ParamCheckerFunctions;
import com.complyt.v1.validators.query_params.AddressDtoQueryParamsExtractor;
import com.complyt.v1.validators.query_params.GtAddressQueryParamsExtractor;
import com.complyt.v1.validators.query_params.QueryParamsExtractorEmpty;
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

    ParameterChecksProvider queryParamChecker = new ParameterChecksProvider(Map.of(
            "status", ParamCheckerFunctions.STATUS_CHECK));

    ShouldCallValidate shouldCallValidate = new ShouldCallValidate(Map.of(
            HttpMethod.PUT, "^/v1/sales_tax_rates$"));


    @Bean
    public ValidationHandler<AddressWithDateDto, SpringValidatorAdapter> addressDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {

        return new ValidationHandler<>(
                AddressWithDateDto.class,
                springValidatorAdapter,
                new DataConflictChecksProvider(new BodyCheckConfig<AddressWithDateDto>(List.of(
                        new AddressDtoChecker()
                )).entityDtoFluxFunction(), Map.of()),
                new AddressDtoQueryParamsExtractor(),
                pathVariableChecker,
                queryParamChecker,
                shouldCallValidate
        );
    }

    @Bean
    public ValidationHandler<GtAddressDto, SpringValidatorAdapter> gtAddressDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(
                GtAddressDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(new BodyCheckConfig<InternalSalesTaxRatesDto>(List.of()).entityDtoFluxFunction(), Map.of()),
                new GtAddressQueryParamsExtractor(),
                pathVariableChecker,
                queryParamChecker,
                shouldCallValidate
        );
    }

    @Bean
    public ValidationHandler<InternalSalesTaxRatesDto, SpringValidatorAdapter> internalRatesDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(
                InternalSalesTaxRatesDto.class,
                springValidatorAdapter,
                new DataConflictChecksProvider(new BodyCheckConfig<InternalSalesTaxRates>(List.of(new InternalSalesTaxRatesDtoChecker())).entityDtoFluxFunction(), Map.of()),
                new QueryParamsExtractorEmpty<>(),
                pathVariableChecker,
                queryParamChecker,
                shouldCallValidate
        );
    }
}