package com.complyt.v1.config;

import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.checkables.ComplytIdCheckable;
import com.complyt.v1.models.checkables.StateCheckable;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.models.customer.exemption.ExemptionWrapperDto;
import com.complyt.v1.models.nexus.DateWrapperDto;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.validators.DataConflictChecksProvider;
import com.complyt.v1.validators.ParameterChecksProvider;
import com.complyt.v1.validators.ShouldCallValidate;
import com.complyt.v1.validators.ValidationHandler;
import com.complyt.v1.validators.body_checkers.transaction.ItemsAlignmentChecker;
import com.complyt.v1.validators.body_checkers.transaction.NegativeItemsNotHavingDiscountChecker;
import com.complyt.v1.validators.body_checkers.transaction.TransactionDtoShippingAddressChecker;
import com.complyt.v1.validators.body_checkers.transaction.TransactionTotalAmountChecker;
import com.complyt.v1.validators.custom_body.CustomBodyExtractorEmpty;
import com.complyt.v1.validators.custom_body.DateWrapperDtoCustomBodyExtractor;
import com.complyt.v1.validators.param_checker.ParamCheckerFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.List;
import java.util.Map;

@Configuration
public class ValidatorConfig {

    ParameterChecksProvider pathVariableChecker = new ParameterChecksProvider(Map.of(
            "complytId", ParamCheckerFunctions.UUID_CHECK,
            "source", ParamCheckerFunctions.SOURCE_CHECK,
            "externalId", ParamCheckerFunctions.EXTERNAL_ID_NOT_NULL_CHECK,
            "state", ParamCheckerFunctions.STATE_CHECK));

    ParameterChecksProvider queryParamChecker = new ParameterChecksProvider(Map.of(
            "page", ParamCheckerFunctions.PAGE_CHECK,
            "size", ParamCheckerFunctions.SIZE_CHECK,
            "date", ParamCheckerFunctions.DATE_CHECK));

    ShouldCallValidate shouldCallValidate = new ShouldCallValidate(Map.of(
            HttpMethod.PUT, "^/v1/transactions/source/[^/]+/externalId/[^/]+$|"
                    + "^/v1/customers/source/[^/]+/externalId/[^/]+$|"
                    + "^/v1/exemptions/complytId/[^/]+$|"
                    + "^/v1/nexus/state/[^/]+$",
            HttpMethod.POST, "^/v1/nexus/refresh/state/[^/]+$|"
                    + "^/v1/exemptions$"));

    @Bean
    ValidationHandler<CustomerDto, SpringValidatorAdapter> customerDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(CustomerDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(Map.of(
                        "source", CustomerDto.SOURCE_CONFLICT_CHECK,
                        "externalId", CustomerDto.EXTERNAL_ID_CONFLICT_CHECK),
                                                          null),
                new CustomBodyExtractorEmpty<>(),
                pathVariableChecker,
                queryParamChecker,
                shouldCallValidate);
    }

    @Bean
    ValidationHandler<TransactionDto, SpringValidatorAdapter> transactionDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(TransactionDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(Map.of(
                        "source", TransactionDto.SOURCE_CONFLICT_CHECK,
                        "externalId", TransactionDto.EXTERNAL_ID_CONFLICT_CHECK),
                        new BodyCheckConfig(List.of(
                                new TransactionDtoShippingAddressChecker(),
                                new TransactionTotalAmountChecker(),
                                new ItemsAlignmentChecker(),
                                new NegativeItemsNotHavingDiscountChecker()
                        )).transactionDtoFluxFunction()),
                new CustomBodyExtractorEmpty<>(),
                pathVariableChecker,
                queryParamChecker,
                shouldCallValidate);
    }

    @Bean
    ValidationHandler<ExemptionDto, SpringValidatorAdapter> exemptionDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(ExemptionDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(Map.of(
                        "complytId", ComplytIdCheckable.COMPLYT_ID_CONFLICT_CHECK),
                        null),
                new CustomBodyExtractorEmpty<>(),
                pathVariableChecker,
                queryParamChecker,
                shouldCallValidate);
    }

    @Bean
    ValidationHandler<SalesTaxTrackingDto, SpringValidatorAdapter> salesTaxTrackingDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(SalesTaxTrackingDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(Map.of(
                        "state", StateCheckable.STATE_CONFLICT_CHECK),
                        null), new CustomBodyExtractorEmpty<>(),
                pathVariableChecker,
                queryParamChecker,
                shouldCallValidate);

    }

    @Bean
    ValidationHandler<DateWrapperDto, SpringValidatorAdapter> dateWrapperDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(DateWrapperDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(Map.of(),
                        null), new DateWrapperDtoCustomBodyExtractor(),
                pathVariableChecker,
                queryParamChecker,
                shouldCallValidate);

    }

    @Bean
    ValidationHandler<ExemptionWrapperDto, SpringValidatorAdapter> exemptionWrapperDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(ExemptionWrapperDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(Map.of(),
                        null),
                new CustomBodyExtractorEmpty<>(),
                pathVariableChecker,
                queryParamChecker,
                shouldCallValidate);

    }
}
