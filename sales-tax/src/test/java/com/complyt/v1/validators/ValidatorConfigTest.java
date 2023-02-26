package com.complyt.v1.validators;

import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.TransactionDto;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.models.checkables.ComplytIdCheckable;
import com.complyt.v1.models.checkables.ExternalIdCheckable;
import com.complyt.v1.models.checkables.SourceCheckable;
import com.complyt.v1.models.checkables.StateCheckable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ValidatorConfigTest {

    private static ValidatorConfig validatorConfig;

    @MockBean
    SpringValidatorAdapter springValidatorAdapter;

    @MockBean
    DataConflictChecksProvider specialDataConflictChecksBuilder;

    @BeforeEach
    void setUp() {
        validatorConfig = new ValidatorConfig();
    }

    @Test
    void customerDtoValidationHandler_ReturnsValidationHandler() {
        // Given
        ValidationHandler<CustomerDto, SpringValidatorAdapter> actualCustomerDtoValidationHandler = new ValidationHandler<>(CustomerDto.class, springValidatorAdapter, specialDataConflictChecksBuilder);

        // When
        ValidationHandler<CustomerDto, SpringValidatorAdapter> expectedCustomerDtoValidationHandler = validatorConfig.customerDtoValidationHandler(springValidatorAdapter, specialDataConflictChecksBuilder);

        // Then
        assertEquals(expectedCustomerDtoValidationHandler, actualCustomerDtoValidationHandler);
    }

    @Test
    void transactionDtoValidationHandler_ReturnsValidationHandler() {
        // Given
        ValidationHandler<TransactionDto, SpringValidatorAdapter> actualTransactionDtoValidationHandler = new ValidationHandler<>(TransactionDto.class, springValidatorAdapter, specialDataConflictChecksBuilder);

        // When
        ValidationHandler<TransactionDto, SpringValidatorAdapter> expectedTransactionDtoValidationHandler = validatorConfig.transactionDtoValidationHandler(springValidatorAdapter, specialDataConflictChecksBuilder);

        // Then
        assertEquals(expectedTransactionDtoValidationHandler, actualTransactionDtoValidationHandler);
    }

    @Test
    void exemptionDtoValidationHandler_ReturnsValidationHandler() {
        // Given
        ValidationHandler<ExemptionDto, SpringValidatorAdapter> actualexemptionDtoValidationHandler = new ValidationHandler<>(ExemptionDto.class, springValidatorAdapter, specialDataConflictChecksBuilder);

        // When
        ValidationHandler<ExemptionDto, SpringValidatorAdapter> expectedexemptionDtoValidationHandler = validatorConfig.exemptionDtoValidationHandler(springValidatorAdapter, specialDataConflictChecksBuilder);

        // Then
        assertEquals(expectedexemptionDtoValidationHandler, actualexemptionDtoValidationHandler);
    }

    @Test
    void salesTaxTrackingDtoValidationHandler_ReturnsValidationHandler() {
        // Given
        ValidationHandler<SalesTaxTrackingDto, SpringValidatorAdapter> actualSalesTaxTrackingDtoValidationHandler = new ValidationHandler<>(SalesTaxTrackingDto.class, springValidatorAdapter, specialDataConflictChecksBuilder);

        // When
        ValidationHandler<SalesTaxTrackingDto, SpringValidatorAdapter> expectedSalesTaxTrackingDtoValidationHandler = validatorConfig.salesTaxTrackingDtoValidationHandler(springValidatorAdapter, specialDataConflictChecksBuilder);

        // Then
        assertEquals(expectedSalesTaxTrackingDtoValidationHandler, actualSalesTaxTrackingDtoValidationHandler);
    }

    @Test
    void dataConflictChecksProvider_ReturnsValidationHandler() {
        // Given
        Map<String, BiFunction<?, ServerRequest, Mono<Boolean>>> map = new HashMap<>();
        map.put("state", StateCheckable.STATE_CONFLICT_CHECK);
        map.put("source", SourceCheckable.SOURCE_CONFLICT_CHECK);
        map.put("externalId", ExternalIdCheckable.EXTERNAL_ID_CONFLICT_CHECK);
        map.put("complytId", ComplytIdCheckable.COMPLYT_ID_CONFLICT_CHECK);
        DataConflictChecksProvider expectedDataConflictChecksProvider = new DataConflictChecksProvider(map);

        // When
        DataConflictChecksProvider actualDataConflictChecksProvider = validatorConfig.dataConflictChecksProvider();

        // Then
        assertEquals(expectedDataConflictChecksProvider,actualDataConflictChecksProvider);
    }
}