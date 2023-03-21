package com.complyt.v1.config;

import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.TransactionDto;
import com.complyt.v1.models.checkables.ComplytIdCheckable;
import com.complyt.v1.models.checkables.StateCheckable;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.validators.DataConflictChecksProvider;
import com.complyt.v1.validators.ValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Configuration
public class ValidatorConfig {

    @Bean
    ValidationHandler<CustomerDto, SpringValidatorAdapter> customerDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {

        Map<String, BiFunction<?, ServerRequest, Mono<Boolean>>> map = new HashMap<>();
        map.put("source", CustomerDto.SOURCE_CONFLICT_CHECK);
        map.put("externalId", CustomerDto.EXTERNAL_ID_CONFLICT_CHECK);

        return new ValidationHandler<>(CustomerDto.class, springValidatorAdapter, new DataConflictChecksProvider(map));
    }

    @Bean
    ValidationHandler<TransactionDto, SpringValidatorAdapter> transactionDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {

        Map<String, BiFunction<?, ServerRequest, Mono<Boolean>>> variableConflictChecksMap = new HashMap<>();
        variableConflictChecksMap.put("source", TransactionDto.SOURCE_CONFLICT_CHECK);
        variableConflictChecksMap.put("externalId", TransactionDto.EXTERNAL_ID_CONFLICT_CHECK);

        return new ValidationHandler<>(TransactionDto.class, springValidatorAdapter, new DataConflictChecksProvider(variableConflictChecksMap));
    }

    @Bean
    ValidationHandler<ExemptionDto, SpringValidatorAdapter> exemptionDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {

        Map<String, BiFunction<?, ServerRequest, Mono<Boolean>>> variableConflictChecksMap = new HashMap<>();
        variableConflictChecksMap.put("complytId", ComplytIdCheckable.COMPLYT_ID_CONFLICT_CHECK);

        return new ValidationHandler<>(ExemptionDto.class, springValidatorAdapter, new DataConflictChecksProvider(variableConflictChecksMap));
    }

    @Bean
    ValidationHandler<SalesTaxTrackingDto, SpringValidatorAdapter> salesTaxTrackingDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {

        Map<String, BiFunction<?, ServerRequest, Mono<Boolean>>> variableConflictChecksMap = new HashMap<>();
        variableConflictChecksMap.put("state", StateCheckable.STATE_CONFLICT_CHECK);

        return new ValidationHandler<>(SalesTaxTrackingDto.class, springValidatorAdapter, new DataConflictChecksProvider(variableConflictChecksMap));
    }
}
