package com.complyt.v1.validators;

import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.TransactionDto;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.models.properties.ComplytIdCheckable;
import com.complyt.v1.models.properties.ExternalIdCheckable;
import com.complyt.v1.models.properties.SourceCheckable;
import com.complyt.v1.models.properties.StateCheckable;
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
    ValidationHandler<CustomerDto, SpringValidatorAdapter> customerDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter, @Autowired DataConflictChecksProvider dataConflictChecksProvider) {
        return new ValidationHandler<>(CustomerDto.class, springValidatorAdapter, dataConflictChecksProvider);
    }

    @Bean
    ValidationHandler<TransactionDto, SpringValidatorAdapter> transactionDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter, @Autowired DataConflictChecksProvider dataConflictChecksProvider) {
        return new ValidationHandler<>(TransactionDto.class, springValidatorAdapter, dataConflictChecksProvider);
    }

    @Bean
    ValidationHandler<ExemptionDto, SpringValidatorAdapter> exemptionDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter, @Autowired DataConflictChecksProvider dataConflictChecksProvider) {
        return new ValidationHandler<>(ExemptionDto.class, springValidatorAdapter, dataConflictChecksProvider);
    }

    @Bean
    ValidationHandler<SalesTaxTrackingDto, SpringValidatorAdapter> salesTaxTrackingDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter, @Autowired DataConflictChecksProvider dataConflictChecksProvider) {
        return new ValidationHandler<>(SalesTaxTrackingDto.class, springValidatorAdapter, dataConflictChecksProvider);
    }

    @Bean
    DataConflictChecksProvider dataConflictChecksProvider() {
        Map<String, BiFunction<?, ServerRequest, Mono<Boolean>>> map = new HashMap<>();
        map.put("state", StateCheckable.STATE_CONFLICT_CHECK);
        map.put("source", SourceCheckable.SOURCE_CONFLICT_CHECK);
        map.put("externalId", ExternalIdCheckable.EXTERNAL_ID_CONFLICT_CHECK);
        map.put("complytId", ComplytIdCheckable.COMPLYT_ID_CONFLICT_CHECK);
        return new DataConflictChecksProvider(map);
    }
}
