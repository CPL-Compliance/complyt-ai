package com.complyt.v1.validators;

import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.v1.models.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

@SpringBootTest()
class TransactionValidationHandlerTest {

    @Autowired
    SpringValidatorAdapter springValidatorAdapter;

    @Autowired
    ValidationHandler<TransactionDto, SpringValidatorAdapter> transactionDtoValidationHandler;

    @MockBean
    ServerRequest serverRequest;
    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
    }

    @Test
    void validate_validTransaction_returnsTransactionDto() {
        TransactionDto transactionDto = objectStub.createTransactionDto(UUID.randomUUID().toString());
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));
        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.validateRequestBody(serverRequest);

        StepVerifier.create(validationMono).expectNext(transactionDto).verifyComplete();
    }

    @Test
    void validate_invalidTransactionDto_returnsError() {
        TransactionDto transactionDto = objectStub.createTransactionDto(UUID.randomUUID().toString()).withShippingAddress(null);
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));
        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.validateRequestBody(serverRequest);

        StepVerifier.create(validationMono).expectError().verify();
    }
}