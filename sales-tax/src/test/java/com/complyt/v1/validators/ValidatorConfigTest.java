package com.complyt.v1.validators;

import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.TransactionDto;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
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

    ObjectStub objectStub;

    @BeforeEach
    void setUp() {

        validatorConfig = new ValidatorConfig();
        objectStub = new ObjectStub(new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
    }

    @Test
    void customerDtoValidationHandler_ReturnsValidationHandler() {
        // Given
        ValidationHandler<CustomerDto, SpringValidatorAdapter> customerDtoValidationHandler = validatorConfig.customerDtoValidationHandler(springValidatorAdapter);
        CustomerDto customerDto = objectStub.createCustomerDto(UUID.randomUUID().toString());

        // When
        when(serverRequest.bodyToMono(CustomerDto.class)).thenReturn(Mono.just(customerDto));
        when(serverRequest.pathVariable("source")).thenReturn(customerDto.source());
        when(serverRequest.pathVariable("externalId")).thenReturn("not same external id");

        Mono<CustomerDto> customerDtoMono = customerDtoValidationHandler.validate(serverRequest, "source", "externalId");

        // Then
        StepVerifier.create(customerDtoMono).expectErrorMessage("The requested operation failed because there was an unresolvable conflict between two or more inputs.");
    }

    @Test
    void transactionDtoValidationHandler_ReturnsValidationHandler() {
        // Given
        ValidationHandler<TransactionDto, SpringValidatorAdapter> transactionDtoValidationHandler = validatorConfig.transactionDtoValidationHandler(springValidatorAdapter);
        TransactionDto transactionDto = objectStub.createTransactionDto(UUID.randomUUID().toString());

        // When
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));
        when(serverRequest.pathVariable("source")).thenReturn(transactionDto.source());
        when(serverRequest.pathVariable("externalId")).thenReturn("not same external id");

        Mono<TransactionDto> transactionDtoMono = transactionDtoValidationHandler.validate(serverRequest, "source", "externalId");

        // Then
        StepVerifier.create(transactionDtoMono).expectErrorMessage("The requested operation failed because there was an unresolvable conflict between two or more inputs.");
    }

    @Test
    void exemptionDtoValidationHandler_ReturnsValidationHandler() {
        // Given
        ValidationHandler<ExemptionDto, SpringValidatorAdapter> exemptionDtoValidationHandler = validatorConfig.exemptionDtoValidationHandler(springValidatorAdapter);
        ExemptionDto exemptionDto = objectStub.createExemptionDto();

        // When
        when(serverRequest.bodyToMono(ExemptionDto.class)).thenReturn(Mono.just(exemptionDto));
        when(serverRequest.pathVariable("complytId")).thenReturn("not same external id");

        Mono<ExemptionDto> exemptionDtoMono = exemptionDtoValidationHandler.validate(serverRequest, "complytId");

        // Then
        StepVerifier.create(exemptionDtoMono).expectErrorMessage("The requested operation failed because there was an unresolvable conflict between two or more inputs.");
    }

    @Test
    void salesTaxTrackingDtoValidationHandler_ReturnsValidationHandler() {
        // Given
        ValidationHandler<SalesTaxTrackingDto, SpringValidatorAdapter> salesTaxTrackingDtoValidationHandler = validatorConfig.salesTaxTrackingDtoValidationHandler(springValidatorAdapter);
        SalesTaxTrackingDto salesTaxTrackingDto = objectStub.createSalesTaxTrackingDto();

        // When
        when(serverRequest.bodyToMono(SalesTaxTrackingDto.class)).thenReturn(Mono.just(salesTaxTrackingDto));
        when(serverRequest.pathVariable("state")).thenReturn("not same external id");

        Mono<SalesTaxTrackingDto> salesTaxTrackingDtoMono = salesTaxTrackingDtoValidationHandler.validate(serverRequest, "state");

        // Then
        StepVerifier.create(salesTaxTrackingDtoMono).expectErrorMessage("The requested operation failed because there was an unresolvable conflict between two or more inputs.");
    }
}