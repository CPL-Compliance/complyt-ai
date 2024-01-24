package com.complyt.v1.config;

import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.validators.DataConflictChecksProvider;
import com.complyt.v1.validators.ValidationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ValidatorConfigTest {

    private static ValidatorConfig validatorConfig;

    @MockBean
    ServerRequest serverRequest;

    @MockBean
    SpringValidatorAdapter springValidatorAdapter;

    @MockBean
    DataConflictChecksProvider dataConflictChecksProvider;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {

        validatorConfig = new ValidatorConfig();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
    }

    @Test
    void customerDtoValidationHandler_ReturnsValidationHandler() {
        // Given
        ValidationHandler<CustomerDto, SpringValidatorAdapter> customerDtoValidationHandler = validatorConfig.customerDtoValidationHandler(springValidatorAdapter);
        CustomerDto customerDto = testUtilities.createCustomerDto(UUID.randomUUID().toString());
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("externalId", customerDto.externalId());
        pathVariables.put("source", customerDto.source());
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();


        // When
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.bodyToMono(CustomerDto.class)).thenReturn(Mono.just(customerDto));
        when(serverRequest.pathVariable("source")).thenReturn(customerDto.source());
        when(serverRequest.pathVariable("externalId")).thenReturn("not same external id");

        Mono<CustomerDto> customerDtoMono = customerDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(customerDtoMono).expectErrorMessage(GenericErrorMessages.DATA_CONFLICT_ERROR);
    }

    @Test
    void transactionDtoValidationHandler_ReturnsValidationHandler() {
        // Given
        ValidationHandler<TransactionDto, SpringValidatorAdapter> transactionDtoValidationHandler = validatorConfig.transactionDtoValidationHandler(springValidatorAdapter);
        TransactionDto transactionDto = testUtilities.createTransactionDto(UUID.randomUUID().toString());
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("externalId", transactionDto.externalId());
        pathVariables.put("source", transactionDto.source());
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();


        // When
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));
        when(serverRequest.pathVariable("source")).thenReturn(transactionDto.source());
        when(serverRequest.pathVariable("externalId")).thenReturn("not same external id");

        Mono<TransactionDto> transactionDtoMono = transactionDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(transactionDtoMono).expectErrorMessage(GenericErrorMessages.DATA_CONFLICT_ERROR);
    }
//    @Test //todo: fix
//    void transactionDtoValidationHandler_ReturnsValidationHandler() {
//        // Given
//        ValidationHandler<TransactionDto, SpringValidatorAdapter> transactionDtoValidationHandler = validatorConfig.transactionDtoValidationHandler(springValidatorAdapter);
//        TransactionDto transactionDto = testUtilities.createTransactionDto(UUID.randomUUID().toString());
//        Map<String, String> pathVariables = new HashMap<>();
//        pathVariables.put("externalId", transactionDto.externalId());
//        pathVariables.put("source", transactionDto.source());
//
//        // When
//        when(serverRequest.pathVariables()).thenReturn(pathVariables);
//        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));
//        when(serverRequest.pathVariable("source")).thenReturn(transactionDto.source());
//        when(serverRequest.pathVariable("externalId")).thenReturn("not same external id");
//
//        Mono<TransactionDto> transactionDtoMono = transactionDtoValidationHandler.validate(serverRequest);
//
//        // Then
//        StepVerifier.create(transactionDtoMono).expectErrorMessage(GenericErrorMessages.DATA_CONFLICT_ERROR);
//    }

    @Test
    void exemptionDtoValidationHandler_ReturnsValidationHandler() {
        // Given
        ValidationHandler<ExemptionDto, SpringValidatorAdapter> exemptionDtoValidationHandler = validatorConfig.exemptionDtoValidationHandler(springValidatorAdapter);
        ExemptionDto exemptionDto = testUtilities.createExemptionDto();
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("complytId", exemptionDto.complytId().toString());
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();


        // When
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.bodyToMono(ExemptionDto.class)).thenReturn(Mono.just(exemptionDto));
        when(serverRequest.pathVariable("complytId")).thenReturn("not same external id");

        Mono<ExemptionDto> exemptionDtoMono = exemptionDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(exemptionDtoMono).expectErrorMessage(GenericErrorMessages.DATA_CONFLICT_ERROR);
    }

    @Test
    void salesTaxTrackingDtoValidationHandler_ReturnsValidationHandler() {
        // Given
        ValidationHandler<SalesTaxTrackingDto, SpringValidatorAdapter> salesTaxTrackingDtoValidationHandler = validatorConfig.salesTaxTrackingDtoValidationHandler(springValidatorAdapter);
        SalesTaxTrackingDto salesTaxTrackingDto = testUtilities.createSalesTaxTrackingDto();
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("state", salesTaxTrackingDto.state().name());
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();


        // When
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.bodyToMono(SalesTaxTrackingDto.class)).thenReturn(Mono.just(salesTaxTrackingDto));
        when(serverRequest.pathVariable("state")).thenReturn("not same external id");

        Mono<SalesTaxTrackingDto> salesTaxTrackingDtoMono = salesTaxTrackingDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(salesTaxTrackingDtoMono).expectErrorMessage(GenericErrorMessages.DATA_CONFLICT_ERROR);
    }
}