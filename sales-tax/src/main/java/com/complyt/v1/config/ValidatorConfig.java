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
import com.complyt.v1.validators.ValidationHandler;
import com.complyt.v1.validators.custom_body.CustomBodyExtractorEmpty;
import com.complyt.v1.validators.custom_body.DateWrapperDtoCustomBodyExtractor;
import com.complyt.v1.validators.param_checker.ComplytIdPathVariableCheckable;
import com.complyt.v1.validators.param_checker.PathVariableChecksProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.nio.file.Path;
import java.util.Map;

@Configuration
public class ValidatorConfig {

    PathVariableChecksProvider pathChecker = new PathVariableChecksProvider(Map.of("complytId", ComplytIdPathVariableCheckable.COMPLYT_ID_CHECK));

    @Bean
    ValidationHandler<CustomerDto, SpringValidatorAdapter> customerDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(CustomerDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(Map.of(
                        "source", CustomerDto.SOURCE_CONFLICT_CHECK,
                        "externalId", CustomerDto.EXTERNAL_ID_CONFLICT_CHECK),
                        null),
                new CustomBodyExtractorEmpty<>(),
                pathChecker);
    }

    @Bean
    ValidationHandler<TransactionDto, SpringValidatorAdapter> transactionDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(TransactionDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(Map.of(
                        "source", TransactionDto.SOURCE_CONFLICT_CHECK,
                        "externalId", TransactionDto.EXTERNAL_ID_CONFLICT_CHECK),
                        BodyCheckConfig.TRANSACTION_BODY_CHECK),
                new CustomBodyExtractorEmpty<>(),
                pathChecker);
    }

    @Bean
    ValidationHandler<ExemptionDto, SpringValidatorAdapter> exemptionDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(ExemptionDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(Map.of(
                        "complytId", ComplytIdCheckable.COMPLYT_ID_CONFLICT_CHECK),
                        null),
                new CustomBodyExtractorEmpty<>(),
                pathChecker);
    }

    @Bean
    ValidationHandler<SalesTaxTrackingDto, SpringValidatorAdapter> salesTaxTrackingDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(SalesTaxTrackingDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(Map.of(
                        "state", StateCheckable.STATE_CONFLICT_CHECK),
                        null), new CustomBodyExtractorEmpty<>(),
                pathChecker);

    }

    @Bean
    ValidationHandler<DateWrapperDto, SpringValidatorAdapter> dateWrapperDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(DateWrapperDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(Map.of(),
                        null), new DateWrapperDtoCustomBodyExtractor(),
                pathChecker);

    }

    @Bean
    ValidationHandler<ExemptionWrapperDto, SpringValidatorAdapter> exemptionWrapperDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(ExemptionWrapperDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(Map.of(),
                        null),
                new CustomBodyExtractorEmpty<>(),
                pathChecker);

    }
}
